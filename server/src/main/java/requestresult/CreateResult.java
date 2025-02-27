package requestresult;

import com.google.gson.annotations.Expose;

public record CreateResult(
        @Expose Integer gameID,
        @Expose String message,
        int statusCode) {
}
