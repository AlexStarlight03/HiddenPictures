module com.alexdube.hiddenpictures {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.alexdube.hiddenpictures to javafx.fxml;
    exports com.alexdube.hiddenpictures;
}