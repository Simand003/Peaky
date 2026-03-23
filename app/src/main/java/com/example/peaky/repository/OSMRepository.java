package com.example.peaky.repository;

import com.example.peaky.source.osm.OSMDataSource;

public class OSMRepository {

    private OSMDataSource dataSource;

    public OSMRepository(OSMDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getPeaksInArea(double north, double south, double east, double west,
                               OSMDataSource.Callback callback) {
        dataSource.getPeaksInArea(north, south, east, west, callback);
    }
}

