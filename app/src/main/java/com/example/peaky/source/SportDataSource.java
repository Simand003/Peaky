package com.example.peaky.source;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SportDataSource {

    private final FirebaseFirestore firestore;

    public SportDataSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public void setDefaultEquipmentForSport(String sportName, String type, String equipmentId, String userId) {
        DocumentReference sportRef = firestore.collection("users")
                .document(userId)
                .collection("sports")
                .document(sportName);

        Map<String, Object> update = new HashMap<>();
        update.put("defaultEquipment." + type, equipmentId);

        sportRef.set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Sport updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating sport", e));
    }
}

