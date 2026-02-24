package com.alexdube.hiddenpictures;

import com.alexdube.hiddenpictures.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class Profil implements Initializable {
    @FXML private Label usernameLabel;
    @FXML private Label gamesCountLabel;
    @FXML private Label bestScoreLabel;
    @FXML private Label avgScoreLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button updateBtn;
    @FXML private Label messageLabel;

    private final ApiService apiService = new ApiService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Session.getCurrentUser() == null) {
            usernameLabel.setText("Non connecté");
            gamesCountLabel.setText("");
            bestScoreLabel.setText("");
            avgScoreLabel.setText("");
            updateBtn.setDisable(true);
            return;
        }
        int userId = Session.getCurrentUser().getId();
        String username = Session.getCurrentUser().getUsername();
        usernameLabel.setText("Pseudo : " + username);
        usernameField.setText(username);
        gamesCountLabel.setText("Nombre de parties : " + apiService.getGamesCount(userId));
        bestScoreLabel.setText("Meilleur score : " + apiService.getBestScore(userId));
        avgScoreLabel.setText("Score moyen : " + String.format("%.2f", apiService.getAvgScore(userId)));
    }

    @FXML
    private void handleUpdate() {
        if (Session.getCurrentUser() == null) return;
        int userId = Session.getCurrentUser().getId();
        String newUsername = usernameField.getText().trim();
        String newPassword = passwordField.getText().trim();
        boolean updated = apiService.updateProfile(userId, newUsername, newPassword);
        if (updated) {
            messageLabel.setText("Profil mis à jour !");
            if (!newUsername.isEmpty()) Session.getCurrentUser().setUsername(newUsername);
        } else {
            messageLabel.setText("Aucune modification.");
        }
    }
}