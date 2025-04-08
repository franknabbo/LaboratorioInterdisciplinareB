module org.example.bookreccomender2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example.bookreccomender2 to javafx.fxml;
    exports org.example.bookreccomender2;
}