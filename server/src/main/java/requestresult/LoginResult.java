package requestresult;

import com.google.gson.annotations.Expose;

public record LoginResult (
        @Expose String username,
        @Expose String authToken,
        @Expose String message,
        int statusCode) {
}
