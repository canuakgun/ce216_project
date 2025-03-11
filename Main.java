public class Main {
    public static void main(String[] args) {
        Handler handler = new Handler();

        
        Game game1 = new Game("Elden Ring", "FromSoftware", "Bandai Namco Entertainment", "Action RPG", 
                            "PS4, PS5, Xbox One, Xbox Series X/S, PC", "1234567890", "2022", "50+ hours", "Open-World, Fantasy, RPG");
        Game game2 = new Game("The Witcher 3", "CD Projekt Red", "CD Projekt", "Action RPG", 
                            "PS4, PS5, Xbox One, Xbox Series X/S, PC", "2345678901", "2015", "100+ hours", "Open-World, Fantasy, RPG");
        Game game3 = new Game("Cyberpunk 2077", "CD Projekt Red", "CD Projekt", "Action RPG", 
                            "PS4, PS5, Xbox One, Xbox Series X/S, PC", "3456789012", "2020", "50+ hours", "Open-World, Sci-Fi, RPG");
        Game game4 = new Game("Red Dead Redemption 2", "Rockstar Games", "Rockstar Games", "Action-Adventure", 
                            "PS4, PS5, Xbox One, Xbox Series X/S, PC", "4567890123", "2018", "60+ hours", "Open-World, Western, Action");
        Game game5 = new Game("God of War", "Santa Monica Studio", "Sony Interactive Entertainment", "Action-Adventure", 
                            "PS4, PS5, PC", "5678901234", "2018", "30+ hours", "Action, Adventure, Mythology");

        
        handler.addGame(game1);
        handler.addGame(game2);
        handler.addGame(game3);
        handler.addGame(game4);
        handler.addGame(game5);

        
        handler.printAllGames();
    }
}
 // if( user press add game button){
     // open GUI for selection
     // adress them to the JSON File
     // parse JSON FİLE 
     // handler.addGame
    