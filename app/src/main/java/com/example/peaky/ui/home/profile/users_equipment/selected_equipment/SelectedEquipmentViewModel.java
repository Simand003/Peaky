package com.example.peaky.ui.home.profile.users_equipment.selected_equipment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.source.SportDataSource;

import java.util.List;

public class SelectedEquipmentViewModel extends ViewModel {

    private SportRepository sportRepository;

    public SelectedEquipmentViewModel(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    public void getAssociatedSports(String userId, String equipmentId, SportDataSource.OnSportsResultListener listener) {
        sportRepository.getSportsWhereEquipmentIsDefault(userId, equipmentId, listener);
    }
}