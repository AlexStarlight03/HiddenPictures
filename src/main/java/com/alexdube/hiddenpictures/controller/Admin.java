package com.alexdube.hiddenpictures.controller;

import com.alexdube.hiddenpictures.HiddenObjectsApp;
import com.alexdube.hiddenpictures.model.User;
import com.alexdube.hiddenpictures.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Admin {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, Boolean> isAdminCol;
    @FXML private TextField searchUserField;

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final ApiService apiService = new ApiService();

    @FXML
    private void initialize() {
        isAdminCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().getIsAdmin()));
        usernameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        userTable.setItems(userList);
        loadAllUsers();
    }

    @FXML
    private void handleUpdateUsername() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        TextInputDialog dialog = new TextInputDialog(selected.getUsername());
        dialog.setHeaderText("Nouveau nom d'utilisateur:");
        dialog.setTitle("Modifier le nom d'utilisateur");
        dialog.setContentText("Nom d'utilisateur:");
        dialog.showAndWait().ifPresent(newUsername -> {
            boolean ok = apiService.updateUser(selected.getId(), newUsername, null);
            if (ok) {
                showAlert("Succès", "Nom d'utilisateur mis à jour !");
                loadAllUsers();
            } else {
                showAlert("Erreur", "Échec de la mise à jour.");
            }
        });
    }

    @FXML
    private void handleToggleAdminStatus() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        boolean newStatus = !selected.getIsAdmin();
        boolean ok = apiService.updateAdminStatus(selected.getId(), newStatus);
        if (ok) {
            showAlert("Succès", "Statut admin modifié !");
            loadAllUsers();
        } else {
            showAlert("Erreur", "Échec de la modification.");
        }
    }

    @FXML
    private void handleResetPassword() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        boolean ok = apiService.updateUser(selected.getId(), null, "reset");
        if (ok) {
            showAlert("Succès", "Mot de passe réinitialisé à 'reset'.");
        } else {
            showAlert("Erreur", "Échec de la réinitialisation.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer l'utilisateur et toutes ses parties ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                boolean ok = apiService.deleteUser(selected.getId());
                if (ok) {
                    showAlert("Succès", "Utilisateur supprimé !");
                    loadAllUsers();
                } else {
                    showAlert("Erreur", "Échec de la suppression.");
                }
            }
        });
    }

    @FXML
    private void handleSearchUser() {
        String search = searchUserField.getText().trim();
        userList.clear();
        userList.addAll(apiService.getUserByUsername(search));
        userTable.setItems(userList);
    }

    @FXML
    private void handleShowAllUsers() {
        loadAllUsers();
    }

    private void loadAllUsers() {
        userList.clear();
        userList.addAll(apiService.getAllUsers());
        userTable.setItems(userList);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    @FXML
    private void handleReturn() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }
}