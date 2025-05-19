package com.example.gamecatalogproject;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.StandardCopyOption;

public class JSONParser {
    private static final String APP_DATA_DIR = "GameCatalogProject";
    private static final String JSON_FILE = "list.json";

    private Path getJsonFilePath() {
        String appDataDir = System.getenv("APPDATA");
        if (appDataDir == null || appDataDir.trim().isEmpty()) {
            // back to user.home if APPDATA is not available
            appDataDir = System.getProperty("user.home");
            System.out.println("APPDATA not found, using user.home: " + appDataDir);
        }
        Path path = Paths.get(appDataDir, APP_DATA_DIR, JSON_FILE);
        System.out.println("Using data path: " + path.toString());
        return path;
    }

    private void createDefaultJsonFile(Path jsonPath) throws Exception {
        try {
            System.out.println("Creating new JSON file at: " + jsonPath.toString());
            Files.createDirectories(jsonPath.getParent());
            Files.write(jsonPath, "[]".getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            System.err.println("Failed to create JSON file: " + e.getMessage());
            throw new Exception("Could not create game data file. Please check if you have write permissions.", e);
        }
    }

    public ArrayList<Game> readFromJsonFile() {
        ArrayList<Game> result = new ArrayList<>();

        try {
            Path jsonPath = getJsonFilePath();
            System.out.println("Reading from data path: " + jsonPath.toString());

            if (!Files.exists(jsonPath)) {
                createDefaultJsonFile(jsonPath);
                return result;
            }

            if (!Files.isReadable(jsonPath)) {
                throw new Exception("Cannot read game data file. Please check file permissions.");
            }

            String text = new String(Files.readAllBytes(jsonPath), StandardCharsets.UTF_8);
            if (text.trim().isEmpty()) {
                System.out.println("Empty JSON file found, returning empty list");
                return result;
            }

            System.out.println("Successfully read " + text.length() + " bytes from JSON file");

            try {
                if (text.trim().startsWith("[")) {
                    JSONArray arr = new JSONArray(text);
                    processGamesArray(arr, result);
                } else {
                    JSONObject obj = new JSONObject(text);
                    if (obj.has("games")) {
                        JSONArray arr = obj.getJSONArray("games");
                        processGamesArray(arr, result);
                    } else {
                        Game game = parseGameObject(obj);
                        if (game != null) {
                            result.add(game);
                        }
                    }
                }
                System.out.println("Successfully loaded " + result.size() + " games from JSON");
            } catch (Exception e) {
                System.err.println("Error parsing JSON content: " + e.getMessage());
                throw new Exception("Game data file is corrupted. Please restore from backup or create a new one.", e);
            }
        } catch (Exception exception) {
            System.err.println("Error reading game data: " + exception.getMessage());
            exception.printStackTrace();
        }
        return result;
    }

    public ArrayList<Game> readFromJsonFile(String filePath) {
        ArrayList<Game> result = new ArrayList<>();
        try {
            Path jsonPath = Paths.get(filePath);
            String text = new String(Files.readAllBytes(jsonPath), StandardCharsets.UTF_8);

            if (text.trim().startsWith("[")) {
                JSONArray arr = new JSONArray(text);
                processGamesArray(arr, result);
            } else {
                JSONObject obj = new JSONObject(text);
                if (obj.has("games")) {
                    JSONArray arr = obj.getJSONArray("games");
                    processGamesArray(arr, result);
                } else {
                    Game game = parseGameObject(obj);
                    if (game != null) {
                        result.add(game);
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("Error parsing JSON from file: " + exception.toString());
            exception.printStackTrace();
        }
        return result;
    }

    public boolean saveToJsonFile(List<Game> games) {
        try {
            if (games == null) {
                throw new IllegalArgumentException("Games list cannot be null");
            }

            JSONArray jsonArray = new JSONArray();
            for (Game game : games) {
                if (game == null) {
                    System.out.println("Warning: Skipping null game in save operation");
                    continue;
                }
                JSONObject gameObj = new JSONObject();
                gameObj.put("title", game.getGameTitle());
                gameObj.put("developer", game.getGameDeveloper());
                gameObj.put("publisher", game.getGamePublisher());
                gameObj.put("genres", new JSONArray(game.getGameGenre()));
                gameObj.put("platforms", new JSONArray(game.getGamePlatforms()));
                gameObj.put("steamID", game.getGameSteamID());
                gameObj.put("releaseYear", game.getGameReleaseYear());
                gameObj.put("playtime", game.getGamePlaytime());
                gameObj.put("tags", new JSONArray(game.getGameTags()));
                gameObj.put("rating", game.getGameRating());
                jsonArray.put(gameObj);
            }

            Path jsonPath = getJsonFilePath();
            if (!Files.exists(jsonPath.getParent())) {
                Files.createDirectories(jsonPath.getParent());
            }

           
            if (Files.exists(jsonPath)) {
                Path backupPath = jsonPath.resolveSibling(jsonPath.getFileName() + ".backup");
                Files.copy(jsonPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.write(jsonPath,
                    jsonArray.toString(2).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving to JSON file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveToJsonFile(String filePath, List<Game> games) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Game game : games) {
                JSONObject gameObj = new JSONObject();
                gameObj.put("title", game.getGameTitle());
                gameObj.put("developer", game.getGameDeveloper());
                gameObj.put("publisher", game.getGamePublisher());
                gameObj.put("genres", new JSONArray(game.getGameGenre()));
                gameObj.put("platforms", new JSONArray(game.getGamePlatforms()));
                gameObj.put("steamID", game.getGameSteamID());
                gameObj.put("releaseYear", game.getGameReleaseYear());
                gameObj.put("playtime", game.getGamePlaytime());
                gameObj.put("tags", new JSONArray(game.getGameTags()));
                gameObj.put("rating", game.getGameRating());
                jsonArray.put(gameObj);
            }

            Path jsonPath = Paths.get(filePath);
            Files.createDirectories(jsonPath.getParent());

            Files.write(jsonPath,
                    jsonArray.toString(2).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving to JSON file: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    private void processGamesArray(JSONArray arr, ArrayList<Game> result) {
        for (int i = 0; i < arr.length(); i++) {
            JSONObject gameObj = arr.getJSONObject(i);
            Game game = parseGameObject(gameObj);
            if (game != null) {
                result.add(game);
            }
        }
    }

    public Game parseGameObject(JSONObject gameObj) {
        try {
            String title = gameObj.getString("title");
            String developer = gameObj.getString("developer");
            String publisher = gameObj.getString("publisher");

            JSONArray genresArray = gameObj.has("genres") ?
                    gameObj.getJSONArray("genres") : gameObj.getJSONArray("genre");
            List<String> genres = jsonArrayToList(genresArray);

            JSONArray platformsArray = gameObj.getJSONArray("platforms");
            List<String> platforms = jsonArrayToList(platformsArray);

            String steamID = gameObj.has("steamID") ?
                    gameObj.getString("steamID") : gameObj.getString("steamid");

            String releaseYear = gameObj.has("releaseYear") ?
                    gameObj.getString("releaseYear") : gameObj.getString("release_year");

            String playtime = gameObj.getString("playtime");

            JSONArray tagsArray = gameObj.getJSONArray("tags");
            List<String> tags = jsonArrayToList(tagsArray);
            String rating = gameObj.has("rating") ? gameObj.getString("rating") : "0.0";

            return new Game(title, developer, publisher, genres, platforms,
                    steamID, releaseYear, playtime, tags, rating);
        } catch (Exception e) {
            System.err.println("Error parsing game object: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private List<String> jsonArrayToList(JSONArray array) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
}
