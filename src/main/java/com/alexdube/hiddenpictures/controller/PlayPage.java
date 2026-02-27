package com.alexdube.hiddenpictures.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.alexdube.hiddenpictures.HiddenObjectsApp;
import com.alexdube.hiddenpictures.util.Sparkle;
import com.alexdube.hiddenpictures.service.ApiService;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;


public class PlayPage implements Initializable {

    @FXML private ImageView hidden01;
    @FXML private ImageView hidden02;
    @FXML private ImageView hidden03;
    @FXML private ImageView hidden04;
    @FXML private ImageView hidden05;
    @FXML private ImageView hidden06;
    @FXML private ImageView hidden07;
    @FXML private ImageView hidden08;
    @FXML private ImageView hidden09;
    @FXML private ImageView hidden10;
    @FXML private ImageView icon01;
    @FXML private ImageView icon02;
    @FXML private ImageView icon03;
    @FXML private ImageView icon04;
    @FXML private ImageView icon05;
    @FXML private ImageView icon06;
    @FXML private ImageView icon07;
    @FXML private ImageView icon08;
    @FXML private ImageView icon09;
    @FXML private ImageView icon10;

    @FXML private ProgressBar progressBar;

    @FXML private VBox winMessageBox;
    @FXML private VBox loseMessageBox;

    @FXML private Label timerLabel;

    private Timeline timeline;
    private int timeSeconds = 80;

    public void startTime() {
        timerLabel.setText(formatTime(timeSeconds));
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeSeconds--;
                    timerLabel.setText(formatTime(timeSeconds));
                    if (timeSeconds <= 0) {
                        timeline.stop();
                        timerLabel.setText("Time's up!");
                        showLoseMessage();
                    }
                })
        );
        timeline.setCycleCount(timeSeconds);
        timeline.play();
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public void showWinMessage() {
        timeline.stop();
        if (!loseMessageBox.isVisible()) {
            saveGameScore(true);
            winMessageBox.setVisible(true);
            winMessageBox.toFront();
        }
    }

    public void showLoseMessage() {
        if (!winMessageBox.isVisible()) {
            saveGameScore(false);
            loseMessageBox.setVisible(true);
            loseMessageBox.toFront();
        }
    }

    private void saveGameScore(boolean win) {
        ApiService apiService = new ApiService();
        int foundPoints = (int) score * 10;
        int mistakePenalty = error * 5;
        int timeBonus = 0;
        if (win) {
            timeBonus = Math.max(0, timeSeconds * 2);
        }
        int finalScore = foundPoints + timeBonus + mistakePenalty;
        if (finalScore < 0) finalScore = 0;

       int userId = Session.getCurrentUser() != null ? Session.getCurrentUser().getId() : 0;
       apiService.addGame(userId, finalScore);
    }

    @FXML
    public void handleBackHome() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }

    @FXML
    public void handleRestart() {
        HiddenObjectsApp.switchPage("fxml/play_view.fxml");
    }

    private float score = 0;

    @FXML private Label scoreLabel;

    private final int total_hidden = 10;

    private final Map<ImageView, ImageView> objectToIcon = new HashMap<>();

    private final int maxError = 3;

    private int error = 0;

    @FXML private Label errorLabel;

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        winMessageBox.setVisible(false);
        loseMessageBox.setVisible(false);
        scoreLabel.setText("0 / " + total_hidden);
        errorLabel.setText((error + " / " + maxError));
        objectToIcon.put(hidden01, icon01);
        objectToIcon.put(hidden02, icon02);
        objectToIcon.put(hidden03, icon03);
        objectToIcon.put(hidden04, icon04);
        objectToIcon.put(hidden05, icon05);
        objectToIcon.put(hidden06, icon06);
        objectToIcon.put(hidden07, icon07);
        objectToIcon.put(hidden08, icon08);
        objectToIcon.put(hidden09, icon09);
        objectToIcon.put(hidden10, icon10);
        startTime();
    }


    @FXML
    private void objectFound(MouseEvent event) {
        ImageView objet = (ImageView)event.getSource();
        ImageView icon = objectToIcon.get(objet);
        if (icon.getOpacity() != 1.0 || loseMessageBox.isVisible()) {
            return;
        }
        hiddenAnimation(objet);
        Sparkle.sparkleAnimation(objet);
        Glow glow = new Glow(0.6);
        objet.setEffect(glow);
        score += 1;
        scoreLabel.setText((int) (score) + " / " + total_hidden);
        icon.setOpacity(0.2);
        progressBar.setProgress(score / 10);
        if (score == 10) {
            showWinMessage();
        }
    }

    private void hiddenAnimation(Node objet) {
        RotateTransition rt = new RotateTransition(Duration.millis(1200), objet);
        rt.setFromAngle(0);
        rt.setToAngle(360);

        ScaleTransition st = new ScaleTransition(Duration.millis(1200), objet);
        st.setToY(1.0f);
        st.setToX(1.0f);
        st.setFromY(2.0f);
        st.setFromX(2.0f);

        ParallelTransition pt = new ParallelTransition(rt, st);
        URL soundUrl = getClass().getResource("/com/alexdube/hiddenpictures/sounds/found.mp3");
        if (soundUrl == null) {
            throw new IllegalArgumentException("Son de succes n'a pas ete trouvee.");
        }
        AudioClip foundSound = new AudioClip(soundUrl.toExternalForm());
        foundSound.setVolume(0.2);
        foundSound.play();
        pt.play();
    }


    @FXML
    public void clicCounter() {
        if (loseMessageBox.isVisible() ||winMessageBox.isVisible()) {
            return;
        }
        URL soundUrl = getClass().getResource("/com/alexdube/hiddenpictures/sounds/error.mp3");
        if (soundUrl == null) {
            throw new IllegalArgumentException("Son d'erreur n'a pas ete trouvee.");
        }
        AudioClip errorSound = new AudioClip(soundUrl.toExternalForm());
        errorSound.setVolume(0.5);
        errorSound.play();
        error += 1;
        errorLabel.setText((error + " / " + maxError));
        if (error > maxError) {
            showLoseMessage();
        }
    }

    @FXML private void handleReturn() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }

}
