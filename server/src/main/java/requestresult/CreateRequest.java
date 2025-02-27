package requestresult;

import com.google.gson.annotations.Expose;

public record CreateRequest (
        @Expose String gameName, String authToken) {
}
