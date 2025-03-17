package com.example.peaky.ui.home.record;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Sport;
import com.example.peaky.repository.SportRepository;

import java.util.List;

public class RecordViewModel extends ViewModel {
    private MutableLiveData<List<Sport>> sportsListLiveData;
    private SportRepository sportRepository;

    public RecordViewModel(SportRepository repository) {
        sportsListLiveData = new MutableLiveData<>();
        sportRepository = repository;
    }

    public LiveData<List<Sport>> getSportsList() {
        return sportsListLiveData;
    }

    public void loadSports(Context context) {
        List<Sport> sportsList = sportRepository.getSports(context);
        sportsListLiveData.setValue(sportsList);
    }
}

