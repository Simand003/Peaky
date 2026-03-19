package com.example.peaky.source.peak;

import com.example.peaky.model.Peak;
import com.google.firebase.firestore.FirebaseFirestore;

public class PeakDataSource {
    private final FirebaseFirestore firestore;

    public PeakDataSource() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void addPeak (Peak peak) {
    }
}