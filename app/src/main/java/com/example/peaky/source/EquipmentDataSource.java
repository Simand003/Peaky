package com.example.peaky.source;

import android.util.Log;

import com.example.peaky.model.equipment.Equipment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EquipmentDataSource {

    private final FirebaseFirestore firestore;

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
                    .collection("equipaggiamenti") // Sottocartella per gli equipaggiamenti
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
}
