package com.example.gamecatalogproject;

import org.json.JSONArray;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Handler {
    private GamesCollection collection;
    private JSONParser jsonParser;

    public Handler() {
        this.collection = new GamesCollection();
        this.jsonParser = new JSONParser();
        loadExternalFile();
    }

    private void loadExternalFile() {
        try {
            // Load from the AppData file
            List<Game> games = jsonParser.readFromJsonFile();
            if (games != null && !games.isEmpty()) {
                collection.addAll(games);
            }
        } catch (Exception e) {
            System.err.println("Error loading games from AppData: " + e.getMessage());
        }
    }

    public void loadGamesFromJson(String filePath) {
        try {
            List<Game> games = jsonParser.readFromJsonFile(filePath);
            if (games != null && !games.isEmpty()) {
                collection.addAll(games);
                saveCollectionToFile(); // Save to AppData after loading from custom path
            }
        } catch (Exception e) {
            System.err.println("Error loading games from file: " + e.getMessage());
        }
    }

    private boolean saveCollectionToFile() {
        try {
            return jsonParser.saveToJsonFile(collection.getAllGames());
        } catch (Exception e) {
            System.err.println("Error saving to AppData: " + e.getMessage());
            return false;
        }
    }

    public boolean addGame(Game game) {
        if (game == null) return false;

        for (Game g : collection.getAllGames()) {
            if (g.getGameSteamID().equals(game.getGameSteamID()) ||
                    g.getGameTitle().equalsIgnoreCase(game.getGameTitle())) {
                return false; // Duplicate steamId or title
            }
        }

        collection.add(game);
        return saveCollectionToFile();
    }

    public boolean removeGame(String title) {
        boolean removed = collection.remove(title);
        if (removed) {
            return saveCollectionToFile();
        }
        return false;
    }

    public void clearCollection() {
        collection.clear();
        saveCollectionToFile();
    }

    public Game findGameByTitle(String title) {
        return collection.getGame(title);
    }

    public boolean containsGame(String title) {
        return collection.contains(title);
    }

    public int addGamesFromList(List<Game> games) {
        if (games == null || games.isEmpty()) {
            return 0;
        }

        int addedCount = 0;
        List<Game> uniqueGames = new ArrayList<>();

        for (Game newGame : games) {
            if (newGame == null) {
                System.out.println("Skipping null game in import list");
                continue;
            }

            boolean isDuplicate = collection.getAllGames().stream()
                    .anyMatch(existing ->
                            existing.getGameTitle().equalsIgnoreCase(newGame.getGameTitle()) ||
                                    (newGame.getGameSteamID() != null &&
                                            !newGame.getGameSteamID().isEmpty() &&
                                            newGame.getGameSteamID().equals(existing.getGameSteamID()))
                    );

            if (!isDuplicate) {
                uniqueGames.add(newGame);
                addedCount++;
                System.out.println("Adding new game: " + newGame.getGameTitle());
            } else {
                System.out.println("Skipping duplicate game - Title: " + newGame.getGameTitle() +
                        (newGame.getGameSteamID() != null ? ", SteamID: " + newGame.getGameSteamID() : ""));
            }
        }

        if (!uniqueGames.isEmpty()) {
            collection.addAll(uniqueGames);
            saveCollectionToFile();
            System.out.println("Successfully added " + addedCount + " new games");
        } else {
            System.out.println("No new games to add - all were duplicates");
        }

        return addedCount;
    }

    // Filtering operations (keep existing implementations)
    public List<Game> filterByDeveloper(String developer) {
        return collection.getGamesByDeveloper(developer);
    }

    public List<Game> filterByPublisher(String publisher) {
        return collection.getGamesByPublisher(publisher);
    }

    public List<Game> filterByGenre(String genre) {
        return collection.getGamesByGenre(genre);
    }

    public List<Game> filterByPlatform(String platform) {
        return collection.getGamesByPlatform(platform);
    }

    public List<Game> filterByYear(String year) {
        return collection.getGamesByYear(year);
    }

    // Collection information
    public int getCollectionSize() {
        return collection.size();
    }

    public boolean isCollectionEmpty() {
        return collection.isEmpty();
    }

    public void printAllGames() {
        if (collection.isEmpty()) {
            System.out.println("The collection is empty.");
            return;
        }

        System.out.println("\n=== GAME COLLECTION (" + collection.size() + " games) ===");
        collection.getAllGames().forEach(this::printGameDetails);
        System.out.println("=== END OF COLLECTION ===\n");
    }

    public void printGameDetails(Game game) {
        if (game == null) {
            System.out.println("Game not found.");
            return;
        }

        System.out.println("\nTitle: " + game.getGameTitle());
        System.out.println("Developer: " + game.getGameDeveloper());
        System.out.println("Publisher: " + game.getGamePublisher());
        System.out.println("Genres: " + String.join(", ", game.getGameGenre()));
        System.out.println("Platforms: " + String.join(", ", game.getGamePlatforms()));
        System.out.println("Steam ID: " + game.getGameSteamID());
        System.out.println("Release Year: " + game.getGameReleaseYear());
        System.out.println("Playtime: " + game.getGamePlaytime() + " hours");
        System.out.println("Tags: " + String.join(", ", game.getGameTags()));
    }

    public List<Game> getAllGames() {
        return collection != null ? collection.getAllGames() : new ArrayList<>();
    }

    public void refreshData() {
        collection.clear();
        List<Game> games = jsonParser.readFromJsonFile();
        if (games != null && !games.isEmpty()) {
            collection.addAll(games);
        }
    }

    public List<String> getAllGenres() {
        return collection.getAllGames().stream()
                .flatMap(game -> game.getGameGenre().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllReleaseYears() {
        return collection.getAllGames().stream()
                .map(Game::getGameReleaseYear)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllPublishers() {
        return collection.getAllGames().stream()
                .map(Game::getGamePublisher)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllTags() {
        return collection.getAllGames().stream()
                .flatMap(game -> game.getGameTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllDevelopers() {
        return collection.getAllGames().stream()
                .map(Game::getGameDeveloper)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllPlatforms() {
        return collection.getAllGames().stream()
                .flatMap(game -> game.getGamePlatforms().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void printFilteredResults(String filterType, String filterValue, List<Game> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("No games found with " + filterType + ": " + filterValue);
            return;
        }

        System.out.println("\n=== FILTERED BY " + filterType.toUpperCase() + ": " + filterValue +
                " (" + results.size() + " games) ===");
        results.forEach(this::printGameDetails);
        System.out.println("=== END OF RESULTS ===\n");
    }

    public List<Game> searchGames(String query) {
        String lowerQuery = query.toLowerCase();
        return collection.getAllGames().stream()
                .filter(game ->
                        game.getGameTitle().toLowerCase().contains(lowerQuery) ||
                                game.getGameDeveloper().toLowerCase().contains(lowerQuery) ||
                                game.getGamePublisher().toLowerCase().contains(lowerQuery) ||
                                String.valueOf(game.getGameReleaseYear()).toLowerCase().contains(lowerQuery) ||
                                String.valueOf(game.getGamePlaytime()).toLowerCase().contains(lowerQuery) ||
                                String.valueOf(game.getGameSteamID()).toLowerCase().contains(lowerQuery) ||
                                String.valueOf(game.getGameRating()).toLowerCase().contains(lowerQuery) ||
                                game.getGameGenre().stream().anyMatch(genre -> genre.toLowerCase().contains(lowerQuery)) ||
                                game.getGamePlatforms().stream().anyMatch(platform -> platform.toLowerCase().contains(lowerQuery)) ||
                                game.getGameTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());
    }

    public JSONParser getJsonParser() {
        return jsonParser;
    }

    // Import/Export
    public List<Game> importGamesFromFile(String filePath) {
        try {
            List<Game> importedGames = jsonParser.readFromJsonFile(filePath);
            if (importedGames != null && !importedGames.isEmpty()) {
                int addedCount = addGamesFromList(importedGames);
                if (addedCount > 0) {
                    saveCollectionToFile(); // Save to AppData after importing
                }
            }
            return importedGames;
        } catch (Exception e) {
            System.err.println("Error importing games: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean exportGamesToFile(String filePath, List<Game> games) {
        try {
            return jsonParser.saveToJsonFile(filePath, games);
        } catch (Exception e) {
            System.err.println("Error exporting games: " + e.getMessage());
            return false;
        }
    }
}
