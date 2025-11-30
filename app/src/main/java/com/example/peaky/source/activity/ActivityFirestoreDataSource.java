package com.example.peaky.source.activity;

import android.location.Location;
import android.util.Log;

import com.example.peaky.model.Activity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityFirestoreDataSource {

    private FirebaseFirestore firestore;

    public ActivityFirestoreDataSource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public void addActivity(String userId, Activity activity) {
        DocumentReference activityRef;

        activityRef = firestore.collection("users")
                .document(userId)
                .collection("activities")
                .document();
        activity.setId(activityRef.getId());


        // --- convertiamo i punti GPS in lista di mappe ---
        List<Map<String, Double>> pointsList = new ArrayList<>();
        if (activity.getPoints() != null) {
            for (Location loc : activity.getPoints()) {
                Map<String, Double> pointMap = new HashMap<>();
                pointMap.put("lat", loc.getLatitude());
                pointMap.put("lon", loc.getLongitude());
                pointsList.add(pointMap);
            }
        }

        // Creiamo una copia dell'activity per Firestore
        Map<String, Object> activityMap = new HashMap<>();
        activityMap.put("id", activity.getId());
        activityMap.put("userId", activity.getUserId());
        activityMap.put("name", activity.getName());
        activityMap.put("sport", activity.getSport());
        activityMap.put("duration", activity.getDuration());
        activityMap.put("distance", activity.getDistance());
        activityMap.put("startTime", activity.getStartTime());
        activityMap.put("elevationGain", activity.getElevationGain());
        activityMap.put("elevationLoss", activity.getElevationLoss());
        activityMap.put("description", activity.getDescription());
        activityMap.put("points", pointsList);  // lista di mappe {"lat":..., "lon":...}

        // --- salva su Firestore ---
        activityRef.set(activityMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ActivityRepository", "Activity saved successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.d("ActivityRepository", "Error saving activity", e);
                });
    }


}
