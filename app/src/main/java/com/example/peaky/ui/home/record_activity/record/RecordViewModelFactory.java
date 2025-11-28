package com.example.peaky.ui.home.record_activity.record;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.sport.SportRepository;

public class RecordViewModelFactory implements ViewModelProvider.Factory {

    private final OSMRepository osmRepository;

    public RecordViewModelFactory(OSMRepository osmRepository) {
        this.osmRepository = osmRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(osmRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}