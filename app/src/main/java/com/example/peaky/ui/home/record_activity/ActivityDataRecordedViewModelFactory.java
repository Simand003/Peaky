package com.example.peaky.ui.home.record_activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.ActivityRepository;
import com.example.peaky.repository.sport.SportRepository;

public class ActivityDataRecordedViewModelFactory implements ViewModelProvider.Factory {

    private final ActivityRepository activityRepository;
    private final SportRepository sportRepository;

    public ActivityDataRecordedViewModelFactory(ActivityRepository activityRepository, SportRepository sportRepository) {
        this.activityRepository = activityRepository;
        this.sportRepository = sportRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ActivityDataRecordedViewModel.class)) {
            return (T) new ActivityDataRecordedViewModel(activityRepository, sportRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
