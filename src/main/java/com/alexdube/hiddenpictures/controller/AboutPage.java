package com.alexdube.hiddenpictures.controller;

import com.alexdube.hiddenpictures.HiddenObjectsApp;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ResourceBundle;

public class AboutPage implements Initializable {

    @FXML
    private VBox mainBox;

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        mainBox.setOpacity(0);
        mainBox.setTranslateY(60);

        FadeTransition ft = new FadeTransition(Duration.millis(900), mainBox);
        ft.setFromValue(0);
        ft.setToValue(1);

        ft.play();
    }


    @FXML
    private Button home_btn;

    @FXML
    private void handleReturn() {
        HiddenObjectsApp.switchPage("fxml/home_view.fxml");
    }


}
