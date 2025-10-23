package service.requestsandresults;


import model.GameData;

import java.util.List;

public record ListGamesResult(List<GameData> games) { }
