import java.util.ArrayList;
import java.util.List;

public class Handler {
    List<Game> collectionof = new ArrayList<>();
    GamesCollection collection = new GamesCollection(collectionof);

    
    public void addGame(Game game) {
        collection.add(game);
    }

    
    public void printAllGames() {
        System.out.println("All Games in the Collection:");
        for (Game game : collectionof) {
            System.out.println("Title: " + game.getGameTitle());
            System.out.println("Developer: " + game.getGameDeveloper());
            System.out.println("Publisher: " + game.getGamePublisher());
            System.out.println("Genre: " + game.getGameGenre());
            System.out.println("Platforms: " + game.getGamePlatforms());
            System.out.println("Steam ID: " + game.getGameSteamID());
            System.out.println("Release Year: " + game.getGameReleaseYear());
            System.out.println("Playtime: " + game.getGamePlaytime());
            System.out.println("Tags: " + game.getGameTags());
            System.out.println("----------------------------");
        }
    }
}
