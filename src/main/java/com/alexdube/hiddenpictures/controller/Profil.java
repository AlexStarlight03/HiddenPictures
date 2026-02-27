package com.alexdube.hiddenpictures.controller;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import com.alexdube.hiddenpictures.HiddenObjectsApp;
import com.alexdube.hiddenpictures.service.ApiService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        ApiService.BestScore bestScore = apiService.getBestScore(userId);
        if (bestScore != null) {
            bestScoreLabel.setText("Meilleur score : " + bestScore.score + " (" + bestScore.date + ")");
        } else {
            bestScoreLabel.setText("Meilleur score : Aucun score enregistré");
        }
        avgScoreLabel.setText("Score moyen : " + String.format("%.2f", apiService.getAvgScore(userId)));
    }

    @FXML
    private void handleUpdate() {
        if (Session.getCurrentUser() == null) return;
        int userId = Session.getCurrentUser().getId();
        String newUsername = usernameField.getText().trim();
        String newPassword = passwordField.getText().trim();
        String hashedNewPassword = hashPassword(newPassword);
        boolean updated = apiService.updateUser(userId, newUsername, newPassword);
        if (updated) {
            messageLabel.setText("Profil mis à jour !");
            if (!newUsername.isEmpty()) Session.getCurrentUser().setUsername(newUsername);
            if (!hashedNewPassword.isEmpty()) Session.getCurrentUser().setPassword(hashedNewPassword);
        } else {
            messageLabel.setText("Aucune modification.");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur de hashage : " + e.getMessage());
            return password;
        }
    }

    @FXML private void handleReturn() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }
}