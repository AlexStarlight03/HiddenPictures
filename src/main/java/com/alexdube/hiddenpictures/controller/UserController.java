package com.alexdube.hiddenpictures.controller;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import com.alexdube.hiddenpictures.HiddenObjectsApp;
import com.alexdube.hiddenpictures.model.User;
import com.alexdube.hiddenpictures.service.ApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserController implements Initializable {
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private PasswordField confirmPasswordField;
    ApiService apiService = new ApiService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        messageLabel.setVisible(false);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        messageLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setVisible(true);
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setVisible(true);
            messageLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }
        boolean success = apiService.createUser(username, password);
        if (success) {
            User user = apiService.getUserByUsername(username);
            if (user != null) {
                Session.setCurrentUser(user);
            }
            HiddenObjectsApp.switchPage("fxml/home_view.fxml");
        } else {
            messageLabel.setVisible(true);
            messageLabel.setText("Erreur : ce nom d'utilisateur est déjà pris!");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        messageLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setVisible(true);
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }
        User user = apiService.login(username, password);
        if (user != null) {
            Session.setCurrentUser(user);
            HiddenObjectsApp.switchPage("fxml/home_view.fxml");
        } else {
            messageLabel.setVisible(true);
            messageLabel.setText("Identifiants incorrects.");
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

    @FXML
    private void handleReturn() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }

    @FXML
    private void switchRegister() {
        HiddenObjectsApp.switchPage("fxml/register_view.fxml");
    }

}
