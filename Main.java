package com.example.gamecatalogproject;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        Handler handler = new Handler();

        List<Game> parsedGames = jsonParser.readFromJsonFile("Hey.json");

        if (parsedGames == null || parsedGames.isEmpty()) {
            System.out.println("No games were loaded from the JSON file.");
            return;
        }

        handler.addGamesFromList(parsedGames);

        // Print all games to verify loading
        System.out.println("Successfully loaded " + handler.getCollectionSize() + " games:");
        handler.printAllGames();

        // Example of working with individual games
        if (handler.getCollectionSize() > 0) {
            Game firstGame = handler.findGameByTitle(parsedGames.get(0).getGameTitle());
            if (firstGame != null) {
                System.out.println("\nDetails of first game:");
                handler.printGameDetails(firstGame);
            }
        }
        // filter
   
                
    }
}
