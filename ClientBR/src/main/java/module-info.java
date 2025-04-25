module org.example.bookreccomender2 {
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires jdk.jshell;
    requires org.kordamp.ikonli.javafx;
    requires javafx.controls;

    opens org.example.bookreccomender2 to javafx.fxml;
    exports org.example.bookreccomender2;
    exports org.example.bookreccomender2.controller;
    opens org.example.bookreccomender2.controller to javafx.fxml;
}