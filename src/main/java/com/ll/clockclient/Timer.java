package com.ll.clockclient;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class Timer {

    DateTimeFormatter format;
    Instant started;
    public final long duration;//seconds
    public Timer(int hour, int min, int sec) {
        duration = hour * 3600L + min * 60L + sec;
        format = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    public Timer start(){
        started = Instant.now();
        return this;
    }

    public long getRemainingTime(){
        return duration - getElapsedTime();
    }

    public long getElapsedTime() {
        if(started != null) {
            return Duration.between(started, Instant.now()).toSeconds();
        }
        else return duration;
    }

    public boolean isExpired() {
        return getElapsedTime() > duration;
    }

    @Override
    public String toString() {
        long remainingTime = getRemainingTime();
        return (int) remainingTime / 60 / 60 + ":" + ((int) remainingTime / 60) % 60 + ":" + (int) remainingTime % 60;
    }
}
