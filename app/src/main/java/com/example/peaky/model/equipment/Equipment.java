package com.example.peaky.model.equipment;

import com.example.peaky.model.Sport;

import java.util.Date;
import java.util.List;

public class Equipment {

    private EquipmentType type;
    private String brand;
    private String model;
    private Date purchase_date;
    private Double price;
    private List<Sport> default_sports;
    private String notes;
    private double distance;
    private double elevation_gain;
    private int uses;
    private int peaks_reached;

    public Equipment (EquipmentType type, String brand, String model, Date purchase_date, double price, List<Sport> default_sports, String notes, double distance, double elevation_gain, int uses, int peaks_reached) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.purchase_date = purchase_date;
        this.price = price;
        this.default_sports = default_sports;
        this.elevation_gain = elevation_gain;
        this.uses = uses;
        this.peaks_reached = peaks_reached;
        this.notes = notes;
        this.distance = distance;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getPurchaseDate() {
        return purchase_date;
    }

    public void setPurchase_date(Date purchase_date) {
        this.purchase_date = purchase_date;
    }

    public List<Sport> getDefault_sports() {
        return default_sports;
    }

    public void setDefault_sports(List<Sport> default_sports) {
        this.default_sports = default_sports;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getElevation_gain() {
        return elevation_gain;
    }

    public void setElevation_gain(double elevation_gain) {
        this.elevation_gain = elevation_gain;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public int getPeaks_reached() {
        return peaks_reached;
    }

    public void setPeaks_reached(int peaks_reached) {
        this.peaks_reached = peaks_reached;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
