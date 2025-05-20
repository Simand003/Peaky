package com.example.peaky.model.equipment;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Equipment implements Serializable {

    private String id;
    private String userId;
    private EquipmentType type;
    private String brand;
    private String model;
    private Date purchase_date;
    private Double price;
    private String notes;
    private double distance;
    private double elevation_gain;
    private int uses;
    private int peaks_reached;

    public Equipment() {}

    public Equipment (String id, String userId, EquipmentType type, String brand, String model, Date purchase_date, double price, String notes, double distance, double elevation_gain, int uses, int peaks_reached) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.purchase_date = purchase_date;
        this.price = price;
        this.elevation_gain = elevation_gain;
        this.uses = uses;
        this.peaks_reached = peaks_reached;
        this.notes = notes;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(Date purchase_date) {
        this.purchase_date = purchase_date;
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

    public String getFormattedPurchaseDate() {
        if (purchase_date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(purchase_date);
    }
}
