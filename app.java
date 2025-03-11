import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class app extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Video Game Catalog");

        Button btnCreate = new Button("Create Game");
        btnCreate.setOnAction(event -> System.out.println("Create Game!"));

        Button btnEdit = new Button("Edit Game");
        btnEdit.setOnAction(event -> System.out.println("Edit Game!"));

        Button btnDelete = new Button("Delete Game");
        btnDelete.setOnAction(event -> System.out.println("Delete Game!"));

        Button btnImport = new Button("Import JSON");
        btnImport.setOnAction(event -> System.out.println("Import JSON!"));

        Button btnExport = new Button("Export JSON");
        btnExport.setOnAction(event -> System.out.println("Export JSON!"));

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(event -> System.exit(0));

        // Klasör seçmek için DirectoryChooser kullanımı
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Game Images Folder");

        Button btnSelectFolder = new Button("Select Folder");
        HBox imageBox = new HBox(10); // Resimleri yan yana yerleştirmek için HBox kullanımı
        imageBox.setAlignment(Pos.CENTER);

        btnSelectFolder.setOnAction(event -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".gif"));
                if (files != null) {
                    imageBox.getChildren().clear();
                    for (File file : files) {
                        Image gameImage = new Image(file.toURI().toString());
                        ImageView imageView = new ImageView(gameImage);
                        imageView.setFitWidth(200); // Genişliği ayarlayın
                        imageView.setFitHeight(200); // Yüksekliği ayarlayın
                        imageBox.getChildren().add(imageView);
                    }
                }
            }
        });

        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(btnCreate, btnEdit, btnDelete, btnImport, btnExport, btnSelectFolder, btnExit);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20);
        root.getChildren().addAll(buttonBox, imageBox);
        root.setAlignment(Pos.TOP_CENTER); // Butonları üst merkeze hizala

        // Çok renkli arka plan oluşturma
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.BLACK),
            new Stop(0.5, Color.RED),
            new Stop(1, Color.BLUE)
            );
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, black, red, blue);");

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}