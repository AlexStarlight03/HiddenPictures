package com.alexdube.hiddenpictures;

import com.alexdube.hiddenpictures.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;


public class HiddenObjectsApp extends Application {

    private static Stage primaryStage;
    private MediaPlayer player1;
    private MediaPlayer player2;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConnection.initDatabase();
        Font.loadFont(getClass().getResourceAsStream("/fonts/MyFont.ttf"), 10);
        URL music1Path = getClass().getResource("/com/alexdube/hiddenpictures/sounds/Plazma.mp3");
        if (music1Path == null) {
            throw new IllegalArgumentException("La musique 1 n'a pas été trouvée.");
        }
        URL music2Path = getClass().getResource("/com/alexdube/hiddenpictures/sounds/YOASOBI.mp3");
        if (music2Path == null) {
            throw new IllegalArgumentException("La musique 2 n'a pas été trouvée.");
        }
        Media music1 = new Media(music1Path.toExternalForm());
        Media music2 = new Media(music2Path.toExternalForm());
        player1 = new MediaPlayer(music1);
        player2 = new MediaPlayer(music2);
        player1.setVolume(0.05);
        player2.setVolume(0.05);

        player1.setOnEndOfMedia(() -> {
            player1.stop();
            player2.seek(Duration.ZERO);
            player2.play();
        });

        player2.setOnEndOfMedia(() -> {
            player2.stop();
            player1.seek(Duration.ZERO);
            player1.play();
        });

        if (Math.random() < 0.5) {
            player1.play();
        } else {
            player2.play();
        }

        primaryStage = stage;
        switchPage("fxml/home_view.fxml");
        stage.setTitle("Hidden Objects");
        stage.setResizable(false);
        stage.show();
    }

    public static void switchPage(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(HiddenObjectsApp.class.getResource(path));
            Scene newScene = new Scene(loader.load());
            primaryStage.setScene(newScene);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

