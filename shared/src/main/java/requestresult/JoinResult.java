package requestresult;

import com.google.gson.annotations.Expose;

public record JoinResult(
        @Expose String message,
        int statusCode) {
}
