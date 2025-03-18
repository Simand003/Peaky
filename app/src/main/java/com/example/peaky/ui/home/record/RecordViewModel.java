package com.example.peaky.ui.home.record;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Sport;
import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.SportRepository;
import com.example.peaky.source.osm.OSMDataSource;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class RecordViewModel extends ViewModel {

    private final MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();
    private final MutableLiveData<GeoPoint> mapLocationLiveData = new MutableLiveData<>();
    private final SportRepository sportRepository;
    private final OSMRepository osmRepository;

    public RecordViewModel(SportRepository sportRepository, OSMRepository mapRepository) {
        this.sportRepository = sportRepository;
        this.osmRepository = mapRepository;
        loadSports();
        loadMapData();
    }

    private void loadSports() {
        List<Sport> sports = sportRepository.getSports();
        sportsLiveData.setValue(sports);
    }

    private void loadMapData() {
        osmRepository.getMapData(new OSMDataSource.OnMapDataFetchedListener() {
            @Override
            public void onSuccess(GeoPoint location) {
                mapLocationLiveData.setValue(location);
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    public LiveData<List<Sport>> getSports() {
        return sportsLiveData;
    }

    public LiveData<GeoPoint> getMapLocation() {
        return mapLocationLiveData;
    }
}
