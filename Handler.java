import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Handler {
    private GamesCollection collection;
    private JSONParser jsonParser;
    private String jsonFilePath;

    public Handler() {
        this.collection = new GamesCollection();
        this.jsonParser = new JSONParser();
        this.jsonFilePath = "List.json"; // Will be in the working directory

        // Try to load from JAR resources first
        List<Game> games = jsonParser.readFromJsonFile(jsonFilePath);
        if (games != null && !games.isEmpty()) {
            collection.addAll(games);
        }
    }
    // Initialization methods
    public void loadGamesFromJson(String filePath) {
        List<Game> games = jsonParser.readFromJsonFile(filePath);
        collection.addAll(games);
    }

    // Add this new method
    private boolean saveCollectionToFile() {
        return jsonParser.saveToJsonFile(jsonFilePath, collection.getAllGames());
    }

    // Basic operations
    public boolean addGame(Game game) {
        if (game == null || collection.contains(game.getGameTitle())) {
            return false;
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

    // Search operations
    public Game findGameByTitle(String title) {
        return collection.getGame(title);
    }

    public boolean containsGame(String title) {
        return collection.contains(title);
    }

    // Bulk operations
    public int addGamesFromList(List<Game> games) {
        if (games == null) return 0;

        int initialSize = collection.size();
        collection.addAll(games);
        saveCollectionToFile(); // Save after bulk addition
        return collection.size() - initialSize;
    }

    // Filtering operations
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

    // Collection information
    public int getCollectionSize() {
        return collection.size();
    }

    public boolean isCollectionEmpty() {
        return collection.isEmpty();
    }

    // Display methods
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
        if (collection != null) {
            return collection.getAllGames();
        }
        return new ArrayList<>(); // Return empty list if collection is null
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

    // Advanced operations
    public List<Game> searchGames(String query) {
        String lowerQuery = query.toLowerCase();
        return collection.getAllGames().stream()
                .filter(game ->
                        game.getGameTitle().toLowerCase().contains(lowerQuery) ||
                                game.getGameDeveloper().toLowerCase().contains(lowerQuery) ||
                                game.getGamePublisher().toLowerCase().contains(lowerQuery) ||
                                game.getGameGenre().stream().anyMatch(genre -> genre.toLowerCase().contains(lowerQuery)) ||
                                game.getGameTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());
    }

    public JSONParser getJsonParser() {
        return jsonParser;
    }
}
