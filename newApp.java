package com.example.gamecatalogproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Optional;

public class newApp extends Application {

    private Handler handler = new Handler();
    private ListView<Game> gamesList = new ListView<>();
    private TextField filterField = new TextField();
    private ComboBox<String> sortOptions = new ComboBox<>();
    private ListView<String> genresList = new ListView<>();
    private ListView<String> devList = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        initializeSampleData();
        setupUI(primaryStage);
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

        // Center Panel - Games List
        VBox centerPanel = createCenterPanel();
        root.setCenter(centerPanel);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Game Collection Catalog");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(200);

        // Genres Filter
        Label genresLabel = new Label("GENRES");
        genresLabel.setStyle("-fx-font-weight: bold;");
        genresList.setItems(FXCollections.observableArrayList(
                "ACTION", "ADVENTURE", "RPG", "FPS", "STRATEGY",
                "SPORTS", "HORROR", "SIMULATION", "PUZZLE", "PLATFORMER"
        ));
        genresList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        HBox genreButtons = new HBox(5);
        Button filterGenre = new Button("Filter");
        filterGenre.setOnAction(e -> filterByGenre());
        Button applyGenre = new Button("Reset");
        applyGenre.setOnAction(e -> refreshGameList());
        genreButtons.getChildren().addAll(filterGenre, applyGenre);

        // Developers Filter
        Label devLabel = new Label("DEVELOPERS");
        devLabel.setStyle("-fx-font-weight: bold;");
        devList.setItems(FXCollections.observableArrayList(
                "Nintendo", "Rockstar", "CD Projekt Red", "FromSoftware",
                "Ubisoft", "Electronic Arts", "Valve", "Bethesda"
        ));

        HBox devButtons = new HBox(5);
        Button filterDev = new Button("Filter");
        filterDev.setOnAction(e -> filterByDeveloper());
        Button applyDev = new Button("Reset");
        applyDev.setOnAction(e -> refreshGameList());

        devButtons.getChildren().addAll(filterDev, applyDev);
        leftPanel.getChildren().addAll(
                genresLabel, genresList, genreButtons,
                new Label("\n"), devLabel, devList, devButtons
        );

        return leftPanel;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        // Filter/Sort Controls
        HBox filterControls = new HBox(10);
        filterField.setPromptText("Search games...");
        filterField.textProperty().addListener((obs, oldVal, newVal) -> searchGames());

        sortOptions.getItems().addAll("Title", "Release Date", "Rating");
        sortOptions.setValue("Title");

        ToggleGroup orderGroup = new ToggleGroup();
        RadioButton ascOrder = new RadioButton("Asc");
        RadioButton descOrder = new RadioButton("Desc");
        ascOrder.setToggleGroup(orderGroup);
        descOrder.setToggleGroup(orderGroup);
        ascOrder.setSelected(true);

        Button addButton = new Button("Add Game");
        addButton.setOnAction(e -> showAddGameDialog());

        Button deleteButton = new Button("Delete Game");
        deleteButton.setOnAction(e -> deleteSelectedGame());

        filterControls.getChildren().addAll(
                new Label("Search:"), filterField,
                new Label("Sort by:"), sortOptions,
                ascOrder, descOrder, addButton, deleteButton
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
                    titleLabel.setStyle("-fx-font-weight: bold;");
                    Label ratingLabel = new Label("★★★★★ " + calculateGameRating(game));
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
                    setGraphic(container);
                }
            }
        });
        refreshGameList();
    }

    private void initializeSampleData() {
        // Sample data initialization
        Game game1 = new Game(
                "The Legend of Zelda: Breath of the Wild",
                "Nintendo",
                "Nintendo",
                List.of("ACTION", "ADVENTURE", "RPG"),
                List.of("Nintendo Switch", "Wii U"),
                "12345",
                "2017",
                "100+",
                List.of("Open World", "Single Player")
        );

        Game game2 = new Game(
                "Elden Ring",
                "FromSoftware",
                "Bandai Namco",
                List.of("ACTION", "RPG"),
                List.of("PC", "PS4", "PS5", "Xbox One", "Xbox Series X"),
                "67890",
                "2022",
                "80+",
                List.of("Open World", "Souls-like", "Multiplayer")
        );

        handler.addGame(game1);
        handler.addGame(game2);
    }

    private void refreshGameList() {
        gamesList.getItems().setAll(handler.getAllGames());
    }

    private void searchGames() {
        String query = filterField.getText();
        if (query == null || query.isEmpty()) {
            refreshGameList();
            return;
        }
        gamesList.getItems().setAll(handler.searchGames(query));
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

        // Set up form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextField developerField = new TextField();
        TextField publisherField = new TextField();
        TextField genreField = new TextField();
        genreField.setPromptText("Comma separated");
        TextField platformField = new TextField();
        platformField.setPromptText("Comma separated");
        TextField steamIdField = new TextField();
        TextField releaseYearField = new TextField();
        TextField playtimeField = new TextField();
        TextField tagsField = new TextField();
        tagsField.setPromptText("Comma separated");

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

    private String calculateGameRating(Game game) {
        // Placeholder - implement your actual rating logic
        if (game.getGameTitle().contains("Zelda")) return "9.5";
        if (game.getGameTitle().contains("Elden")) return "9.0";
        return "8.5";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
