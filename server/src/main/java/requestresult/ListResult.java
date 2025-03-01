package requestresult;

import com.google.gson.annotations.Expose;
import dtos.GameSummary;

import java.util.List;

public record ListResult(
        @Expose List<GameSummary> games,
        @Expose String message,
        int statusCode) {
}

