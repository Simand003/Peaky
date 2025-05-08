package com.example.peaky.ui.home.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.equipment.EquipmentRepository;
import com.example.peaky.repository.sport.SportRepository;

public class ManageEquipmentViewModelFactory implements ViewModelProvider.Factory {

    private final EquipmentRepository equipmentRepository;
    private final SportRepository sportRepository;

    public ManageEquipmentViewModelFactory(EquipmentRepository equipmentRepository, SportRepository sportRepository) {
        this.equipmentRepository = equipmentRepository;
        this.sportRepository = sportRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ManageEquipmentViewModel.class)) {
            return (T) new ManageEquipmentViewModel(equipmentRepository, sportRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

