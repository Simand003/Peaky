package com.example.peaky.ui.home.profile.users_equipment.selected_equipment;

import androidx.lifecycle.ViewModel;

import com.example.peaky.repository.equipment.EquipmentRepository;
import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.source.EquipmentDataSource;
import com.example.peaky.source.SportDataSource;

public class SelectedEquipmentViewModel extends ViewModel {

    private SportRepository sportRepository;
    private EquipmentRepository equipmentRepository;

    public SelectedEquipmentViewModel(SportRepository sportRepository, EquipmentRepository equipmentRepository) {
        this.sportRepository = sportRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public void removeEquipmentFromDefaultInSports(String userId, String equipmentId, SportDataSource.OnCompleteListener listener) {
        sportRepository.removeEquipmentFromDefaultInSports(userId, equipmentId, listener);
    }

    public void deleteEquipment(String userId, String equipmentId, EquipmentDataSource.OnDeleteListener listener) {
        equipmentRepository.deleteEquipment(userId, equipmentId, listener);
    }
}