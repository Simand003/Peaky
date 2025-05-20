package com.example.peaky.ui.home.profile.users_equipment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.equipment.Equipment;
import com.example.peaky.repository.equipment.EquipmentRepository;
import com.example.peaky.source.EquipmentDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ViewModel che segue il flusso ViewModel -> Repository -> DataSource -> Firebase
public class UsersEquipmentViewModel extends ViewModel {

    private final EquipmentRepository repository;

    private final MutableLiveData<Map<String, List<Equipment>>> equipmentByType = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UsersEquipmentViewModel(EquipmentRepository repository) {
        this.repository = repository;
    }

    public LiveData<Map<String, List<Equipment>>> getEquipmentByType() {
        return equipmentByType;
    }

    public void loadUserEquipment(String userId) {
        loading.setValue(true);
        repository.getUserEquipment(userId, new EquipmentDataSource.EquipmentCallback() {
            @Override
            public void onSuccess(List<Equipment> equipmentList) {
                Map<String, List<Equipment>> grouped = new HashMap<>();

                for (Equipment e : equipmentList) {
                    String typeName = e.getType().getName();
                    String category = repository.getCategoryForType(typeName);
                    if (category == null) {
                        Log.w("ViewModel", "Tipo sconosciuto: " + typeName);
                        continue;
                    }
                    grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(e);
                }

                equipmentByType.postValue(grouped);
            }

            @Override
            public void onError(Exception e) {
                Log.e("ViewModel", "Errore nel caricamento: ", e);
            }
        });
    }
}
