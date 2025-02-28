package requestresult;

import com.google.gson.annotations.Expose;
import model.GameData;

import java.util.List;

public record ListResult(
        @Expose List<GameData> games,
        @Expose String message,
        int statusCode) {
}

