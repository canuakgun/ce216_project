import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class app extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game Collection Catalog");
        

        VBox welcomeLayout = new VBox(20);
        welcomeLayout.setPadding(new Insets(20));
        welcomeLayout.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome to the Game Catalog Program");

        
        Image welcomeImage = new Image("file:C:/Users/ATİLA/OneDrive/Pictures/ADSC Studio.png");
        ImageView imageView = new ImageView(welcomeImage);
        imageView.setFitWidth(200); 
        imageView.setFitHeight(200); 
        imageView.setPreserveRatio(true); 

        Button startButton = new Button("Start");
        welcomeLayout.setStyle("-fx-background-color:rgb(68, 24, 30);");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24;");
        startButton.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-font-size: 16;");
        welcomeLayout.getChildren().addAll(welcomeLabel, imageView, startButton);

        Scene welcomeScene = new Scene(welcomeLayout, 400, 300);

      
        VBox filterSidebar = new VBox(10);
        filterSidebar.setPadding(new Insets(10));
        filterSidebar.setStyle("-fx-background-color: #2b2b2b;");

        Accordion accordion = new Accordion();

        TitledPane genrePane = createTitledPane("Genre", new CheckBox("Action"), new CheckBox("Adventure"),
                new CheckBox("RPG"), new CheckBox("Simulation"), new CheckBox("Sports"), new CheckBox("Strategy"),
                new CheckBox("Puzzle"), new CheckBox("Horror"), new CheckBox("Racing"), new CheckBox("Fighting"));

        TitledPane developerPane = createTitledPane("Developer", new CheckBox("Shigeru Miyamoto"),
                new CheckBox("Hideo Kojima"), new CheckBox("Gabe Newell"), new CheckBox("Todd Howard"),
                new CheckBox("Sid Meier"));

        TitledPane publisherPane = createTitledPane("Publisher", new CheckBox("Electronic Arts (EA)"),
                new CheckBox("Activision Blizzard"), new CheckBox("Ubisoft"), new CheckBox("Nintendo"),
                new CheckBox("Sony Interactive Entertainment"));

        TitledPane platformsPane = createTitledPane("Platforms", new CheckBox("XBOX"), new CheckBox("PS5"),
                new CheckBox("PS4"), new CheckBox("NINTENDO"), new CheckBox("PC"));

        TitledPane translatorPane = createTitledPane("Translator", new CheckBox("Gregory Rabassa"),
                new CheckBox("Edith Grossman"), new CheckBox("Suat Karantay"));

        TitledPane releaseYearPane = createTitledPane("Release Year", new CheckBox("1990-1995"),
                new CheckBox("1995-2000"), new CheckBox("2000-2005"), new CheckBox("2005-2010"),
                new CheckBox("2010-2015"), new CheckBox("2015-2020"), new CheckBox("2020-2025"));

        TitledPane playtimePane = createTitledPane("Playtime", new CheckBox("1-5"), new CheckBox("5-15"),
                new CheckBox("15-30"), new CheckBox("30-70"), new CheckBox("70-140"));

        TitledPane formatPane = createTitledPane("Format", new CheckBox("pepe"), new CheckBox("ceke"),
                new CheckBox("deke"));

        TitledPane languagePane = createTitledPane("Language", new CheckBox("Turkish"), new CheckBox("English"),
                new CheckBox("German"), new CheckBox("Russian"), new CheckBox("Chinese"), new CheckBox("Japanese"));

        TitledPane ratingPane = createTitledPane("Rating", new CheckBox("0-20"), new CheckBox("20-40"),
                new CheckBox("40-60"), new CheckBox("60-80"), new CheckBox("80-100"));

        accordion.getPanes().addAll(genrePane, developerPane, publisherPane, platformsPane, translatorPane, releaseYearPane,
                playtimePane, formatPane, languagePane, ratingPane);

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);

        Button applyFilters = new Button("Apply Filters");

        applyFilters.setOnAction(e -> {
            System.out.println("Filtering games with the selected criteria...");
        });

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

        FlowPane gamePane = new FlowPane();
        gamePane.setPadding(new Insets(10));
        gamePane.setHgap(10);
        gamePane.setVgap(10);
        gamePane.setStyle("-fx-background-color: #3b3b3b;");

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

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(scrollPane);
        mainLayout.setTop(topBar);
        mainLayout.setCenter(gamePane);

        Scene mainScene = new Scene(mainLayout, 1200, 800);

        
        startButton.setOnAction(e -> primaryStage.setScene(mainScene));

        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private TitledPane createTitledPane(String title, CheckBox... checkboxes) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-background-color: #2b2b2b;");
        for (CheckBox checkbox : checkboxes) {
            checkbox.setStyle("-fx-text-fill: white;");
            vbox.getChildren().add(checkbox);
        }

        TitledPane titledPane = new TitledPane(title, vbox);
        titledPane.setStyle("-fx-base: #333333; -fx-text-fill: white; -fx-font-size: 14;");
        titledPane.setExpanded(false);

        return titledPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}