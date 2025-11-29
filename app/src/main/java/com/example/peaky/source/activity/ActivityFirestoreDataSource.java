package com.example.peaky.source.activity;

import android.util.Log;

import com.example.peaky.model.Activity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityFirestoreDataSource {

    private FirebaseFirestore firestore;

    public ActivityFirestoreDataSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public void addActivity(String userId, Activity activity) {
        DocumentReference activityRef;
        if (activity.getId() == null) {
            activityRef = firestore.collection("users")
                    .document(userId)
                    .collection("activities")
                    .document();
            activity.setId(activityRef.getId());
        } else {
            activityRef = firestore.collection("users")
                    .document(userId)
                    .collection("activities")
                    .document(activity.getId());;
        }

        activityRef.set(activity)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ActivityRepository", "Equipment saved successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.d("EquipmentRepository", "Error saving activity");
                });
    }
}
