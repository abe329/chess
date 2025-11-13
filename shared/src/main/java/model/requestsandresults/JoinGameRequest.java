package model.requestsandresults;

public record JoinGameRequest(String playerColor, Integer gameID) {
}

// { "playerColor":"WHITE/BLACK", "gameID": 1234 }