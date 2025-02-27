package requestresult;

import com.google.gson.annotations.Expose;

public record CreateResult(
        @Expose String gameID,
        @Expose String message,
        int statusCode) {
}
