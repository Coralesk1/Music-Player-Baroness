module com.otaviogustavo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.ionicons4;

    opens com.otaviogustavo.controllers to javafx.fxml;
    exports com.otaviogustavo;
}
