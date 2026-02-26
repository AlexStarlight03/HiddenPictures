package com.alexdube.hiddenpictures.controller;

import com.alexdube.hiddenpictures.model.Game;
import com.alexdube.hiddenpictures.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private final static ObservableList<Game> gameList = FXCollections.observableArrayList();

    @FXML private TableView<Game> tableView;
    @FXML
    private TableColumn<Game, Integer> idCol;
    @FXML private TableColumn<Game, Integer> userIdCol;
    @FXML private TableColumn<Game, Integer> scoreCol;
    @FXML private TableColumn<Game, LocalDateTime> playedAtCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        playedAtCol.setCellValueFactory(new PropertyValueFactory<>("playedAt"));
        tableView.setItems(gameList);
        loadGames();
    }

    @FXML
    public static void addGame(int userId, int score ) {
        try {
            String sql = "INSERT INTO games (user_id, score, played_at) VALUES (?, ?, ?)";
            try (Connection conn = ApiClient.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, score);
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                int result = stmt.executeUpdate();
                if (result > 0) {
                    System.out.println("Partie ajoutée !");
                    loadGames();
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Erreur de format d'input : " + e.getMessage());
        }
    }

    public static void loadGames() {
        gameList.clear();
        String sql = "SELECT * FROM games ORDER BY played_at";
        try (Connection conn = ApiClient.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Game g = new Game(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("score"),
                        rs.getTimestamp("played_at").toLocalDateTime()
                );
                gameList.add(g);
            }
            System.out.println("* " + gameList.size() + " parties chargées");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
