package com.ll.clockclient;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class AlarmNotification extends MFXSimpleNotification {
    private final StringProperty headerText = new SimpleStringProperty("Alarm");
    private final StringProperty contentText = new SimpleStringProperty();

    public AlarmNotification(Alarm alarm, ObservableList<Alarm> alarms) {

        MFXFontIcon fi = new MFXFontIcon();

        fi.setDescription("mfx-bell");
        fi.setSize(16);
        MFXIconWrapper icon = new MFXIconWrapper(fi, 32);
        Label headerLabel = new Label();
        headerLabel.textProperty().bind(headerText);

        HBox header = new HBox(10, icon, headerLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(InsetsFactory.of(10, 0, 10, 0));
        header.setMaxWidth(Double.MAX_VALUE);

        Label contentLabel = new Label();
        contentLabel.getStyleClass().add("content");
        contentLabel.textProperty().bind(contentText);
        contentLabel.setWrapText(true);
        contentLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentLabel.setAlignment(Pos.TOP_LEFT);

        MFXButton action1 = new MFXButton("Dismiss");
        MFXButton action2 = new MFXButton("Snooze");
        HBox actionsBar = new HBox(15, action1, action2);
        actionsBar.getStyleClass().add("actions-bar");
        actionsBar.setAlignment(Pos.CENTER_RIGHT);
        actionsBar.setPadding(InsetsFactory.all(5));

        action1.setOnAction((ActionEvent e) -> {
            alarm.dismiss();
            MFXNotificationCenterSystem.instance().getCenter().dismiss(this);
        });

        action2.setOnAction((ActionEvent e) -> {
            alarm.snooze();
            MFXNotificationCenterSystem.instance().getCenter().dismiss(this);
            new Thread(() -> {
                Platform.runLater(() -> {
                    alarms.add(alarm);
                });
            }).start();
        });

        BorderPane container = new BorderPane();
        container.getStyleClass().add("notification");
        container.setTop(header);
        container.setCenter(contentLabel);
        container.setBottom(actionsBar);
        container.getStylesheets().add(String.valueOf(AlarmNotification.class.getResource("main.css")));
        container.setMinHeight(200);
        //container.setMargin(actionsBar, InsetsFactory.of(0, 0, 10, 0));
        container.setMinWidth(400);

        setContent(container);
    }

    public String getHeaderText() {
        return headerText.get();
    }

    public StringProperty headerTextProperty() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText.set(headerText);
    }

    public String getContentText() {
        return contentText.get();
    }

    public StringProperty contentTextProperty() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText.set(contentText);
    }
}
