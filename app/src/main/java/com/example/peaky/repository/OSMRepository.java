package com.example.peaky.repository;

import com.example.peaky.source.osm.OSMDataSource;

public class OSMRepository {

    private final OSMDataSource osmDataSource;

    public OSMRepository(OSMDataSource osmDataSource) {
        this.osmDataSource = osmDataSource;
    }

    // Metodo per ottenere i dati dalla fonte (OSMDataSource)
    public void getMapData(OSMDataSource.OnMapDataFetchedListener listener) {
        osmDataSource.fetchDataFromOSM(listener);
    }
}
