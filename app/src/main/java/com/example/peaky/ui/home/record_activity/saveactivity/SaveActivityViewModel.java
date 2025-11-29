/*
package com.example.peaky.ui.home.record_activity.saveactivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Sport;
import com.example.peaky.repository.ActivityRepository;
import com.example.peaky.repository.sport.SportRepository;

import java.util.List;

public class SaveActivityViewModel extends ViewModel {

    private final SportRepository sportRepository;
    private final ActivityRepository activityRepository;

    private MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();

    public SaveActivityViewModel(SportRepository sportRepository, ActivityRepository activityRepository) {
        this.sportRepository = sportRepository;
        this.activityRepository = activityRepository;
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

 */