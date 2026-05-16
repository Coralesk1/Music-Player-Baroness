module com.otaviogustavo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.ionicons4;
    requires mp3agic;
    requires com.google.gson;
    requires javafx.media;

    opens com.otaviogustavo.controllers to javafx.fxml;
    opens com.otaviogustavo to com.google.gson;
    exports com.otaviogustavo;

}
