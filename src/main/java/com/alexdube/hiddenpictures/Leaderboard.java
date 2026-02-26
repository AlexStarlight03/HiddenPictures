package com.alexdube.hiddenpictures;

import com.alexdube.hiddenpictures.model.LeaderboardEntry;
import com.alexdube.hiddenpictures.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class Leaderboard implements Initializable {
    private final ObservableList<LeaderboardEntry> leaderboardList = FXCollections.observableArrayList();
    private final ApiService apiService = new ApiService();

    @FXML private TableView<LeaderboardEntry> leaderboardTable;
    @FXML private TableColumn<LeaderboardEntry, Integer> rankCol;
    @FXML private TableColumn<LeaderboardEntry, String> usernameCol;
    @FXML private TableColumn<LeaderboardEntry, Integer> scoreCol;
    @FXML private TableColumn<LeaderboardEntry, String> dateCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        leaderboardTable.setItems(leaderboardList);

        loadLeaderboard("g.score DESC, g.played_at ASC");

    }
    private void loadLeaderboard(String orderBy) {
        leaderboardList.clear();
        leaderboardList.addAll(apiService.getLeaderboard(orderBy));
    }

    @FXML
    private void handleReturn() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }
}