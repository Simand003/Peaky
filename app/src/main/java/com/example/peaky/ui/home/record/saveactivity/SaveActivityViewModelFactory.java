package com.example.peaky.ui.home.record.saveactivity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.sport.SportRepository;

public class SaveActivityViewModelFactory implements ViewModelProvider.Factory{

    private final SportRepository sportRepository;

    public SaveActivityViewModelFactory(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SaveActivityViewModel.class)) {
            return (T) new SaveActivityViewModel(sportRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
