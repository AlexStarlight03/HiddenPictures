package com.alexdube.hiddenpictures;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ResourceBundle;

public class HomePage implements Initializable {

    @FXML
    private VBox mainMenu;
    @FXML
    private ImageView kirakira;

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
    }


    @FXML
    private Button play_btn;
    @FXML
    private Button about_btn;
    @FXML
    private Button creator_btn;

    @FXML
    private void handlePlay() {
        HiddenObjectsApp.switchPage("fxml/play_view.fxml");
    }

    @FXML
    private void handleAbout() {
        HiddenObjectsApp.switchPage("fxml/about_view.fxml");
    }

    @FXML
    private void handleCreator() {
        HiddenObjectsApp.switchPage("fxml/creator_view.fxml");
    }

}
