module org.example.bookrecommender2 {
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jdk.jshell;
    requires org.kordamp.ikonli.javafx;
    requires javafx.controls;

    opens bookrecommender to javafx.fxml;
    exports bookrecommender;
    exports bookrecommender.controller;
    opens bookrecommender.controller to javafx.fxml;
}