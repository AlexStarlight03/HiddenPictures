module com.alexdube.hiddenpictures {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires java.sql;


    opens com.alexdube.hiddenpictures to javafx.fxml;
    exports com.alexdube.hiddenpictures;
    exports com.alexdube.hiddenpictures.model;
    opens com.alexdube.hiddenpictures.model to javafx.fxml;
    exports com.alexdube.hiddenpictures.controller;
    opens com.alexdube.hiddenpictures.controller to javafx.fxml;
}