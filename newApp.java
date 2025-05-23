package com.example.gamecatalogproject;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.util.converter.LocalDateStringConverter;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", ".")
        );
    }

    private void setupUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top - Title
        Label title = new Label("GAME CATALOG");
        title.setAlignment(Pos.CENTER);
        title.setStyle("""
        -fx-font-size: 32px;
        -fx-font-weight: bold;
        -fx-text-fill: linear-gradient(to right,navy   ,black );
        -fx-font-family: 'Roboto Light', 'Roboto Light', 'Roboto Light';
        -fx-effect: dropshadow(one-pass-box, rgba(0,2,4,0.4), 2, 0.0, 1, 1);
        -fx-padding: 32px;
        -fx-background-color: transparent;
    """);

        root.setTop(title);

        // Left Panel - Filters (wrapped in ScrollPane)
        VBox leftPanelContent = createLeftPanel(); 
        ScrollPane leftScrollPane = new ScrollPane(leftPanelContent);
        leftScrollPane.setFitToWidth(true); 
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); 
        leftScrollPane.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 10;"
        );
        root.setLeft(leftScrollPane);

        // Center Panel - Games List and Controls
        VBox centerPanel = createCenterPanel();
        root.setCenter(centerPanel);

        // Bottom - Help Button
        HBox bottomPanel = new HBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-alignment: bottom-left;");

        // Adding Import and Export buttons to bottom panel
        Button importButton = new Button("Import JSON");
        importButton.setOnAction(e -> importGames(primaryStage));

        Button exportButton = new Button("Export Selected");
        exportButton.setOnAction(e -> exportSelectedGames(primaryStage));

        bottomPanel.getChildren().addAll(importButton, exportButton, buttonforhelp);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 1115, 650);
        primaryStage.setTitle("Game Collection Catalog");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
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

        // Create the main content VBox
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(15));
        contentBox.setAlignment(Pos.TOP_LEFT);

        Label helpText = new Label(
                "1. Use the filters on the left to narrow down the game list. You can select multiple filters by holding Ctrl and clicking each filter. To select a range, hold Shift and click between two filters.\n" +
                        "2. Use the search bar to find games,genres,tags everything ! The search is case-insensitive and updates results as you type.\n" +
                        "3. Sort games by Title, Release Date, or Rating using the dropdown menu. Sorting is applied instantly based on the selected option (Ascending or Descending).\n" +
                        "4. Add new games using the 'Add Game' button. This opens a form where you can enter the game's title, release date, genre, rating, and other details.\n" +
                        "5. Edit games using the 'Edit Game' button. This opens a form where you can change the game's title, release date, genre, rating, and other details.\n" +
                        "6. Import games from a JSON file using the 'Import' button. The imported data will be added to the current list.\n" +
                        "7. Export selected games using the 'Export' button. You can select multiple games to include in the exported JSON file.\n\n" +
                        "8. Delete selected games using the 'Delete Game' button. To select multiple games, hold Ctrl and click on each game. A confirmation prompt will appear before deletion.\n\n" +
                        "Click the buttons below to view detailed images in full screen."
        );
        helpText.setStyle("-fx-font-size: 14px;");
        helpText.setWrapText(true);

        // Create ScrollPane for the text
        ScrollPane textScrollPane = new ScrollPane(helpText);
        textScrollPane.setFitToWidth(true);
        textScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        textScrollPane.setPrefViewportHeight(232);
        textScrollPane.setMaxHeight(Double.MAX_VALUE);

        // Create buttons to open images in new windows
        Button showImage1Btn = new Button("General");
        Button showImage2Btn = new Button("Adding a Game");

        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 8px 16px;";
        showImage1Btn.setStyle(buttonStyle);
        showImage2Btn.setStyle(buttonStyle);

        // Set button actions
        showImage1Btn.setOnAction(e -> showImageInNewWindow("/help1.png", "Add Game Form"));
        showImage2Btn.setOnAction(e -> showImageInNewWindow("/help2.png", "Help Image"));

        contentBox.getChildren().addAll(
                textScrollPane,
                new VBox(10, showImage1Btn, showImage2Btn)
        );

        // Set the dialog content
        helpAlert.getDialogPane().setContent(contentBox);
        helpAlert.getDialogPane().setPrefSize(600, 400); 
        helpAlert.setResizable(true);

        helpAlert.showAndWait();
    }

    private void showImageInNewWindow(String imagePath, String title) {
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                throw new IllegalArgumentException("Image not found: " + imagePath);
            }

            // Create new stage for the image
            Stage imageStage = new Stage();
            imageStage.setTitle(title);

            // Load the image
            Image image = new Image(imageUrl.toString());
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            // Create a StackPane to center the image
            StackPane root = new StackPane(imageView);
            root.setPadding(new Insets(10));

            // Create scene and set it to the stage
            Scene scene = new Scene(root);
            imageStage.setScene(scene);

            // Make the window maximized
            imageStage.setMaximized(true);

            // Calculate ideal size (90% of screen dimensions)
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() * 0.9;
            double height = screenBounds.getHeight() * 0.9;

            // Set initial size
            imageStage.setWidth(width);
            imageStage.setHeight(height);

            // Set minimum size
            imageStage.setMinWidth(800);
            imageStage.setMinHeight(600);

            imageStage.centerOnScreen();

            imageView.fitWidthProperty().bind(root.widthProperty().subtract(20));
            imageView.fitHeightProperty().bind(root.heightProperty().subtract(20));

            imageStage.show();

        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Could not load image");
            errorAlert.setContentText("The image could not be loaded: " + imagePath);
            errorAlert.showAndWait();
        }
    }
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setPrefWidth(280);

        genresList.setItems(FXCollections.observableArrayList(handler.getAllGenres()));
        yearList.setItems(FXCollections.observableArrayList(handler.getAllReleaseYears()));
        publisherList.setItems(FXCollections.observableArrayList(handler.getAllPublishers()));
        tagsList.setItems(FXCollections.observableArrayList(handler.getAllTags()));
        devList.setItems(FXCollections.observableArrayList(handler.getAllDevelopers()));
        platformList.setItems(FXCollections.observableArrayList(handler.getAllPlatforms()));

        // --- Genres ---
        TitledPane genresPane = createFilterPane(
                "🎮 Genres",
                genresList.getItems(),
                genresList,
                () -> {} 
        );

        // --- Filter by Year ---
        TitledPane yearPane = createFilterPane(
                "📅 Year",
                yearList.getItems(),
                yearList,
                () -> {} 
        );

        // --- Publishers ---
        TitledPane pubPane = createFilterPane(
                "🏢 Publishers",
                publisherList.getItems(),
                publisherList,
                () -> {} 
        );

        // --- Tags ---
        TitledPane tagsPane = createFilterPane(
                "\uD83D\uDC7E Tags",
                tagsList.getItems(),
                tagsList,
                () -> {}
        );

        // --- Developers ---
        TitledPane devPane = createFilterPane(
                "👨 Developers",
                devList.getItems(),
                devList,
                () -> {} 
        );

        // --- Platforms ---
        TitledPane platformsPane = createFilterPane(
                "🖥 Platforms",
                platformList.getItems(),
                platformList,
                () -> {} 
        );

        leftPanel.getChildren().addAll(
                genresPane, devPane, pubPane, tagsPane,
                platformsPane, yearPane, createResetAllButton());

        return leftPanel;
    }
    private Button createResetAllButton() {
        Button resetAllBtn = new Button("Reset All Filters");
        resetAllBtn.setMaxWidth(Double.MAX_VALUE); 

        resetAllBtn.setOnAction(e -> {
            genresList.getSelectionModel().clearSelection();
            yearList.getSelectionModel().clearSelection();
            publisherList.getSelectionModel().clearSelection();
            tagsList.getSelectionModel().clearSelection();
            devList.getSelectionModel().clearSelection();
            platformList.getSelectionModel().clearSelection();

            applyAllFilters();
        });

        return resetAllBtn;
    }
    private TitledPane createFilterPane(String title, ObservableList<String> items, ListView<String> listView, Runnable filterAction) {
        listView.setItems(items);
        listView.setPrefHeight(120);
        listView.setStyle("-fx-background-color: #3c3f41; -fx-text-fill: white;");
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Add listener for selection changes
        listView.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<String>) change -> applyAllFilters()
        );

        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> {
            listView.getSelectionModel().clearSelection();
            applyAllFilters(); 
        });

        VBox content = new VBox(8, listView, resetBtn);
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
            boolean isTenScale = rating > 5; 

            //  5-point scale for display
            double displayRating = rating;

            // Create star container
            HBox starsBox = new HBox(2);

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

            Label numericRating = new Label(String.format("%.1f/10", rating));
            numericRating.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

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
        gamesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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

                    DropShadow ds = new DropShadow();
                    ds.setRadius(0.5);          // çok ince gölge
                    ds.setOffsetX(0);
                    ds.setOffsetY(0);
                    ds.setColor(Color.BLACK);
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
                String encodedQuery = URLEncoder.encode(gameTitle, StandardCharsets.UTF_8);
                String googleSearchUrl = "https://www.google.com/search?q=" + encodedQuery + "+game";

                HostServices hostServices = getHostServices();
                hostServices.showDocument(googleSearchUrl);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not open browser: " + ex.getMessage());
            }
        });

        return moreInfoLink;
    }
    private void initializeSampleData() {
     
        System.out.println("Successfully loaded " + handler.getCollectionSize() + " games:");
        handler.printAllGames();
        updateFilterLists();

        refreshGameList();
        Platform.runLater(this::resetAllFilters);
    }
    private void updateFilterLists() {
        ObservableList<String> allGenres = FXCollections.observableArrayList();
        for (Game game : handler.getAllGames()) {
            for (String genre : game.getGameGenre()) {
                if (!allGenres.contains(genre.toUpperCase())) {
                    allGenres.add(genre.toUpperCase());
                }
            }
        }
        genresList.setItems(allGenres);

        ObservableList<String> allDevs = FXCollections.observableArrayList();
        for (Game game : handler.getAllGames()) {
            String dev = game.getGameDeveloper();
            if (!allDevs.contains(dev)) {
                allDevs.add(dev);
            }
        }
        devList.setItems(allDevs);

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
        ObservableList<String> selectedGenres = genresList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedYears = yearList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedPublishers = publisherList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedDevelopers = devList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedTags = tagsList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedPlatforms = platformList.getSelectionModel().getSelectedItems();

        // Refresh the data
        handler.refreshData();

        // Reapply filters
        applyAllFilters();

        // Restore selections
        genresList.getSelectionModel().selectAll();
        yearList.getSelectionModel().selectAll();
        publisherList.getSelectionModel().selectAll();
        devList.getSelectionModel().selectAll();
        tagsList.getSelectionModel().selectAll();
        platformList.getSelectionModel().selectAll();
    }
    private void resetAllFilters() {
        genresList.getSelectionModel().clearSelection();
        yearList.getSelectionModel().clearSelection();
        publisherList.getSelectionModel().clearSelection();
        tagsList.getSelectionModel().clearSelection();
        devList.getSelectionModel().clearSelection();
        platformList.getSelectionModel().clearSelection();

        applyAllFilters();
    }

    private void setAllFiltersDisabled(boolean disabled) {
        genresList.setDisable(disabled);
        yearList.setDisable(disabled);
        publisherList.setDisable(disabled);
        tagsList.setDisable(disabled);
        devList.setDisable(disabled);
        platformList.setDisable(disabled);
    }
    private void searchGames() {
        String query = filterField.getText().toLowerCase().trim();

        applyAllFilters();

        if (query.isEmpty()) {
            return;
        }

        ObservableList<Game> filteredGames = gamesList.getItems().filtered(game -> {
            if (game.getGameTitle().toLowerCase().contains(query)) {
                return true;
            }

            if (game.getGameDeveloper().toLowerCase().contains(query)) {
                return true;
            }

            if (game.getGamePublisher().toLowerCase().contains(query)) {
                return true;
            }

            if (game.getGameReleaseYear().toLowerCase().contains(query)) {
                return true;
            }

            if (game.getGameSteamID().toLowerCase().contains(query)) {
                return true;
            }

            if (game.getGamePlaytime().toLowerCase().contains(query)) {
                return true;
            }

            if (game.getGameRating().toLowerCase().contains(query)) {
                return true;
            }

            for (String genre : game.getGameGenre()) {
                if (genre.toLowerCase().contains(query)) {
                    return true;
                }
            }

            for (String platform : game.getGamePlatforms()) {
                if (platform.toLowerCase().contains(query)) {
                    return true;
                }
            }

            for (String tag : game.getGameTags()) {
                if (tag.toLowerCase().contains(query)) {
                    return true;
                }
            }

            return false;
        });

        gamesList.setItems(filteredGames);
    }





    private void applyAllFilters() {
        // Get all selected values from each filter category
        ObservableList<String> selectedGenres = genresList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedYears = yearList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedPublishers = publisherList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedDevelopers = devList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedTags = tagsList.getSelectionModel().getSelectedItems();
        ObservableList<String> selectedPlatforms = platformList.getSelectionModel().getSelectedItems();

        // Filter the games
        ObservableList<Game> filteredGames = FXCollections.observableArrayList();

        outerLoop:
        for (Game game : handler.getAllGames()) {
            if (!selectedGenres.isEmpty()) {
                boolean genreMatch = false;
                for (String genre : selectedGenres) {
                    if (game.getGameGenre().contains(genre)) {
                        genreMatch = true;
                        break;
                    }
                }
                if (!genreMatch) continue outerLoop;
            }

            if (!selectedYears.isEmpty()) {
                boolean yearMatch = false;
                for (String year : selectedYears) {
                    if (game.getGameReleaseYear().equals(year)) {
                        yearMatch = true;
                        break;
                    }
                }
                if (!yearMatch) continue outerLoop;
            }

            if (!selectedPublishers.isEmpty()) {
                boolean publisherMatch = false;
                for (String publisher : selectedPublishers) {
                    if (game.getGamePublisher().equals(publisher)) {
                        publisherMatch = true;
                        break;
                    }
                }
                if (!publisherMatch) continue outerLoop;
            }

            if (!selectedDevelopers.isEmpty()) {
                boolean developerMatch = false;
                for (String developer : selectedDevelopers) {
                    if (game.getGameDeveloper().equals(developer)) {
                        developerMatch = true;
                        break;
                    }
                }
                if (!developerMatch) continue outerLoop;
            }

            if (!selectedTags.isEmpty()) {
                boolean tagMatch = false;
                for (String tag : selectedTags) {
                    if (game.getGameTags().contains(tag)) {
                        tagMatch = true;
                        break;
                    }
                }
                if (!tagMatch) continue outerLoop;
            }

            if (!selectedPlatforms.isEmpty()) {
                boolean platformMatch = false;
                for (String platform : selectedPlatforms) {
                    if (game.getGamePlatforms().contains(platform)) {
                        platformMatch = true;
                        break;
                    }
                }
                if (!platformMatch) continue outerLoop;
            }

            // If we get here the game matches all active filters
            filteredGames.add(game);
        }

        // Update the games list
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
                    ? Double.compare(Double.parseDouble(g1.getGameRating()), Double.parseDouble(g2.getGameRating()))
                    : Double.compare(Double.parseDouble(g2.getGameRating()), Double.parseDouble(g1.getGameRating())));
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
                resetAllFilters();
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

        dialog.setResizable(true);
        dialog.getDialogPane().setMinSize(400, 600);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(500);
        scrollPane.setPrefViewportWidth(400);

        TextField titleField = new TextField();
        TextField developerField = new TextField();
        TextField publisherField = new TextField();
        TextField genreField = new TextField();
        genreField.setPromptText("Comma separated (Action,Adventure)");

        // Platform selection using the custom PlatformSelector
        PlatformSelector platformSelector = new PlatformSelector();

        TextField steamIdField = new TextField();
        DatePicker releaseYearField = new DatePicker();
        TextField playtimeField = new TextField();
        TextField tagsField = new TextField();
        tagsField.setPromptText("Comma separated (Open-World,Multiplayer)");
        TextField ratingField = new TextField();
        playtimeField.setPromptText("Hours");
        ratingField.setPromptText("0.0 - 10.0");

        // Input validation setup
        setupInputValidation(steamIdField, playtimeField, ratingField);

        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Developer:"), 0, 1);
        grid.add(developerField, 1, 1);
        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);
        grid.add(new Label("Genres:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Platforms:"), 0, 4);
        grid.add(platformSelector, 1, 4);
        grid.add(new Label("Steam ID:"), 0, 5);
        grid.add(steamIdField, 1, 5);
        grid.add(new Label("Release Year:"), 0, 6);
        grid.add(releaseYearField, 1, 6);
        grid.add(new Label("Playtime:"), 0, 7);
        grid.add(playtimeField, 1, 7);
        grid.add(new Label("Tags:"), 0, 8);
        grid.add(tagsField, 1, 8);
        grid.add(new Label("Rating (0.0-10.0):"), 0, 9);
        grid.add(ratingField, 1, 9);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.initModality(Modality.APPLICATION_MODAL);
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        addValidationListeners(okButton, titleField, steamIdField, releaseYearField,
                playtimeField, ratingField);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    // Get selected platforms
                    List<String> selectedPlatforms = platformSelector.getSelectedPlatforms();

                    // Get genres and tags
                    List<String> genres = Arrays.asList(genreField.getText().split("\\s*,\\s*"));
                    List<String> tags = Arrays.asList(tagsField.getText().split("\\s*,\\s*"));
                    String year= String.valueOf(releaseYearField.getValue().getYear());
                    return new Game(
                            titleField.getText().trim(),
                            developerField.getText().trim(),
                            publisherField.getText().trim(),
                            genres,
                            selectedPlatforms,
                            steamIdField.getText().trim(),
                            year,
                            playtimeField.getText().trim(),
                            tags,
                            ratingField.getText().trim()
                    );
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create game: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(game -> {
            if (handler.addGame(game)) {
                updateFiltersWithNewGame(game);

                refreshGameList();
                filterField.clear();
                resetAllFilters();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add game (Duplicate title or duplicate SteamID)");
            }
        });
    }

    // Helper method to update filters with new game's properties
    private void updateFiltersWithNewGame(Game game) {
        // Update genres list
        for (String genre : game.getGameGenre()) {
            if (!genresList.getItems().contains(genre)) {
                genresList.getItems().add(genre);
            }
        }

        String dev = game.getGameDeveloper();
        if (!devList.getItems().contains(dev)) {
            devList.getItems().add(dev);
        }

        String publisher = game.getGamePublisher();
        if (!publisherList.getItems().contains(publisher)) {
            publisherList.getItems().add(publisher);
        }

        for (String tag : game.getGameTags()) {
            if (!tagsList.getItems().contains(tag)) {
                tagsList.getItems().add(tag);
            }
        }

        for (String platform : game.getGamePlatforms()) {
            if (!platformList.getItems().contains(platform)) {
                platformList.getItems().add(platform);
            }
        }

        String year = game.getGameReleaseYear();
        if (!yearList.getItems().contains(year)) {
            yearList.getItems().add(year);
        }
    }
    private class PlatformSelector extends VBox {
        private final List<CheckBox> checkBoxes = new ArrayList<>();

        public PlatformSelector() {
            setSpacing(5);

            Map<String, List<String>> platformCategories = new LinkedHashMap<>();
            platformCategories.put("Consoles", Arrays.asList("Nintendo Switch", "Playstation", "Xbox"));
            platformCategories.put("PC", Arrays.asList("Steam", "Epic Games Store", "Microsoft Store"));
            platformCategories.put("Mobile", Arrays.asList("Android", "iOS"));
            platformCategories.put("Cloud Gaming", Arrays.asList("NVIDIA GeForce Now", "Google Stadia"));
            platformCategories.put("Other", Arrays.asList("Arcade", "Atari", "Nintendo", "Sega",
                    "Sony", "Commodore 64", "PlayStation Portable", "Nintendo 3DS"));

            // Create UI for each category
            platformCategories.forEach((category, platforms) -> {
                Label categoryLabel = new Label(category);
                categoryLabel.setStyle(" -fx-font-style: italic;");
                getChildren().add(categoryLabel);

                for (String platform : platforms) {
                    CheckBox checkBox = new CheckBox(platform);
                    checkBoxes.add(checkBox);
                    getChildren().add(checkBox);
                }
            });
        }

        public List<String> getSelectedPlatforms() {
            return checkBoxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toList());
        }
    }

    // Input validation setup
    private void setupInputValidation(TextField steamIdField, TextField playtimeField, TextField ratingField) {
        // Steam ID - numeric only
        steamIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                steamIdField.setText(oldVal);
            }
        });

        // Playtime - positive integer
        playtimeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                playtimeField.setText(oldVal);
            }
        });

        // Rating - decimal between 0.0 and 10.0
        ratingField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,1})?") ||
                    (newVal.matches("\\d+") && Integer.parseInt(newVal) > 10) ||
                    (newVal.matches("\\d+\\.\\d+") && Double.parseDouble(newVal) > 10.0)) {
                ratingField.setText(oldVal);
            }
        });

        // Add tooltips
        steamIdField.setTooltip(new Tooltip("Numeric Steam ID only"));
        playtimeField.setTooltip(new Tooltip("Playtime in minutes (positive integer)"));
        ratingField.setTooltip(new Tooltip("Rating from 0.0 to 10.0"));
    }

    // Validation listeners for OK button
    private void addValidationListeners(Node okButton, TextField titleField,
                                        TextField steamIdField, DatePicker releaseDatePicker,
                                        TextField playtimeField, TextField ratingField) {
        // Combine all fields for validation
        List<Control> requiredFields = Arrays.asList(
                titleField, steamIdField, releaseDatePicker, playtimeField, ratingField
        );

        // Create change listener for all fields
        ChangeListener<Object> validationListener = (obs, oldVal, newVal) -> {
            boolean allValid = !titleField.getText().trim().isEmpty();

            // Check title is not empty

            // Check Steam ID is numeric and not empty
            try {
                Integer.parseInt(steamIdField.getText().trim());
            } catch (NumberFormatException e) {
                allValid = false;
            }

            // Check release year is valid (1970-2025)
            if (releaseDatePicker.getValue() == null ||
                    releaseDatePicker.getValue().getYear() < 1970 ||
                    releaseDatePicker.getValue().getYear() > 2025) {
                allValid = false;
            }

            // Check playtime is positive integer
            try {
                int playtime = Integer.parseInt(playtimeField.getText().trim());
                if (playtime <= 0) {
                    allValid = false;
                }
            } catch (NumberFormatException e) {
                allValid = false;
            }

            // Check rating is between 0.0 and 10.0
            try {
                double rating = Double.parseDouble(ratingField.getText().trim());
                if (rating < 0.0 || rating > 10.0) {
                    allValid = false;
                }
            } catch (NumberFormatException e) {
                allValid = false;
            }

            okButton.setDisable(!allValid);
        };

        // Add listener to all fields
        requiredFields.forEach(field -> {
            if (field instanceof TextField) {
                ((TextField) field).textProperty().addListener(validationListener);
            } else if (field instanceof DatePicker) {
                ((DatePicker) field).valueProperty().addListener(validationListener);
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

        dialog.setResizable(true);
        dialog.getDialogPane().setMinSize(400, 600);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(500);
        scrollPane.setPrefViewportWidth(400);

        // Initialize fields with selected game's data
        TextField titleField = new TextField(selectedGame.getGameTitle());
        TextField developerField = new TextField(selectedGame.getGameDeveloper());
        TextField publisherField = new TextField(selectedGame.getGamePublisher());
        TextField genreField = new TextField(String.join(", ", selectedGame.getGameGenre()));
        genreField.setPromptText("Comma separated (Action,Adventure)");

        // Use PlatformSelector instead of TextField
        PlatformSelector platformSelector = new PlatformSelector();

        TextField steamIdField = new TextField(selectedGame.getGameSteamID());
        DatePicker releaseYearField = new DatePicker();
        TextField playtimeField = new TextField(selectedGame.getGamePlaytime());
        TextField tagsField = new TextField(String.join(", ", selectedGame.getGameTags()));
        tagsField.setPromptText("Comma separated (Open-World,Multiplayer)");
        TextField ratingField = new TextField(selectedGame.getGameRating());
        ratingField.setPromptText("0.0 - 10.0");

        // Input validation setup
        setupInputValidation(steamIdField, playtimeField, ratingField);

        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Developer:"), 0, 1);
        grid.add(developerField, 1, 1);
        grid.add(new Label("Publisher:"), 0, 2);
        grid.add(publisherField, 1, 2);
        grid.add(new Label("Genres:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Platforms:"), 0, 4);
        grid.add(platformSelector, 1, 4);
        grid.add(new Label("Steam ID:"), 0, 5);
        grid.add(steamIdField, 1, 5);
        grid.add(new Label("Release Year:"), 0, 6);
        grid.add(releaseYearField, 1, 6);
        grid.add(new Label("Playtime:"), 0, 7);
        grid.add(playtimeField, 1, 7);
        grid.add(new Label("Tags:"), 0, 8);
        grid.add(tagsField, 1, 8);
        grid.add(new Label("Rating (0.0-10.0):"), 0, 9);
        grid.add(ratingField, 1, 9);

        // Add OK and Cancel buttons
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Disable OK button initially
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(false); // Enable by default since we're editing existing valid data

        // Add validation listeners
        addValidationListeners(okButton, titleField, steamIdField, releaseYearField,
                playtimeField, ratingField);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    List<String> selectedPlatforms = platformSelector.getSelectedPlatforms();

                    List<String> genres = Arrays.asList(genreField.getText().split("\\s*,\\s*"));
                    List<String> tags = Arrays.asList(tagsField.getText().split("\\s*,\\s*"));
                    String year2= String.valueOf(releaseYearField.getValue().getYear());
                    return new Game(
                            titleField.getText().trim(),
                            developerField.getText().trim(),
                            publisherField.getText().trim(),
                            genres,
                            selectedPlatforms,
                            steamIdField.getText().trim(),
                            year2,
                            playtimeField.getText().trim(),
                            tags,
                            ratingField.getText().trim()
                    );
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update game: Every field should correctly filled !" );
                    return null;
                }
            }
            return null;
        });

        Optional<Game> result = dialog.showAndWait();
        result.ifPresent(editedGame -> {
            handler.removeGame(selectedGame.getGameTitle());
            if (handler.addGame(editedGame)) {
                updateFiltersWithNewGame(editedGame);

                refreshGameList();
                filterField.clear();
                resetAllFilters();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Game updated successfully!");
            } else {
                // If editing fails add back the original game
                handler.addGame(selectedGame);
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update game");
            }
        });
    }
    // For test uses only 
    private String calculateGameRating(Game game) { 
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
                List<Game> importedGames = handler.importGamesFromFile(file.getAbsolutePath());
                if (!importedGames.isEmpty()) {
                    refreshGameList();
                    resetAllFilters();
                    updateFilterLists();

                    for(Game game : importedGames) {
                        updateFiltersWithNewGame(game);
                    }

                    showAlert(Alert.AlertType.INFORMATION, "Import Successful",
                            "Successfully imported " + importedGames.size() + " game" +
                                    (importedGames.size() > 1 ? "s" : "") + " from " + file.getName());
                } else {
                    showAlert(Alert.AlertType.WARNING, "Import Warning",
                            "No new games were imported. All selected games are already in the list.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Error",
                        "Failed to import games: " + e.getMessage());
            }
        }
    }

    private void exportSelectedGames(Stage primaryStage) {
        ObservableList<Game> selectedGames = gamesList.getSelectionModel().getSelectedItems();
        if (selectedGames.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select one or more games to export");
            return;
        }

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                boolean success = handler.exportGamesToFile(file.getAbsolutePath(), selectedGames);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                            "Exported " + selectedGames.size() + " games to " + file.getName());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export games");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Export Error",
                        "Failed to export games: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
