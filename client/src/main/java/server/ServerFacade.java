package server;

import com.google.gson.Gson;
import exception.ResponseException;
import requestresult.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) { serverUrl = url; }

    public ClearResult clear() throws ResponseException {
        String path = "/db";
        return makeRequest("DELETE", path, null, ClearResult.class, null);
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        String path = "/user";
        return makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        String path = "/session";
        return makeRequest("POST", path, request, LoginResult.class, null);
    }

    public LogoutResult logout(LogoutRequest request) throws ResponseException {
        String path = "/session";
        return makeRequest("DELETE", path, null, LogoutResult.class, request.authToken());
    }

    public ListResult list(ListRequest request) throws ResponseException {
        String path = "/game";
        return makeRequest("GET", path, null, ListResult.class, request.authToken());
    }

    public CreateResult create(CreateRequest request) throws ResponseException {
        String path = "/game";
        String authToken = request.authToken();
        CreateRequest newCreateRequest = new CreateRequest(request.gameName(), null);
        return makeRequest("POST", path, newCreateRequest, CreateResult.class, authToken);
    }

    public JoinResult join(JoinRequest request) throws ResponseException {
        String path = "/game";
        String authToken = request.authToken();
        JoinRequest newJoinRequest = new JoinRequest(null, request.playerColor(), request.gameID());
        return makeRequest("PUT", path, newJoinRequest, JoinResult.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http, authToken);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http, String authToken) throws IOException {
        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }
        if (request != null) {
            http.setRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr, status);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) { return status / 100 == 2; }
}
