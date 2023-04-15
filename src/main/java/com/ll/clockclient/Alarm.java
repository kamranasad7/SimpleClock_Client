package com.ll.clockclient;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Alarm {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
    LocalDateTime dateTime;
    String alarmSoundSrc;
    Media sound;
    MediaPlayer soundPlayer;
    public Alarm(LocalDateTime when, String soundSrc) {
        dateTime = when;
        alarmSoundSrc = soundSrc;
        sound = new Media(new File(soundSrc).toURI().toString());
        soundPlayer = new MediaPlayer(sound);
    }

    public boolean isFired(ZoneId timeZone) {
        return LocalDateTime.now(timeZone).isAfter(dateTime);
    }

    public void play(){
        soundPlayer.play();
    }
    public void dismiss() {
        soundPlayer.stop();
    }
    public void snooze() {
        soundPlayer.stop();
        dateTime = dateTime.plusMinutes(5);
    }

    @Override
    public String toString() {
        return dateTime.getDayOfWeek().name() + ", " +
                dateTime.getMonth().name() + " " +
                dateTime.getDayOfMonth() + " " +
                dateTime.getYear() + " - " +
                formatter.format(dateTime);
    }
}
