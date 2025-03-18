package com.example.peaky.source.osm;

import com.example.peaky.model.MapData;

import org.osmdroid.util.GeoPoint;

public class OSMDataSource {

    public interface OnMapDataFetchedListener {
        void onSuccess(GeoPoint location);
        void onFailure(String error);
    }

    public void fetchDataFromOSM(OnMapDataFetchedListener listener) {
        try {
            // Simula il recupero dei dati (es. Milano)
            GeoPoint data = new GeoPoint(45.4642, 9.1900);
            listener.onSuccess(data);
        } catch (Exception e) {
            listener.onFailure("Errore nel recupero dei dati");
        }
    }
}



