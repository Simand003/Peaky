package com.example.peaky.ui.login.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.user.UserRepositoryFactory;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(UserRepositoryFactory.getUserRepository());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}