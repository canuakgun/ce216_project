package com.example.gamecatalogproject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    public ArrayList<Game> readFromJsonFile(String fileName) {
        ArrayList<Game> result = new ArrayList<>();

        System.out.println("Attempting to load from: " + Paths.get(fileName).toAbsolutePath());

        try {
            String text = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            System.out.println("Successfully read file, content length: " + text.length());

            if (text.trim().startsWith("[")) {
                System.out.println("Detected root-level array format");
                JSONArray arr = new JSONArray(text);
                processGamesArray(arr, result);
            } else {
                System.out.println("Detected object format");
                JSONObject obj = new JSONObject(text);
                if (obj.has("games")) {
                    System.out.println("Found 'games' array in object");
                    JSONArray arr = obj.getJSONArray("games");
                    processGamesArray(arr, result);
                } else {
                    System.out.println("Treating as single game object");
                    Game game = parseGameObject(obj);
                    if (game != null) {
                        result.add(game);
                    }
                }
            }
            System.out.println("Successfully loaded " + result.size() + " games");
        } catch (Exception exception) {
            System.err.println("Error parsing JSON: " + exception.toString());
            exception.printStackTrace();
        }
        return result;
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

    private Game parseGameObject(JSONObject gameObj) {
        try {
            String title = gameObj.getString("title");
            String developer = gameObj.getString("developer");
            String publisher = gameObj.getString("publisher");

            // Handle both "genre" and "genres" for backward compatibility
            JSONArray genresArray = gameObj.has("genres") ?
                    gameObj.getJSONArray("genres") : gameObj.getJSONArray("genre");
            List<String> genres = jsonArrayToList(genresArray);

            JSONArray platformsArray = gameObj.getJSONArray("platforms");
            List<String> platforms = jsonArrayToList(platformsArray);

            // Handle both "steamid" and "steamID" for backward compatibility
            String steamID = gameObj.has("steamID") ?
                    gameObj.getString("steamID") : gameObj.getString("steamid");

            // Handle both "release_year" and "releaseYear" for backward compatibility
            String releaseYear = gameObj.has("releaseYear") ?
                    gameObj.getString("releaseYear") : gameObj.getString("release_year");

            String playtime = gameObj.getString("playtime");

            JSONArray tagsArray = gameObj.getJSONArray("tags");
            List<String> tags = jsonArrayToList(tagsArray);

            return new Game(title, developer, publisher, genres, platforms,
                    steamID, releaseYear, playtime, tags);
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
