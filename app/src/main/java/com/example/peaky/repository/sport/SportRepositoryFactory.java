package com.example.peaky.repository.sport;

import android.content.Context;

import com.example.peaky.source.SportDataSource;
import com.google.firebase.firestore.FirebaseFirestore;

public class SportRepositoryFactory {

    private static SportRepository sportRepository;

    public static SportRepository getSportRepository(Context context) {
        if (sportRepository == null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            SportDataSource sportDataSource = new SportDataSource(firestore);

            sportRepository = new SportRepository(context.getApplicationContext(),
                    sportDataSource);
        }
        return sportRepository;
    }
}
