package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ResponseException extends RuntimeException {

    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String toJson() {
        return new Gson().toJson(new ErrorMessage(getMessage()));
    }

    public static ResponseException fromJson(InputStream stream, int statusCode) {
        var errorMessage = new Gson().fromJson(new InputStreamReader(stream), ErrorMessage.class);
        return new ResponseException(statusCode, errorMessage.message());
    }

    // Inner record
    private record ErrorMessage(String message) {}
}
