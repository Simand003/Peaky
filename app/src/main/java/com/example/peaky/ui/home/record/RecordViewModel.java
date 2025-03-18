package com.example.peaky.ui.home.record;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Sport;
import com.example.peaky.repository.SportRepository;

import java.util.List;

public class RecordViewModel extends ViewModel {
    private final MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();
    private final SportRepository sportRepository;

    public RecordViewModel(SportRepository repository) {
        this.sportRepository = repository;
        loadSports();
    }

    private void loadSports() {
        List<Sport> sports = sportRepository.getSports();
        sportsLiveData.setValue(sports);
    }

    public LiveData<List<Sport>> getSports() {
        return sportsLiveData;
    }
}

