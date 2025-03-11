import java.util.List;

public class GamesCollection {
    private List<Game> collection;

    public GamesCollection(List<Game> collection) {
        this.collection = collection;
    }

    public void add(Game game) {
        collection.add(game);
    }

    
    public int indexOfGame(String title) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getGameTitle().equals(title)) {
                return i; 
            }
        }
        return -1; 
    }
}
