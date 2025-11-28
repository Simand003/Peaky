package com.example.peaky.repository;

import android.content.Context;

import com.example.peaky.model.Activity;
import com.example.peaky.source.activity.ActivityFirestoreDataSource;
import com.example.peaky.source.activity.ActivityLocalDataSource;

public class ActivityRepository {

    private final Context context;
    private final ActivityFirestoreDataSource activityFirestoreDataSource;
    private final ActivityLocalDataSource activityLocalDataSource;

    public ActivityRepository(Context context, ActivityFirestoreDataSource activityFirestoreDataSource,
                              ActivityLocalDataSource activityLocalDataSource) {
        this.context = context;
        this.activityFirestoreDataSource = activityFirestoreDataSource;
        this.activityLocalDataSource = activityLocalDataSource;
    }

    public void addActivity(String userId, Activity activity) {
        activityFirestoreDataSource.addActivity(userId, activity);
    }
}
