package com.example.peaky.model;

public class Peak {
    public String id;
    public String name;
    public double latitude;
    public double longitude;
    public int altitude;
    public int numeroSaliteTotali;
    public int numeroUtenti;

    public Peak (String name, double latitude, double longitude, int altitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.numeroSaliteTotali = 0;
        this.numeroUtenti = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getNumeroSaliteTotali() {
        return numeroSaliteTotali;
    }

    public void setNumeroSaliteTotali(int numeroSaliteTotali) {
        this.numeroSaliteTotali = numeroSaliteTotali;
    }

    public int getNumeroUtenti() {
        return numeroUtenti;
    }

    public void setNumeroUtenti(int numeroUtenti) {
        this.numeroUtenti = numeroUtenti;
    }
}