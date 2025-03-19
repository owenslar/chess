package requestresult;

import com.google.gson.annotations.Expose;

public record ClearResult (
        @Expose String message,
        int statusCode) {
}
