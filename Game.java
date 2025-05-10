package com.example.gamecatalogproject;
import java.util.List;

public class Game {
    private String gameTitle;
    private String gameDeveloper;
    private String gamePublisher;
    private List<String>  gameGenre;
    private List<String> gamePlatforms;
    private String gameSteamID;
    private String gameReleaseYear;
    private String gamePlaytime;
    private List<String> gameTags;
    private String gameRating;
    // for the genres,tags and platforms we can have multiple so we decided to use a ["x","y"] bracket for the multiple variable attributes so that in the parsing part we can easily apart them from others;


    public Game() { }

    public Game(String gameTitle, String gameDeveloper, String gamePublisher,   List<String>  gameGenre,
                List<String> gamePlatforms, String gameSteamID, String gameReleaseYear, String gamePlaytime,
                List<String> gameTags,String gameRating) {
        this.gameTitle = gameTitle;
        this.gameDeveloper = gameDeveloper;
        this.gamePublisher = gamePublisher;
        this.gameGenre = gameGenre;
        this.gamePlatforms = gamePlatforms;
        this.gameSteamID = gameSteamID;
        this.gameReleaseYear = gameReleaseYear;
        this.gamePlaytime = gamePlaytime;
        this.gameTags = gameTags;
        this.gameRating = gameRating; // Initialize gameRating

    }

    public void printGameTitle() {
        System.out.println("Game Title: " + getGameTitle());
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getGameDeveloper() {
        return gameDeveloper;
    }

    public void setGameDeveloper(String gameDeveloper) {
        this.gameDeveloper = gameDeveloper;
    }

    public String getGamePublisher() {
        return gamePublisher;
    }

    public void setGamePublisher(String gamePublisher) {
        this.gamePublisher = gamePublisher;
    }

    // Getter for gameRating
    public String getGameRating() {
        return gameRating;
    }

    // Setter for gameRating
    public void setGameRating(String gameRating) {
        this.gameRating = gameRating;
    }
    public   List<String>  getGameGenre() {
        return gameGenre;
    }

    public void setGameGenre(List<String>  gameGenre) {
        this.gameGenre = gameGenre;
    }

    public List<String>  getGamePlatforms() {
        return gamePlatforms;
    }

    public void setGamePlatforms(List<String>  gamePlatforms) {
        this.gamePlatforms = gamePlatforms;
    }

    public String getGameSteamID() {
        return gameSteamID;
    }

    public void setGameSteamID(String gameSteamID) {
        this.gameSteamID = gameSteamID;
    }

    public String getGameReleaseYear() {
        return gameReleaseYear;
    }

    public void setGameReleaseYear(String gameReleaseYear) {
        this.gameReleaseYear = gameReleaseYear;
    }

    public String getGamePlaytime() {
        return gamePlaytime;
    }

    public void setGamePlaytime(String gamePlaytime) {
        this.gamePlaytime = gamePlaytime;
    }

    public List<String>  getGameTags() {
        return gameTags;
    }

    public void setGameTags(List<String>  gameTags) {
        this.gameTags = gameTags;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Game game = (Game) obj;
        return gameTitle != null && gameTitle.equals(game.gameTitle);
    }

    public String getPublisher() {
        return gamePublisher;
    }
}

