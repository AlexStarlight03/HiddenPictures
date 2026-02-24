package com.alexdube.hiddenpictures;

import com.alexdube.hiddenpictures.model.User;
import com.alexdube.hiddenpictures.service.ApiService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.sql.*;

public class HomePage implements Initializable {

    @FXML private VBox mainMenu;
    @FXML private ImageView kirakira;
    @FXML private Button play_btn;
    @FXML private Button about_btn;
    @FXML private Button creator_btn;
    @FXML private Button leaderboard_btn;
    @FXML private Button register_btn;
    @FXML private Button login_btn;
    @FXML private Button logout_btn;
    @FXML private Label userInfoLabel;
    private final ApiService apiService = new ApiService();

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        mainMenu.setOpacity(0);
        mainMenu.setTranslateY(60);

        FadeTransition ft = new FadeTransition(Duration.millis(900), mainMenu);
        ft.setFromValue(0);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(900), mainMenu);
        tt.setFromY(60);
        tt.setToY(0);

        FadeTransition kiraFade = new FadeTransition(Duration.millis(1500), kirakira);
        kiraFade.setFromValue(1.0);
        kiraFade.setToValue(0.2);

        ParallelTransition pt = new ParallelTransition(ft, tt, kiraFade);
        pt.play();

        boolean loggedIn = Session.getCurrentUser() != null;
        login_btn.setVisible(!loggedIn);
        register_btn.setVisible(!loggedIn);
        logout_btn.setVisible(loggedIn);
        userInfoLabel.setVisible(loggedIn);

        if (loggedIn) {
            User user = Session.getCurrentUser();
            String username = user.getUsername();
            ApiService.BestScore bestScore = apiService.getBestScore(user.getId());
            if (bestScore != null) {
                userInfoLabel.setText("Connecté: " + username + " | Meilleur score: " + bestScore.score + " (" + bestScore.date + ")");
            } else {
                userInfoLabel.setText("Connecté: " + username + " | Aucun score enregistré");
            }
        }
    }

    @FXML
    private void handlePlay() {
        if (Session.getCurrentUser() == null) {
            HiddenObjectsApp.switchPage("fxml/login_view.fxml");
        } else {
            HiddenObjectsApp.switchPage("fxml/play_view.fxml");
        }
    }

    @FXML
    private void handleLeaderboard() {
        HiddenObjectsApp.switchPage("fxml/leaderboard_view.fxml");
    }

    @FXML
    private void handleAbout() {
        HiddenObjectsApp.switchPage("fxml/about_view.fxml");
    }

    @FXML
    private void handleCreator() {
        HiddenObjectsApp.switchPage("fxml/creator_view.fxml");
    }

    @FXML
    private void handleRegister() { HiddenObjectsApp.switchPage("fxml/register_view.fxml");}

    @FXML
    private void handleLogin() { HiddenObjectsApp.switchPage("fxml/login_view.fxml");}

    @FXML
    private void handleLogout() {
        Session.setCurrentUser(null);
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }

}
