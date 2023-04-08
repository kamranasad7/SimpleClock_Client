package com.ll.clockclient.controllers;

import com.ll.clockclient.Alarm;
import com.ll.clockclient.Timer;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainController {

    DatagramSocket socket = new DatagramSocket();
    ZoneId timeZone = ZoneId.systemDefault();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");


    Media ding = new Media(new File("ding.mp3").toURI().toString());
    MediaPlayer dingPlayer = new MediaPlayer(ding);
    public MainController() throws SocketException { }

    @FXML
    public void closeApp() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void minimizeApp() {
        Stage stage = (Stage) time.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void settingsPopup() {
        settingsDialog.setVisible(true);
    }

    @FXML
    public void closeSettings() {
        settingsDialog.setVisible(false);
    }

    @FXML
    private void timezoneClick() {
        timezoneDropdown.show();
    }

    @FXML
    private void saveSettings() {
        if(timezoneDropdown.getSelectedItem() != null){
            timeZone = ZoneId.of(timezoneDropdown.getSelectedItem());
        }
        settingsDialog.setVisible(false);
    }

    @FXML
    private void addTimer() {
        int h, m, s;
        try {
            if(timerHourInput.getText() == null || timerHourInput.getText().isBlank()) {
                timerHourInput.setText("0");
            }
            if(timerMinuteInput.getText() == null || timerMinuteInput.getText().isBlank()) {
                timerMinuteInput.setText("0");
            }
            if(timerSecondInput.getText() == null || timerSecondInput.getText().isBlank()) {
                timerSecondInput.setText("0");
            }
            h = Integer.parseInt(timerHourInput.getText());
            m = Integer.parseInt(timerMinuteInput.getText());
            s = Integer.parseInt(timerSecondInput.getText());
        }
        catch (Exception e) { return; }
        if(h+m+s == 0) { return; }

        Timer timer = new Timer(h, m, s).start();
        timers.add(timer.toString());
        timerObjs.add(timer);
    }

    @FXML
    private void selectSound() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Music Files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(time.getScene().getWindow());
        soundButton.setText(selectedFile.getName());
        selectedSoundPath = selectedFile.getAbsolutePath();
    }

    @FXML void addAlarm() {
        int h, m, s;
        try {
            if(alarmHourInput.getText() == null || alarmHourInput.getText().isBlank()) {
                alarmHourInput.setText("0");
            }
            if(alarmMinuteInput.getText() == null || alarmMinuteInput.getText().isBlank()) {
                alarmMinuteInput.setText("0");
            }
            if(alarmSecondInput.getText() == null || alarmSecondInput.getText().isBlank()) {
                alarmSecondInput.setText("0");
            }
            h = Integer.parseInt(alarmHourInput.getText());
            m = Integer.parseInt(alarmMinuteInput.getText());
            s = Integer.parseInt(alarmSecondInput.getText());
        }
        catch (Exception e) { return; }
        LocalTime time = LocalTime.of(h, m, s);
        Alarm alarm = new Alarm(LocalDateTime.of(alarmDate.getValue(), time), selectedSoundPath);
        alarms.add(alarm);
    }

    @FXML
    private MFXButton clockPageBtn;
    @FXML
    private MFXButton alarmsPageBtn;
    @FXML
    private MFXButton timerPageBtn;
    @FXML
    private AnchorPane clockPage;
    @FXML
    private AnchorPane alarmsPage;
    @FXML
    private AnchorPane timerPage;
    @FXML
    private Label time;
    @FXML
    private Label date;
    @FXML
    private Label lastSynced;

    @FXML
    private MFXGenericDialog settingsDialog;
    @FXML
    private MFXFilterComboBox<String> timezoneDropdown;
    ObservableList<String> timezones;


    //Timer Page
    @FXML
    private MFXTextField timerHourInput;
    @FXML
    private MFXTextField timerMinuteInput;
    @FXML
    private MFXTextField timerSecondInput;
    @FXML
    private MFXListView<String> timersListView;
    ObservableList<String> timers;
    ObservableList<Timer> timerObjs;


    //Alarm Page
    @FXML
    private MFXTextField alarmHourInput;
    @FXML
    private MFXTextField alarmMinuteInput;
    @FXML
    private MFXTextField alarmSecondInput;
    @FXML
    private DatePicker alarmDate;

    @FXML
    private MFXButton soundButton;
    String selectedSoundPath = "DefaultAlarm.mp3";
    @FXML
    private MFXListView<Alarm> alarmsListView;
    ObservableList<Alarm> alarms;
    @FXML
    public void initialize() {

        clockPageBtn.setOnAction(actionEvent -> {
            clockPage.setVisible(true);
            alarmsPage.setVisible(false);
            timerPage.setVisible(false);
        });
        alarmsPageBtn.setOnAction(actionEvent -> {
            clockPage.setVisible(false);
            alarmsPage.setVisible(true);
            timerPage.setVisible(false);
        });
        timerPageBtn.setOnAction(actionEvent -> {
            clockPage.setVisible(false);
            alarmsPage.setVisible(false);
            timerPage.setVisible(true);
        });


        //TIME
        updateTime();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(this::updateTime);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        //Server Sync
        getServerTime();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 5);
                    Platform.runLater(this::getServerTime);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
/*
        timezones = FXCollections.observableArrayList();
        timezones.addAll(ZoneId.getAvailableZoneIds());
        timezoneDropdown.setItems(timezones);
        timezoneDropdown.promptTextProperty().setValue(timeZone.toString());
*/

        //TIMER PAGE
        timerHourInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                timerHourInput.setText(oldValue);
            }
        });
        timerMinuteInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                timerMinuteInput.setText(oldValue);
            }
        });
        timerSecondInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                timerSecondInput.setText(oldValue);
            }
        });

        timers = FXCollections.observableArrayList();
        timerObjs = FXCollections.observableArrayList();
        timersListView.setItems(timers);


        new Thread(() -> {
            while (true) {
                try {
                    Platform.runLater(() -> {
                        for (int i = 0; i < timers.size(); i++) {
                            if(timerObjs.get(i).isExpired()) {
                                timers.remove(i); timerObjs.remove(i);
                                dingPlayer.play();
                            }
                            else timers.set(i, timerObjs.get(i).toString());
                        }
                    });
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        //ALARM PAGE
        alarmDate.setValue(LocalDate.now());
        alarmHourInput.setText(String.valueOf(LocalTime.now().getHour()));
        alarms = FXCollections.observableArrayList();
        alarmsListView.setItems(alarms);

        new Thread(() -> {
            while (true) {
                try {
                    Platform.runLater(() -> {
                        for (Alarm alarm : alarms) {
                            if (alarm.isFired()) {
                                //timers.remove(i); timerObjs.remove(i);
                                alarm.play();
                            }
                        }
                    });
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }

    private void updateTime() {
        setTime(LocalDateTime.now(timeZone));
    }

    private void setTime(LocalDateTime t) {
        time.setText(formatter.format(t));
        date.setText(t.getDayOfWeek().name() + ", " + t.getMonth().name() + " " + t.getDayOfMonth() + " " + t.getYear());
    }

    public void getServerTime() {
        try {
            InetAddress serverIP = InetAddress.getByName("localhost");
            int serverPort = 8000;

            byte[] reqBody = timeZone.getId().getBytes();

            DatagramPacket req = new DatagramPacket(reqBody, reqBody.length, serverIP, serverPort);
            socket.send(req);

            byte[] buff = new byte[100];
            DatagramPacket res = new DatagramPacket(buff, buff.length);
            socket.receive(res);

            String time = new String(buff, 0, res.getLength());
            LocalDateTime serverTime = LocalDateTime.parse(time);
            setTime(serverTime);
            lastSynced.setText("Last Synced with Time Server: " + formatter.format(serverTime));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
