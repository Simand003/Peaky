/*
package com.example.peaky.ui.home.record_activity.saveactivity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.ActivityRepository;
import com.example.peaky.repository.sport.SportRepository;

public class SaveActivityViewModelFactory implements ViewModelProvider.Factory{

    private final SportRepository sportRepository;
    private final ActivityRepository activityRepository;

    public SaveActivityViewModelFactory(SportRepository sportRepository, ActivityRepository activityRepository) {
        this.sportRepository = sportRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SaveActivityViewModel.class)) {
            return (T) new SaveActivityViewModel(sportRepository, activityRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


 */