package requestresult;

import com.google.gson.annotations.Expose;

public record CreateRequest (String gameName, String authToken) {
}
