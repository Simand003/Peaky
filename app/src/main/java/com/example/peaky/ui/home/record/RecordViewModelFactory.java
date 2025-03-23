package com.example.peaky.ui.home.record;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.SportRepository;

import android.content.Context;

public class RecordViewModelFactory implements ViewModelProvider.Factory {

    private final SportRepository sportRepository;
    private final OSMRepository osmRepository;

    public RecordViewModelFactory(SportRepository sportRepository, OSMRepository osmRepository) {
        this.sportRepository = sportRepository;
        this.osmRepository = osmRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(sportRepository, osmRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

