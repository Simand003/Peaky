package com.example.peaky.ui.home.record_activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.ActivityRepository;

public class ActivityDataRecordedViewModelFactory implements ViewModelProvider.Factory {

    private final ActivityRepository activityRepository;

    public ActivityDataRecordedViewModelFactory(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ActivityDataRecordedViewModel.class)) {
            return (T) new ActivityDataRecordedViewModel(activityRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
