package com.example.peaky.model;

public class Activity {

    private String name;// nome dell'attività (es: "Corsa")
    private Sport sport;
    private long duration;      // durata in millisecondi
    private double distance;    // distanza in km
    private long startTime;     // quando è stata registrata

    public Activity(String name, Sport sport, long duration, double distance, long startTime) {
        this.name = name;
        this.sport=sport;
        this.duration = duration;
        this.distance = distance;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                ", startTime=" + startTime +
                '}';
    }
}

