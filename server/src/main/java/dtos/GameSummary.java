package dtos;

import model.GameData;

public record GameSummary(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName) {

    public static GameSummary from(GameData gameData) {
        return new GameSummary(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName()
        );
    }
}
