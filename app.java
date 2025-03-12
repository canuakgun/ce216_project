import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class app extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Left Sidebar for Filters
        VBox filterSidebar = new VBox(10);
        filterSidebar.setPadding(new Insets(10));
        filterSidebar.setStyle("-fx-background-color: #2b2b2b;");

        // Create Accordion for collapsible filter sections
        Accordion accordion = new Accordion();

        // Genre Filter with options
        TitledPane genrePane = createTitledPane("Genre", new CheckBox("Action"), new CheckBox("Adventure"),
                new CheckBox("RPG"), new CheckBox("Simulation"), new CheckBox("Sports"), new CheckBox("Strategy"),
                new CheckBox("Puzzle"), new CheckBox("Horror"), new CheckBox("Racing"), new CheckBox("Fighting"));

        // Developer Filter with options
        TitledPane developerPane = createTitledPane("Developer", new CheckBox("Shigeru Miyamoto"),
                new CheckBox("Hideo Kojima"), new CheckBox("Gabe Newell"), new CheckBox("Todd Howard"),
                new CheckBox("Sid Meier"));

        // Publisher Filter with options
        TitledPane publisherPane = createTitledPane("Publisher", new CheckBox("Electronic Arts (EA)"),
                new CheckBox("Activision Blizzard"), new CheckBox("Ubisoft"), new CheckBox("Nintendo"),
                new CheckBox("Sony Interactive Entertainment"));

        // Platforms Filter with options
        TitledPane platformsPane = createTitledPane("Platforms", new CheckBox("XBOX"), new CheckBox("PS5"),
                new CheckBox("PS4"), new CheckBox("NINTENDO"), new CheckBox("PC"));

        // Translator Filter with options
        TitledPane translatorPane = createTitledPane("Translator", new CheckBox("Gregory Rabassa"),
                new CheckBox("Edith Grossman"), new CheckBox("Suat Karantay"));

        // Release Year Filter with options
        TitledPane releaseYearPane = createTitledPane("Release Year", new CheckBox("1990-1995"),
                new CheckBox("1995-2000"), new CheckBox("2000-2005"), new CheckBox("2005-2010"),
                new CheckBox("2010-2015"));

        // Playtime Filter with options
        TitledPane playtimePane = createTitledPane("Playtime", new CheckBox("1-5"), new CheckBox("5-15"),
                new CheckBox("15-30"), new CheckBox("30-70"), new CheckBox("70-140"));

        // Format Filter with options
        TitledPane formatPane = createTitledPane("Format", new CheckBox("pepe"), new CheckBox("ceke"),
                new CheckBox("deke"));

        // Language Filter with options
        TitledPane languagePane = createTitledPane("Language", new CheckBox("Türkçe"), new CheckBox("İngilizce"),
                new CheckBox("Almanca"), new CheckBox("Rusça"), new CheckBox("Çince"), new CheckBox("Japonca"));

        // Rating Filter with options
        TitledPane ratingPane = createTitledPane("Rating", new CheckBox("0-20"), new CheckBox("20-40"),
                new CheckBox("40-60"), new CheckBox("60-80"), new CheckBox("80-100"));

        // Add TitledPanes to Accordion
        accordion.getPanes().addAll(genrePane, developerPane, publisherPane, platformsPane, translatorPane, releaseYearPane,
                playtimePane, formatPane, languagePane, ratingPane);

        // Scrollable area for filters
        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600); // Set max height for scrolling

        // Apply Filters Button
        Button applyFilters = new Button("Apply Filters");

        applyFilters.setOnAction(e -> {
            // Handle filtering logic based on selected checkboxes
            System.out.println("Filtering games with the selected criteria...");
            // Add the filter logic as before...
        });

        // Top Bar with Search and Buttons
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #1b1b1b;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search games...");

        Button importButton = new Button("Import");
        Button exportButton = new Button("Export");
        Button helpButton = new Button("Help");

        topBar.getChildren().addAll(searchField, importButton, exportButton, helpButton);

        // Main Content Area for Game Cards
        FlowPane gamePane = new FlowPane();
        gamePane.setPadding(new Insets(10));
        gamePane.setHgap(10);
        gamePane.setVgap(10);
        gamePane.setStyle("-fx-background-color: #3b3b3b;");

        // Placeholder Game Cards
        for (int i = 1; i <= 6; i++) {
            VBox gameCard = new VBox(5);
            gameCard.setPadding(new Insets(5));
            gameCard.setStyle("-fx-background-color: #4b4b4b; -fx-border-color: #5b5b5b; -fx-border-radius: 5;");
            gameCard.setAlignment(Pos.CENTER);
            gameCard.setPrefWidth(150);

            Label gameTitle = new Label("Game " + i);
            gameTitle.setStyle("-fx-text-fill: white;");

            gameCard.getChildren().add(gameTitle);
            gamePane.getChildren().add(gameCard);
        }

        // Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(scrollPane); // Use scrollable filter sidebar
        mainLayout.setTop(topBar);
        mainLayout.setCenter(gamePane);

        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("Game Collection Catalog");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TitledPane createTitledPane(String title, CheckBox... checkboxes) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-background-color: #2b2b2b;");
        for (CheckBox checkbox : checkboxes) {
            checkbox.setStyle("-fx-text-fill: white;");
            vbox.getChildren().add(checkbox);
        }

        // Create the TitledPane with customized styles
        TitledPane titledPane = new TitledPane(title, vbox);
        titledPane.setStyle("-fx-base: #333333; -fx-text-fill: white; -fx-font-size: 14;");

        // Set custom style for the header (title)
        titledPane.setStyle("-fx-base: #333333; -fx-text-fill: white; -fx-font-size: 14;");
        titledPane.setExpanded(false);  // By default, panes are collapsed

        return titledPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}