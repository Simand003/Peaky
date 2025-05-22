package com.example.peaky.source;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportDataSource {

    private final FirebaseFirestore firestore;

    public SportDataSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public interface OnSportsResultListener {
        void onResult(List<String> sports);
        void onError(Exception e);
    }

    public interface OnCompleteListener {
        void onComplete();
        void onError(Exception e);
    }

    public void setDefaultEquipmentForSport(String sportName, String type, String equipmentId, String userId) {
        DocumentReference sportRef = firestore.collection("users")
                .document(userId)
                .collection("sports")
                .document(sportName);

        // Prima leggi il documento per recuperare la mappa esistente (se presente)
        sportRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> defaultEquipment = new HashMap<>();

            if (documentSnapshot.exists()) {
                Map<String, Object> existingMap = (Map<String, Object>) documentSnapshot.get("defaultEquipment");
                if (existingMap != null) {
                    defaultEquipment.putAll(existingMap);
                }
            }

            // Aggiorna/aggiungi la nuova associazione
            defaultEquipment.put(type, equipmentId);

            // Scrivi la mappa aggiornata nel documento
            Map<String, Object> update = new HashMap<>();
            update.put("defaultEquipment", defaultEquipment);

            sportRef.set(update, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Sport updated"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating sport", e));
        }).addOnFailureListener(e -> Log.e("Firestore", "Error reading sport", e));
    }


    public void getSportsWhereEquipmentIsDefault(String userId, String equipmentId, OnSportsResultListener listener) {
        firestore.collection("users")
                .document(userId)
                .collection("sports")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> sportsWithEquipment = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> defaultEquipmentMap = (Map<String, Object>) doc.get("defaultEquipment");
                        if (defaultEquipmentMap != null && defaultEquipmentMap.containsValue(equipmentId)) {
                            sportsWithEquipment.add(doc.getId());
                        }
                    }
                    listener.onResult(sportsWithEquipment);
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    public void removeEquipmentFromDefaultInSports(String userId, String equipmentId, OnCompleteListener listener) {
        firestore.collection("users")
                .document(userId)
                .collection("sports")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DocumentSnapshot> docsToUpdate = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> defaultEquipmentMap = (Map<String, Object>) doc.get("defaultEquipment");

                        if (defaultEquipmentMap != null && defaultEquipmentMap.containsValue(equipmentId)) {
                            docsToUpdate.add(doc);
                        }
                    }

                    if (docsToUpdate.isEmpty()) {
                        listener.onComplete();
                        return;
                    }

                    // Ora aggiorna ogni sport eliminando la chiave corrispondente
                    for (DocumentSnapshot doc : docsToUpdate) {
                        Map<String, Object> defaultEquipmentMap = (Map<String, Object>) doc.get("defaultEquipment");
                        String sportId = doc.getId();
                        DocumentReference sportRef = firestore.collection("users")
                                .document(userId)
                                .collection("sports")
                                .document(sportId);

                        // Trova la chiave da rimuovere
                        String keyToRemove = null;
                        for (Map.Entry<String, Object> entry : defaultEquipmentMap.entrySet()) {
                            if (equipmentId.equals(entry.getValue())) {
                                keyToRemove = entry.getKey();
                                break;
                            }
                        }

                        if (keyToRemove != null) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("defaultEquipment." + keyToRemove, com.google.firebase.firestore.FieldValue.delete());

                            sportRef.update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        docsToUpdate.remove(doc);
                                        if (docsToUpdate.isEmpty()) {
                                            listener.onComplete();
                                        }
                                    })
                                    .addOnFailureListener(listener::onError);
                        }
                    }

                })
                .addOnFailureListener(listener::onError);
    }


}