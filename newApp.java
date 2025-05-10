import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class newApp extends Application {

    private Button buttonforhelp = new Button("Help");
    private Handler handler = new Handler();
    private ListView<Game> gamesList = new ListView<>();
    private TextField filterField = new TextField();
    private ComboBox<String> sortOptions = new ComboBox<>();
    private ListView<String> genresList = new ListView<>();
    private ListView<String> devList = new ListView<>();
    private ListView<String> tagsList = new ListView<>();
    private ToggleGroup orderGroup = new ToggleGroup();
    private FileChooser fileChooser = new FileChooser();

    @Override
    public void start(Stage primaryStage) {
        initializeSampleData();
        setupUI(primaryStage);
        
        // Configure file chooser
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON Files", "*.json"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    private void setupUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top - Title
        Label title = new Label("GAME CATALOG");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        root.setTop(title);

        // Left Panel - Filters
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);

        // Center Panel - Games List and Controls
        VBox centerPanel = createCenterPanel();
        root.setCenter(centerPanel);

        // Bottom - Help Button
        HBox bottomPanel = new HBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-alignment: bottom-left;");
        
        // Add Import and Export buttons to bottom panel
        Button importButton = new Button("Import JSON");
        importButton.setOnAction(e -> importGames(primaryStage));
        
        Button exportButton = new Button("Export Selected");
        exportButton.setOnAction(e -> exportSelectedGames(primaryStage));
        
        bottomPanel.getChildren().addAll(importButton, exportButton, buttonforhelp);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Game Collection Catalog");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Help");
        helpAlert.setHeaderText("How to Use the Game Catalog");
        helpAlert.setContentText(
                "1. Use the filters on the left to narrow down the game list.\n" +
                        "2. Use the search bar to find games by title.\n" +
                        "3. Sort games by Title, Release Date, or Rating using the dropdown.\n" +
                        "4. Add new games using the 'Add Game' button.\n" +
                        "5. Delete selected games using the 'Delete Game' button."
        );
        helpAlert.showAndWait();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(200);

        // Genres Filter
        Label genresLabel = new Label("GENRES");
        genresLabel.setStyle("-fx-font-weight: bold;");
        genresList.setItems(FXCollections.observableArrayList(
                "Action", "Adventure", "RPG", "FPS", "Strategy",
                "Sports", "Horror", "Simulation", "Puzzle", "Platformer", "Open-World", "Racing"
        ));
        genresList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        HBox genreButtons = new HBox(5);
        Button filterGenre = new Button("Filter");
        filterGenre.setOnAction(e -> filterByGenre());
        Button resetGenre = new Button("Reset");
        resetGenre.setOnAction(e -> {
            genresList.getSelectionModel().clearSelection();
            refreshGameList();
        });
        genreButtons.getChildren().addAll(filterGenre, resetGenre);

        // Developers Filter
        Label devLabel = new Label("DEVELOPERS/PUBLISHERS");
        devLabel.setStyle("-fx-font-weight: bold;");
        devList.setItems(FXCollections.observableArrayList(
                "Nintendo", "Rockstar Games", "CD Projekt Red", "FromSoftware",
                "Ubisoft", "Electronic Arts", "Valve", "Bethesda"
        ));

        HBox devButtons = new HBox(5);
        Button filterDev = new Button("Filter");
        filterDev.setOnAction(e -> filterByDeveloper());
        Button resetDev = new Button("Reset");
        resetDev.setOnAction(e -> {
            devList.getSelectionModel().clearSelection();
            refreshGameList();
        });
        devButtons.getChildren().addAll(filterDev, resetDev);

        // Tags Filter
        Label tagsLabel = new Label("TAGS");
        tagsLabel.setStyle("-fx-font-weight: bold;");
        tagsList.setItems(FXCollections.observableArrayList(
                "Multiplayer", "Open-World", "Turn-based", "Co-op", "Singleplayer",
                "Sandbox", "Survival", "Story-rich", "Casual", "Competitive"
        ));
        tagsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        HBox tagButtons = new HBox(5);
        Button filterTag = new Button("Filter");
        filterTag.setOnAction(e -> filterByTags());
        Button resetTag = new Button("Reset");
        resetTag.setOnAction(e -> {
            tagsList.getSelectionModel().clearSelection();
            refreshGameList();
        });
        tagButtons.getChildren().addAll(filterTag, resetTag);

        leftPanel.getChildren().addAll(
                genresLabel, genresList, genreButtons,
                new Label("\n"), devLabel, devList, devButtons,
                new Label("\n"), tagsLabel, tagsList, tagButtons
        );

        return leftPanel;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        // Filter/Sort Controls
        HBox filterControls = new HBox(10);
        filterControls.setPadding(new Insets(0, 0, 10, 0));

        filterField.setPromptText("Search games...");
        filterField.textProperty().addListener((obs, oldVal, newVal) -> searchGames());

        sortOptions.getItems().addAll("Title", "Release Date", "Rating");
        sortOptions.setValue("Title");
        sortOptions.setOnAction(e -> sortGames());

        RadioButton ascOrder = new RadioButton("Asc");
        RadioButton descOrder = new RadioButton("Desc");
        ascOrder.setToggleGroup(orderGroup);
        descOrder.setToggleGroup(orderGroup);
        ascOrder.setSelected(true);
        orderGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> sortGames());

        Button addButton = new Button("Add Game");
        addButton.setOnAction(e -> showAddGameDialog());

        Button deleteButton = new Button("Delete Game");
        deleteButton.setOnAction(e -> deleteSelectedGame());

        // Add Edit Game button
        Button editButton = new Button("Edit Game");
        editButton.setOnAction(e -> showEditGameDialog());

        // Move Help button here
        buttonforhelp.setOnAction(e -> showHelpDialog());

        filterControls.getChildren().addAll(
                new Label("Search:"), filterField,
                new Label("Sort by:"), sortOptions,
                new Label("Order:"), ascOrder, descOrder,
                addButton, deleteButton, editButton, buttonforhelp // Add Edit button here
        );

        // Games List
        setupGamesListView();
        centerPanel.getChildren().addAll(filterControls, gamesList);

        return centerPanel;
    }

    private void setupGamesListView() {
        gamesList.setCellFactory(lv -> new ListCell<Game>() {
            @Override
            protected void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || game == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);

                    // Game Title and Rating
                    HBox titleBox = new HBox(5);
                    Label titleLabel = new Label(game.getGameTitle());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    Label ratingLabel = new Label("★★★★★ " + calculateGameRating(game));
                    ratingLabel.setStyle("-fx-text-fill: #ffcc00;");
                    titleBox.getChildren().addAll(titleLabel, ratingLabel);

                    // Game Details
                    Label detailsLabel = new Label(String.format(
                            "Developer: %s | Publisher: %s | Year: %s",
                            game.getGameDeveloper(),
                            game.getGamePublisher(),
                            game.getGameReleaseYear()
                    ));

                    // Genres and Platforms
                    Label genresLabel = new Label("Genres: " + String.join(", ", game.getGameGenre()));
                    Label platformsLabel = new Label("Platforms: " + String.join(", ", game.getGamePlatforms()));

                    container.getChildren().addAll(titleBox, detailsLabel, genresLabel, platformsLabel);
                    container.setStyle("-fx-padding: 5; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
                    setGraphic(container);
                }
            }
        });
        refreshGameList();
    }

    private void initializeSampleData() {
        // The handler will now automatically load the data
        // Print all games to verify loading
        System.out.println("Successfully loaded " + handler.getCollectionSize() + " games:");
        handler.printAllGames();
        updateFilterLists();
        refreshGameList();
    }
    private void updateFilterLists() {
        // Update genres list
        ObservableList<String> allGenres = FXCollections.observableArrayList();
        for (Game game : handler.getAllGames()) {
            for (String genre : game.getGameGenre()) {
                if (!allGenres.contains(genre.toUpperCase())) {
                    allGenres.add(genre.toUpperCase());
                }
            }
        }
        genresList.setItems(allGenres);

        // Update developers list
        ObservableList<String> allDevs = FXCollections.observableArrayList();
        for (Game game : handler.getAllGames()) {
            String dev = game.getGameDeveloper();
            if (!allDevs.contains(dev)) {
                allDevs.add(dev);
            }
        }
        devList.setItems(allDevs);

        // Update tags list
        ObservableList<String> allTags = FXCollections.observableArrayList();
        for (Game game : handler.getAllGames()) {
            for (String tag : game.getGameTags()) {
                if (!allTags.contains(tag)) {
                    allTags.add(tag);
                }
            }
        }
        tagsList.setItems(allTags);
    }

    private void refreshGameList() {
        gamesList.getItems().setAll(handler.getAllGames());
    }

    private void searchGames() {
        String query = filterField.getText().toLowerCase(); // Convert query to lowercase for case-insensitive search
        if (query.isEmpty()) {
            refreshGameList(); // Refresh the list if the query is empty
            return;
        }

        List<Game> results = handler.searchGames(query); // Use the handler to search for games
        gamesList.setItems(FXCollections.observableArrayList(results)); // Update the ListView with the search results
    }

    private void filterByGenre() {
        String selectedGenre = genresList.getSelectionModel().getSelectedItem();
        if (selectedGenre != null) {
            gamesList.getItems().setAll(handler.filterByGenre(selectedGenre));
        }
    }

    private void filterByDeveloper() {
        String selectedDev = devList.getSelectionModel().getSelectedItem();
        if (selectedDev != null) {
            gamesList.getItems().setAll(handler.filterByDeveloper(selectedDev));
        }
    }

    private void filterByTags() {
        ObservableList<String> selectedTags = tagsList.getSelectionModel().getSelectedItems();
        if (selectedTags.isEmpty()) {
            refreshGameList();
            return;
        }

        ObservableList<Game> filteredGames = FXCollections.observableArrayList();
        for (Game game : handler.getAllGames()) {
            for (String tag : selectedTags) {
                if (game.getGameTags().contains(tag)) {
                    filteredGames.add(game);
                    break; // Avoid adding the same game multiple times
                }
            }
        }
        gamesList.setItems(filteredGames);
    }

    private void sortGames() {
        String sortBy = sortOptions.getValue();
        boolean isAscending = ((RadioButton) orderGroup.getSelectedToggle()).getText().equals("Asc");

        ObservableList<Game> games = gamesList.getItems();

        if ("Title".equals(sortBy)) {
            games.sort((g1, g2) -> isAscending
                    ? g1.getGameTitle().compareToIgnoreCase(g2.getGameTitle())
                    : g2.getGameTitle().compareToIgnoreCase(g1.getGameTitle()));
        } else if ("Release Date".equals(sortBy)) {
            games.sort((g1, g2) -> isAscending
                    ? g1.getGameReleaseYear().compareTo(g2.getGameReleaseYear())
                    : g2.getGameReleaseYear().compareTo(g1.getGameReleaseYear()));
        } else if ("Rating".equals(sortBy)) {
            games.sort((g1, g2) -> isAscending
                    ? Double.compare(Double.parseDouble(calculateGameRating(g1)), Double.parseDouble(calculateGameRating(g2)))
                    : Double.compare(Double.parseDouble(calculateGameRating(g2)), Double.parseDouble(calculateGameRating(g1))));
        }

        gamesList.setItems(games);
    }

    private void deleteSelectedGame() {
        Game selectedGame = gamesList.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete " + selectedGame.getGameTitle() + "?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (handler.removeGame(selectedGame.getGameTitle())) {
                refreshGameList();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete game");
            }
        }
    }

    private void showAddGameDialog() {
        Dialog<Game> dialog = new Dialog<>();
        dialog.setTitle("Add New Game");
        dialog.setHeaderText("Enter game details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextField developerField = new TextField();
        TextField publisherField = new TextField();
        TextField genreField = new TextField();
        genreField.setPromptText("Comma separated (Action,Adventure)");
        TextField platformField = new TextField();
        platformField.setPromptText("Comma separated (PC,PS5)");
        TextField steamIdField = new TextField();
        TextField releaseYearField = new TextField();
        TextField playtimeField = new TextField();
        TextField tagsField = new TextField();
        tagsField.setPromptText("Comma separated (Open-World,Multiplayer)");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Developer:"), 0, 1);
        grid.add(developerField, 1, 1);
        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);
        grid.add(new Label("Genres:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Platforms:"), 0, 4);
        grid.add(platformField, 1, 4);
        grid.add(new Label("Steam ID:"), 0, 5);
        grid.add(steamIdField, 1, 5);
        grid.add(new Label("Release Year:"), 0, 6);
        grid.add(releaseYearField, 1, 6);
        grid.add(new Label("Playtime:"), 0, 7);
        grid.add(playtimeField, 1, 7);
        grid.add(new Label("Tags:"), 0, 8);
        grid.add(tagsField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Game(
                        titleField.getText(),
                        developerField.getText(),
                        publisherField.getText(),
                        List.of(genreField.getText().split("\\s*,\\s*")),
                        List.of(platformField.getText().split("\\s*,\\s*")),
                        steamIdField.getText(),
                        releaseYearField.getText(),
                        playtimeField.getText(),
                        List.of(tagsField.getText().split("\\s*,\\s*"))
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(game -> {
            if (handler.addGame(game)) {
                refreshGameList();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add game (possibly duplicate title)");
            }
        });
    }

    private void showEditGameDialog() {
        Game selectedGame = gamesList.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to edit");
            return;
        }

        Dialog<Game> dialog = new Dialog<>();
        dialog.setTitle("Edit Game");
        dialog.setHeaderText("Edit game details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(selectedGame.getGameTitle());
        TextField developerField = new TextField(selectedGame.getGameDeveloper());
        TextField publisherField = new TextField(selectedGame.getGamePublisher());
        TextField genreField = new TextField(String.join(", ", selectedGame.getGameGenre()));
        genreField.setPromptText("Comma separated (Action,Adventure)");
        TextField platformField = new TextField(String.join(", ", selectedGame.getGamePlatforms()));
        platformField.setPromptText("Comma separated (PC,PS5)");
        TextField steamIdField = new TextField(selectedGame.getGameSteamID());
        TextField releaseYearField = new TextField(selectedGame.getGameReleaseYear());
        TextField playtimeField = new TextField(selectedGame.getGamePlaytime());
        TextField tagsField = new TextField(String.join(", ", selectedGame.getGameTags()));
        tagsField.setPromptText("Comma separated (Open-World,Multiplayer)");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Developer:"), 0, 1);
        grid.add(developerField, 1, 1);
        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);
        grid.add(new Label("Genres:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Platforms:"), 0, 4);
        grid.add(platformField, 1, 4);
        grid.add(new Label("Steam ID:"), 0, 5);
        grid.add(steamIdField, 1, 5);
        grid.add(new Label("Release Year:"), 0, 6);
        grid.add(releaseYearField, 1, 6);
        grid.add(new Label("Playtime:"), 0, 7);
        grid.add(playtimeField, 1, 7);
        grid.add(new Label("Tags:"), 0, 8);
        grid.add(tagsField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Game(
                        titleField.getText(),
                        developerField.getText(),
                        publisherField.getText(),
                        List.of(genreField.getText().split("\\s*,\\s*")),
                        List.of(platformField.getText().split("\\s*,\\s*")),
                        steamIdField.getText(),
                        releaseYearField.getText(),
                        playtimeField.getText(),
                        List.of(tagsField.getText().split("\\s*,\\s*"))
                );
            }
            return null;
        });

        Optional<Game> result = dialog.showAndWait();
        result.ifPresent(editedGame -> {
            // First remove the old game
            handler.removeGame(selectedGame.getGameTitle());
            // Then add the edited game
            if (handler.addGame(editedGame)) {
                refreshGameList();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game edited successfully!");
            } else {
                // If adding fails, add back the original game
                handler.addGame(selectedGame);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to edit game");
            }
        });
    }

    private String calculateGameRating(Game game) {
        // Placeholder - implement your actual rating logic
        if (game.getGameTitle().contains("Zelda")) return "9.5";
        if (game.getGameTitle().contains("Elden")) return "9.0";
        return "9.2";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void importGames(Stage primaryStage) {
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                List<Game> importedGames = handler.getJsonParser().readFromJsonFile(file.getAbsolutePath());
                int addedCount = handler.addGamesFromList(importedGames);
                refreshGameList();
                showAlert(Alert.AlertType.INFORMATION, "Import Successful", 
                    "Successfully imported " + addedCount + " games from " + file.getName());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Error", 
                    "Failed to import games: " + e.getMessage());
            }
        }
    }

    private void exportSelectedGames(Stage primaryStage) {
        Game selectedGame = gamesList.getSelectionModel().getSelectedItem();
        if (selectedGame == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to export");
            return;
        }

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                boolean success = handler.getJsonParser().saveToJsonFile(
                    file.getAbsolutePath(), 
                    List.of(selectedGame)
                );
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Export Successful", 
                        "Game exported successfully to " + file.getName());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Export Error", 
                        "Failed to export game");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Export Error", 
                    "Failed to export game: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);

    }
}
