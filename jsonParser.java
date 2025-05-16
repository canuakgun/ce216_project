package com.example.gamecatalogproject;


import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    public ArrayList<Game> readFromJsonFile(String fileName) {
        ArrayList<Game> result = new ArrayList<>();

        try {
            // First try to load as resource from JAR
            InputStream is = getClass().getResourceAsStream("/" + fileName);
            String text;

            if (is != null) {
                // Inside JAR
                text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                is.close();
            } else {
                // Outside JAR (development)
                System.out.println("Attempting to load from: " + Paths.get(fileName).toAbsolutePath());
                text = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            }

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
            System.err.println("Error parsing JSON: " + exception.toString());
            exception.printStackTrace();
        }
        return result;
    }

    public boolean saveToJsonFile(String fileName, List<Game> games) {
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
                gameObj.put("rating", game.getGameRating()); // Ensure rating is saved
                jsonArray.put(gameObj);
            }

            // Save to external file
            Files.write(Paths.get(fileName),
                    jsonArray.toString(2).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving to JSON file: " + e.toString());
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
            String rating = gameObj.has("rating") ? gameObj.getString("rating") : "0.0"; // Added rating field


            return new Game(title, developer, publisher, genres, platforms,
                    steamID, releaseYear, playtime, tags,rating);
        } catch (Exception e) {
            System.out.println("Error parsing game object: " + e.toString());
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
