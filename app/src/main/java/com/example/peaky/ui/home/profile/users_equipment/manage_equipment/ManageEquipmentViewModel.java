/*
package com.example.peaky.ui.home.profile.users_equipment.manage_equipment;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Sport;
import com.example.peaky.model.equipment.Equipment;
import com.example.peaky.repository.equipment.EquipmentRepository;
import com.example.peaky.repository.sport.SportRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageEquipmentViewModel extends ViewModel {

    private final EquipmentRepository equipmentRepository;
    private final SportRepository sportRepository;
    private final MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> equiopmentCategoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> equipmentTypesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> buttonMarginBottom = new MutableLiveData<>(16);

    private Equipment equipmentToEdit;

    public ManageEquipmentViewModel(EquipmentRepository repository, SportRepository sportRepository) {
        this.equipmentRepository = repository;
        this.sportRepository = sportRepository;
        loadCategories();
        loadSports();
    }

    private void loadSports() {
        List<Sport> sports = sportRepository.getSports();
        sportsLiveData.setValue(sports);
    }

    public LiveData<List<Sport>> getSports() {
        return sportsLiveData;
    }

    private void loadCategories() {
        equiopmentCategoriesLiveData.setValue(equipmentRepository.getCategories());
    }

    public void onCategorySelected(String category) {
        equipmentTypesLiveData.setValue(equipmentRepository.getTypesForCategory(category));
    }

    public LiveData<ArrayList<String>> getCategories() {
        return equiopmentCategoriesLiveData;
    }

    public LiveData<ArrayList<String>> getTypes() {
        return equipmentTypesLiveData;
    }
    public void adjustButtonPosition(View bottomSheet) {
        int marginBottom = 16; // Margine di default

        if (bottomSheet.getHeight() > 0) {
            marginBottom += bottomSheet.getHeight();
        }

        buttonMarginBottom.setValue(marginBottom);
    }

    public void resetButtonPosition() {
        buttonMarginBottom.setValue(16);
    }

    public void addEquipment(String userId, Equipment equipment) {
        equipmentRepository.addEquipment(userId, equipment);
    }

    public void setDefaultEquipmentForSport(String sportName, String type, Equipment equipment) {
        sportRepository.setDefaultEquipmentForSport(sportName, type, equipment.getId(), equipment.getUserId());
    }

    public void setEquipmentToEdit(Equipment equipment) {
        this.equipmentToEdit = equipment;
    }

    public Equipment getEquipmentToEdit() {
        return equipmentToEdit;
    }

    public void updateEquipment(String userId, Equipment updatedEquipment) {
        equipmentRepository.updateEquipment(userId, updatedEquipment);
    }
}


 */