package com.example.peaky.repository;

import com.example.peaky.source.osm.OSMDataSource;

public class OSMRepository {

    private OSMDataSource dataSource;

    public OSMRepository(OSMDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getNearbyPeaks(double lat, double lon, OSMDataSource.Callback callback) {
        dataSource.getPeaks(lat, lon, callback);
    }
}

