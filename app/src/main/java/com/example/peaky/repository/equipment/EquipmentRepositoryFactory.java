package com.example.peaky.repository.equipment;

import android.content.Context;

import com.example.peaky.source.EquipmentDataSource;
import com.google.firebase.firestore.FirebaseFirestore;

public class EquipmentRepositoryFactory {

    private static EquipmentRepository equipmentRepository;

    public static EquipmentRepository getEquipmentRepository(Context context) {
        if (equipmentRepository == null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            EquipmentDataSource equipmentDataSource = new EquipmentDataSource(firestore);

            equipmentRepository = new EquipmentRepository(context.getApplicationContext(),
                    equipmentDataSource);
        }
        return equipmentRepository;
    }
}