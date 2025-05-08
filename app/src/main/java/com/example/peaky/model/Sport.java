package com.example.peaky.model;

import com.example.peaky.model.equipment.Equipment;
import com.example.peaky.model.equipment.EquipmentCategory;

import java.util.HashMap;
import java.util.Map;

public class Sport {
    private String name;
    private int icon;
    private Map<String, Equipment> defaultEquipment; // Mappa che tiene traccia degli equipaggiamenti per categoria

    public Sport(String name, int icon) {
        this.name = name;
        this.icon = icon;
        this.defaultEquipment = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setDefaultEquipment(String type, Equipment equipment) {
        defaultEquipment.put(type, equipment);
    }

    public Equipment getDefaultEquipment(String type) {
        return defaultEquipment.get(type);
    }

    public Map<String, Equipment> getAllDefaultEquipment() {
        return defaultEquipment;
    }
}
