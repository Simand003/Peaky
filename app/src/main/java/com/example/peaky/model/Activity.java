package com.example.peaky.model;

public class Activity {

    private String uid;
    private String name;// nome dell'attività (es: "Corsa")
    private Sport sport;
    private long duration;      // durata in millisecondi
    private double distance;    // distanza in km
    private long startTime;     // quando è stata registrata
    private double elevationGain;
    private double elevationLoss;
    private String description;

    public Activity(String uid, String name, Sport sport, long duration, double distance, long startTime,
                    double elevationGain, double elevationLoss, String description) {
        this.uid = uid;
        this.name = name;
        this.sport=sport;
        this.duration = duration;
        this.distance = distance;
        this.startTime = startTime;
        this.elevationGain = elevationGain;
        this.elevationLoss = elevationLoss;
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public double getElevationLoss() {
        return elevationLoss;
    }

    public void setElevationLoss(double elevationLoss) {
        this.elevationLoss = elevationLoss;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "uid='" + uid + '\'' +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                ", startTime=" + startTime +
                ", elevationGain=" + elevationGain +
                ", elevationLoss=" + elevationLoss +
                ", description='" + description + '\'' +
                '}';
    }
}

