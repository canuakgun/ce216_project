 package com.example.gamecatalogproject;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

public class newApp extends Application {

    private final Button buttonforhelp = new Button("Help");
    private final Handler handler = new Handler();
    private final ListView<Game> gamesList = new ListView<>();
    private final TextField filterField = new TextField();
    private final ComboBox<String> sortOptions = new ComboBox<>();
    private final ListView<String> genresList = new ListView<>();
    private final ListView<String> devList = new ListView<>();
    private final ListView<String> tagsList = new ListView<>();

    private final ListView<String> yearList = new ListView<>();
    private final ListView<String> platformList = new ListView<>();

    private final ListView<String> publisherList = new ListView<>();
    private final ToggleGroup orderGroup = new ToggleGroup();
    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void start(Stage primaryStage) {
        initializeSampleData();
        setupUI(primaryStage);

        // Configure file chooser
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", ".")
        );
    }

    private void setupUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top - Title
        Label title = new Label("                                                      GAME CATALOG");
        title.setAlignment(Pos.TOP_CENTER);
        title.setStyle("-fx-font-size: 28px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #2c3e50; " +
                "-fx-font-family: 'Segoe UI', sans-serif; " +
                "-fx-padding: 15px;");
        root.setTop(title);

        // Left Panel - Filters
        VBox leftPanel = createLeftPanel();
        leftPanel.setStyle(
                "-fx-background-color: #f0f0f0; " +          // Light background color
                        "-fx-border-color: #cccccc; " +               // Light gray border
                        "-fx-border-width: 1; " +                      // Thin border width
                        "-fx-border-radius: 15; " +                    // Rounded corners
                        "-fx-padding: 20; " +                           // Padding inside the left panel
                        "-fx-spacing: 35;"                             // Space between child elements
        );        root.setLeft(leftPanel);

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
    private void showGameDetails(Game game) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle(game.getGameTitle() + " - Details");

        // Main container
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f9f9f9;");

        // Game Title
        Label titleLabel = new Label(game.getGameTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setAlignment(Pos.TOP_CENTER);


        // Rating
        Label ratingLabel = new Label(getStarRating(game.getGameRating ()));
        ratingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffcc00;");
        VBox ratingBox = createEnhancedRatingDisplay(game.getGameRating());
        // Separator
        Separator separator1 = new Separator();
        separator1.setPadding(new Insets(5, 0, 5, 0));

        // Basic Info Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(8);
        infoGrid.setPadding(new Insets(0, 0, 10, 0));

        // Add basic info
        addGridRow(infoGrid, 0, "Developer:", game.getGameDeveloper());
        addGridRow(infoGrid, 1, "Publisher:", game.getGamePublisher());
        addGridRow(infoGrid, 2, "Release Year:", game.getGameReleaseYear());
        addGridRow(infoGrid, 3, "Playtime:", game.getGamePlaytime() + " hours");
        addGridRow(infoGrid, 4, "Steam ID:", game.getGameSteamID());

        // Separator
        Separator separator2 = new Separator();
        separator2.setPadding(new Insets(5, 0, 5, 0));

        // Categories Section
        HBox categoriesBox = new HBox(20);
        categoriesBox.setPadding(new Insets(10, 0, 0, 0));

        // Genres
        VBox genresBox = createCategoryBox("Genres", game.getGameGenre());

        // Platforms
        VBox platformsBox = createCategoryBox("Platforms", game.getGamePlatforms());

        // Tags
        VBox tagsBox = createCategoryBox("Tags", game.getGameTags());

        categoriesBox.getChildren().addAll(genresBox, platformsBox, tagsBox);

        // Close Button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        closeButton.setOnAction(e -> detailsStage.close());

        Hyperlink moreInfoLink = createGoogleSearchLink(game.getGameTitle());

        // Add all components to root
        root.getChildren().addAll(
                titleLabel,
                ratingBox,
                separator1,
                infoGrid,
                separator2,
                categoriesBox,
                closeButton,
                moreInfoLink
        );

        // Configure scene and stage
        Scene scene = new Scene(root, 500, 580);
        detailsStage.setScene(scene);
        detailsStage.setResizable(false);
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.show();
    }

    // Helper method to add rows to the info grid
    private void addGridRow(GridPane grid, int row, String label, String value) {
        Label infoLabel = new Label(label);
        infoLabel.setStyle("-fx-font-weight: bold;");
        grid.add(infoLabel, 0, row);

        Label infoValue = new Label(value);
        grid.add(infoValue, 1, row);
    }

    // Helper method to create category boxes
    private VBox createCategoryBox(String title, List<String> items) {
        VBox box = new VBox(5);
        Label titleLabel = new Label(title + ":");
        titleLabel.setStyle("-fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(items));
        listView.setPrefHeight(150);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd;");

        box.getChildren().addAll(titleLabel, listView);
        return box;
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
        VBox leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setPrefWidth(280);

        // --- Genres ---
        TitledPane genresPane = createFilterPane(
                "🎮 Genres",
                FXCollections.observableArrayList(
                        "Action", "Adventure", "RPG", "FPS", "Strategy", "Sports",
                        "Horror", "Simulation", "Puzzle", "Platformer", "Open-World", "Racing"
                ),
                genresList,
                this::filterByGenre
        );

        // --- Filter by Year ---
        TitledPane yearPane = createFilterPane(
                "📅 Year",
                FXCollections.observableArrayList(
                        "2025", "2024", "2023", "2022", "2021", "2020",
                        "2019", "2018", "2017", "2016", "2015"
                ),
                yearList,
                this::filterByYear
        );


        // --- Publishers ---
        TitledPane pubPane = createFilterPane(
                "🏢 Publishers",
                FXCollections.observableArrayList(
                        "Electronic Arts", "Activision", "Bandai Namco", "SEGA", "2K Games","CD Projekt","ConcernedApe","Bethesda","Bethesda Softworks"
                ),
                publisherList,
                this::filterByPublisher
        );

        // --- Tags ---
        TitledPane tagsPane = createFilterPane(
                "\uD83D\uDC7E Tags",
                FXCollections.observableArrayList(
                        "Multiplayer", "Open-World", "Turn-based", "Co-op", "Singleplayer",
                        "Sandbox", "Survival", "Story-rich", "Casual", "Competitive"
                ),
                tagsList,
                this::filterByTags
        );
        // --- Developers ---
        TitledPane devPane = createFilterPane(
                "👨 Developers",
                FXCollections.observableArrayList(
                        "Nintendo", "Rockstar Games", "CD Projekt Red", "FromSoftware",
                        "Ubisoft", "Valve", "id Software","Bethesda Game Studios","Capcom","Larian Studios","Supergiant Games"
                ),
                devList,
                this::filterByDeveloper
        );


        // --- Platforms ---
        TitledPane platformsPane = createFilterPane(
                "🖥 Platforms",
                FXCollections.observableArrayList("PC", "PlayStation", "Xbox", "Switch", "Mobile"),
                platformList,
                this::filterByPlatform
        );


        leftPanel.getChildren().addAll(
                genresPane, devPane, pubPane, tagsPane,
                platformsPane,yearPane
        );

        return leftPanel;
    }

    private void filterByYear() {
        String selectedYear = yearList.getSelectionModel().getSelectedItem();
        if (selectedYear != null) {
            gamesList.getItems().setAll(handler.filterByYear(selectedYear));
        }
    }

    private void filterByPlatform() {
        String selectedPlatform = platformList.getSelectionModel().getSelectedItem();
        if (selectedPlatform != null) {
            gamesList.getItems().setAll(handler.filterByPlatform(selectedPlatform));
        }
    }


    private TitledPane createFilterPane(String title, ObservableList<String> items, ListView<String> listView, Runnable filterAction) {
        listView.setItems(items);
        listView.setPrefHeight(120);
        listView.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white;");
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button filterBtn = new Button("Filter");
        filterBtn.setOnAction(e -> filterAction.run());

        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> {
            listView.getSelectionModel().clearSelection();
            refreshGameList();
        });

        HBox buttons = new HBox(10, filterBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(8, listView, buttons);
        content.setPadding(new Insets(8));

        TitledPane pane = new TitledPane(title, content);
        pane.setExpanded(false);
        pane.setAnimated(true);
        return pane;
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

    private VBox createEnhancedRatingDisplay(String ratingStr) {
        VBox ratingBox = new VBox(5);
        ratingBox.setAlignment(Pos.CENTER_LEFT);

        try {
            double rating = Double.parseDouble(ratingStr);
            boolean isTenScale = rating > 5; // Assume ratings >5 are on 10-point scale

            // Normalize to 5-point scale for display
            double displayRating = isTenScale ? rating  : rating;

            // Create star container
            HBox starsBox = new HBox(2);

            // Determine star rating (using if-else instead of loops)
            if (displayRating <= 10 && displayRating > 8) {
                addStars(starsBox, 5, 0); // ★★★★★
            } else if (displayRating <= 8 && displayRating > 6) {
                addStars(starsBox, 4, 1); // ★★★★☆
            } else if (displayRating <= 6 && displayRating > 4) {
                addStars(starsBox, 3, 2); // ★★★☆☆
            } else if (displayRating <= 4 && displayRating > 2) {
                addStars(starsBox, 2, 3); // ★★☆☆☆
            } else if (displayRating <= 2 && displayRating > 0) {
                addStars(starsBox, 1, 4); // ★☆☆☆☆
            } else {
                addStars(starsBox, 0, 5); // ☆☆☆☆☆
            }

            // Add numeric rating
            Label numericRating = new Label(String.format("%.1f/10", rating));
            numericRating.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Add rating source (optional)
            Label sourceLabel = new Label("User Rating");
            sourceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

            ratingBox.getChildren().addAll(starsBox, numericRating, sourceLabel);

        } catch (NumberFormatException e) {
            Label errorLabel = new Label("Rating not available");
            errorLabel.setStyle("-fx-text-fill: #999999;");
            ratingBox.getChildren().add(errorLabel);
        }

        return ratingBox;
    }

    // Helper method to add gold and gray stars
    private void addStars(HBox starsBox, int fullStars, int emptyStars) {
        // Add gold stars (★)
        for (int i = 0; i < fullStars; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #ffcc00; -fx-font-size: 24px;");
            starsBox.getChildren().add(star);
        }
        // Add gray stars (☆)
        for (int i = 0; i < emptyStars; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 24px;");
            starsBox.getChildren().add(star);
}
    }

    private String getStarRating(String ratingStr) {
        try {
            double rating = Double.parseDouble(ratingStr);
            int fullStars = (int) rating;
            boolean hasHalfStar = (rating - fullStars) >= 0.5;

            StringBuilder stars = new StringBuilder();
            if(rating <= 10 && rating > 8)
                stars.append("★★★★★");
            else if(rating <= 8 && rating > 6)
                stars.append("★★★★☆");
            else if(rating <= 6 && rating > 4)
                stars.append("★★★☆☆");
            else if(rating <= 4 && rating > 2)
                stars.append("★★☆☆☆");
            else if(rating <= 2 && rating > 0)
                stars.append("★☆☆☆☆");
            else
                stars.append("☆☆☆☆☆");

            return stars.toString();
        } catch (NumberFormatException e) {
            return "☆☆☆☆☆";
        }
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
                    Label ratingLabel = new Label(getStarRating(game.getGameRating()) + " " + game.getGameRating()+ "/10");
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
        });  gamesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Game selectedGame = gamesList.getSelectionModel().getSelectedItem();
                if (selectedGame != null) {
                    showGameDetails(selectedGame);
                }
            }
        });

        refreshGameList();
    }

    private Hyperlink createGoogleSearchLink(String gameTitle) {
        Hyperlink moreInfoLink = new Hyperlink("More Information on Google");
        moreInfoLink.setStyle("-fx-text-fill: #0066cc; -fx-font-size: 14px;");

        moreInfoLink.setOnAction(e -> {
            try {
                // Encode the game title for URL
                String encodedQuery = URLEncoder.encode(gameTitle, "UTF-8");
                String googleSearchUrl = "https://www.google.com/search?q=" + encodedQuery + "+game";

                // Open the URL in the default browser
                HostServices hostServices = getHostServices();
                hostServices.showDocument(googleSearchUrl);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not open browser: " + ex.getMessage());
            }
        });

        return moreInfoLink;
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
        String query = filterField.getText().toLowerCase();
        if (query.isEmpty()) {
            refreshGameList();
            return;
        }

        List<Game> results = handler.searchGames(query);
        gamesList.setItems(FXCollections.observableArrayList(results));
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
    private void filterByPublisher() {
        String selectedPublisher = publisherList.getSelectionModel().getSelectedItem();
        if (selectedPublisher != null) {
            gamesList.getItems().setAll(handler.filterByPublisher(selectedPublisher));
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
        TextField ratingField = new TextField();
        ratingField.setPromptText("0.0 - 10.0");


        ratingField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                ratingField.setText(oldVal);
            }
        });


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
        grid.add(new Label("Rating:"), 0, 9);
        grid.add(ratingField, 1, 9);

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
                        List.of(tagsField.getText().split("\\s*,\\s*")),
                        ratingField.getText()

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
        TextField ratingField = new TextField();
        ratingField.setPromptText("0.0 - 10.0");


        ratingField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                ratingField.setText(oldVal);
            }
        });


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
        grid.add(new Label("Rating:"), 0, 9);
        grid.add(ratingField, 1, 9);

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
                        List.of(tagsField.getText().split("\\s*,\\s*")),
                        ratingField.getText()

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
