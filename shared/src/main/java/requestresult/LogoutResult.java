package requestresult;

import com.google.gson.annotations.Expose;

public record LogoutResult (
        @Expose String message,
        int statusCode) {
}
