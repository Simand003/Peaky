package com.example.peaky.source;

import android.util.Log;

import com.example.peaky.model.equipment.Equipment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDataSource {

    private final FirebaseFirestore firestore;

    public interface EquipmentCallback {
        void onSuccess(List<Equipment> equipmentList);
        void onError(Exception e);
    }

    public interface OnDeleteListener {
        void onDeleted();
        void onError(Exception e);
    }

    public EquipmentDataSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public void addEquipment(String userId, Equipment equipment) {
        // Verifica se l'equipaggiamento ha un ID già presente
        DocumentReference equipmentRef;
        if (equipment.getId() == null) {
            // Se l'ID è null, Firestore genera un ID automatico
            equipmentRef = firestore.collection("users")
                    .document(userId)  // Usa l'ID dell'utente
                    .collection("equipments") // Sottocartella per gli equipaggiamenti
                    .document();  // Firestore genera un ID
            equipment.setId(equipmentRef.getId()); // Imposta l'ID generato
        } else {
            // Se l'ID è già presente, usalo
            equipmentRef = firestore.collection("users")
                    .document(userId)
                    .collection("equipments")
                    .document(equipment.getId());
        }

        // Aggiungi l'ID dell'utente all'equipaggiamento
        equipment.setUserId(userId);

        // Salva l'equipaggiamento nel database
        equipmentRef.set(equipment)
                .addOnSuccessListener(aVoid -> {
                    // Successo, esegui eventuali azioni
                    Log.d("EquipmentRepository", "Equipment saved successfully.");
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore
                    Log.e("EquipmentRepository", "Error saving equipment: ", e);
                });
    }

    public void getUserEquipment(String userId, EquipmentCallback callback) {
        firestore.collection("users")
                .document(userId)
                .collection("equipments")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Equipment> equipmentList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Equipment e = doc.toObject(Equipment.class);
                        equipmentList.add(e);
                    }
                    callback.onSuccess(equipmentList);
                })
                .addOnFailureListener(callback::onError);
    }

    public void deleteEquipment(String userId, String equipmentId, OnDeleteListener listener) {
        firestore.collection("users")
                .document(userId)
                .collection("equipments")
                .document(equipmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("EquipmentDataSource", "Equipment deleted successfully.");
                    listener.onDeleted();
                })
                .addOnFailureListener(e -> {
                    Log.e("EquipmentDataSource", "Error deleting equipment", e);
                    listener.onError(e);
                });
    }
}
