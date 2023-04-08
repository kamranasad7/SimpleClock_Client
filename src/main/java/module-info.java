module com.ll.clockclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX;
    requires javafx.media;


    opens com.ll.clockclient to javafx.fxml;
    opens com.ll.clockclient.controllers to javafx.fxml;
    exports com.ll.clockclient;
    exports com.ll.clockclient.controllers;
}