module com.ll.clockclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ll.clockclient to javafx.fxml;
    exports com.ll.clockclient;
}