package com.alexdube.hiddenpictures.controller;

import com.alexdube.hiddenpictures.HiddenObjectsApp;
import com.alexdube.hiddenpictures.Session;
import com.alexdube.hiddenpictures.model.User;
import com.alexdube.hiddenpictures.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private PasswordField confirmPasswordField;

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
        String hashedPassword = hashPassword(password);

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();

            String sqlSelect = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setString(1, username);
                ResultSet rs = stmtSelect.executeQuery();
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                    Session.setCurrentUser(user);
                }
            }
            HiddenObjectsApp.switchPage("fxml/home_view.fxml");
        } catch (SQLException e) {
            messageLabel.setVisible(true);
            messageLabel.setText("Erreur: Ce nom d'utilisateur est deja prit!");
        }
    }

    private void loadUsers() {
        userList.clear();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                userList.add(u);
            }
            System.out.println("* " + userList.size() + " utilisateurs chargés");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
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
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setString(1, username);
             stmt.setString(2, hashedPassword); // Hash in production!
             ResultSet rs = stmt.executeQuery();
             if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                Session.setCurrentUser(user);
                messageLabel.setVisible(true);
                messageLabel.setText("Connexion réussie !");
                HiddenObjectsApp.switchPage("fxml/home_view.fxml");
             } else {
                 messageLabel.setVisible(true);
                 messageLabel.setText("Identifiants incorrects.");
             }
        } catch (SQLException e) {
            messageLabel.setVisible(true);
            messageLabel.setText("Erreur: " + e.getMessage());
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
    private void handleBack() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }

    @FXML
    private void handleInscription() { HiddenObjectsApp.switchPage("fxml/register_view.fxml");}
}
