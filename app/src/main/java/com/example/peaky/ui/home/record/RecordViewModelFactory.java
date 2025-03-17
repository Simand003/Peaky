package com.example.peaky.ui.home.record;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.SportRepository;

public class RecordViewModelFactory implements ViewModelProvider.Factory {
    private SportRepository sportRepository;

    public RecordViewModelFactory(SportRepository repository) {
        this.sportRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(sportRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

