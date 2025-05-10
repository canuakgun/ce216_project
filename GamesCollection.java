package com.example.gamecatalogproject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GamesCollection {
    public List<Game> getAllGenres;
    private List<Game> collection;

    public GamesCollection() {
        this.collection = new ArrayList<>();
    }

    public GamesCollection(List<Game> collection) {
        this.collection = new ArrayList<>(collection); // Defensive copy
    }

    // Basic operations
    public void add(Game game) {
        if (game != null && !contains(game.getGameTitle())) {
            collection.add(game);
        }
    }

    public boolean remove(String title) {
        int index = indexOfGame(title);
        if (index != -1) {
            collection.remove(index);
            return true;
        }
        return false;
    }

    public void clear() {
        collection.clear();
    }

    public int size() {
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    // Search operations
    public int indexOfGame(String title) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getGameTitle().equalsIgnoreCase(title)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(String title) {
        return indexOfGame(title) != -1;
    }

    public Game getGame(String title) {
        int index = indexOfGame(title);
        return index != -1 ? collection.get(index) : null;
    }

    // Bulk operations
    public void addAll(List<Game> games) {
        if (games != null) {
            for (Game game : games) {
                add(game); // Uses our add method which checks for duplicates
            }
        }
    }

    // Filtering operations
    public List<Game> getGamesByDeveloper(String developer) {
        return collection.stream()
                .filter(game -> game.getGameDeveloper().equalsIgnoreCase(developer))
                .collect(Collectors.toList());
    }

    public List<Game> getGamesByPublisher(String publisher) {
        return collection.stream()
                .filter(game -> game.getGamePublisher().equalsIgnoreCase(publisher))
                .collect(Collectors.toList());
    }

    public List<Game> getGamesByGenre(String genre) {
        return collection.stream()
                .filter(game -> game.getGameGenre().contains(genre))
                .collect(Collectors.toList());
    }

    public List<Game> getGamesByPlatform(String platform) {
        return collection.stream()
                .filter(game -> game.getGamePlatforms().contains(platform))
                .collect(Collectors.toList());
    }
        public List<Game> getGamesByYear(String year){
            return collection.stream()
                    .filter(game -> game.getGameReleaseYear().contains(year))
                    .collect(Collectors.toList());
        }

    // Getter with defensive copy
    public List<Game> getAllGames() {
        return new ArrayList<>(collection);
    }

    // Utility methods
    @Override
    public String toString() {
        return "GamesCollection{" +
                "games=" + collection.stream()
                .map(Game::getGameTitle)
                .collect(Collectors.joining(", ")) +
                '}';
    }
}
