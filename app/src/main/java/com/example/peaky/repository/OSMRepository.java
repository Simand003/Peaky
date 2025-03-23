package com.example.peaky.repository;

import com.example.peaky.source.osm.OSMDataSource;

public class OSMRepository {

    private final OSMDataSource osmDataSource;

    public OSMRepository(OSMDataSource osmDataSource) {
        this.osmDataSource = osmDataSource;
    }


}

