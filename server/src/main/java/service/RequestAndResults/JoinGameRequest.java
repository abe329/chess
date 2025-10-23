package service.RequestAndResults;

enum playerColor {
    WHITE,
    BLACK;
}

public record JoinGameRequest(String playerColor, Integer gameID) {
}

// { "playerColor":"WHITE/BLACK", "gameID": 1234 }