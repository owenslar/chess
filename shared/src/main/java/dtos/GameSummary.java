package dtos;

import com.google.gson.annotations.Expose;
import model.GameData;

public record GameSummary(
        @Expose int gameID,
        @Expose String whiteUsername,
        @Expose String blackUsername,
        @Expose String gameName) {

    public static GameSummary from(GameData gameData) {
        return new GameSummary(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName()
        );
    }
}
