package com.example.peaky.model.equipment;

import java.util.List;

public class EquipmentCategory {
    private String name;
    private List<EquipmentType> types;

    public EquipmentCategory(String name, List<EquipmentType> types) {
        this.name = name;
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public List<EquipmentType> getTypes() {
        return types;
    }


}

