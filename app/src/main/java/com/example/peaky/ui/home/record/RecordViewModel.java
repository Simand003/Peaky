package com.example.peaky.ui.home.record;

import static com.example.peaky.ui.home.record.RecordFragment.FOUND;
import static com.example.peaky.ui.home.record.RecordFragment.NOT_FOUND;
import static com.example.peaky.ui.home.record.RecordFragment.SEARCHING;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.peaky.R;
import com.example.peaky.model.Sport;
import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.SportRepository;

import java.util.List;

import androidx.lifecycle.ViewModel;

public class RecordViewModel extends ViewModel {

    private final SportRepository sportRepository;
    private final OSMRepository osmRepository;

    public RecordViewModel(SportRepository sportRepository, OSMRepository osmRepository) {
        this.sportRepository = sportRepository;
        this.osmRepository = osmRepository;
        loadSports();
    }

    private void loadSports() {
        List<Sport> sports = sportRepository.getSports();
    }

    public LiveData<List<Sport>> getSports() {
        return new MutableLiveData<>(); // ritorna la lista di sport
    }

    public int getBackgroundColor(int stato) {
        switch (stato) {
            case 1: return R.color.green;
            case 2: return R.color.md_theme_error;
            default: return R.color.md_theme_primary;
        }
    }

    public String getTextForState(int stato) {
        switch (stato) {
            case 1: return FOUND;
            case 2: return NOT_FOUND;
            default: return SEARCHING;
        }
    }
}


