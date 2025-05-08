package com.example.peaky.model.equipment;

import java.util.List;

public class EquipmentCategory {
    private String name;
    private int icon;
    private List<EquipmentType> types;

    public EquipmentCategory(String name, int icon, List<EquipmentType> types) {
        this.name = name;
        this.types = types;
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

    public List<EquipmentType> getTypes() {
        return types;
    }

    public void setTypes(List<EquipmentType> types) {
        this.types = types;
    }
}