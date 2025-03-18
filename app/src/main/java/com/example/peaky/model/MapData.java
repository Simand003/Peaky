package com.example.peaky.model;

import org.osmdroid.util.GeoPoint;

public class MapData {
    private final GeoPoint location;

    public MapData(GeoPoint location) {
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }
}
