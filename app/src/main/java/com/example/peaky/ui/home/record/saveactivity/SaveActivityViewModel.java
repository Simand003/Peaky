package com.example.peaky.ui.home.record.saveactivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Sport;
import com.example.peaky.repository.sport.SportRepository;

import java.util.List;

public class SaveActivityViewModel extends ViewModel {

    private final SportRepository sportRepository;

    private MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();

    public SaveActivityViewModel(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
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