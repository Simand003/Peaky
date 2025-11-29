package com.example.peaky.ui.home.record_activity;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.model.Activity;
import com.example.peaky.model.Sport;
import com.example.peaky.repository.ActivityRepository;
import com.example.peaky.repository.sport.SportRepository;

import java.util.List;

public class ActivityDataRecordedViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isRecording = new MutableLiveData<>(false);
    private final MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> startTimestamp = new MutableLiveData<>(0L);

    private long lastStartTimestamp = 0L;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long current = System.currentTimeMillis();
            long updated = elapsedTime.getValue() + (current - lastStartTimestamp);
            elapsedTime.setValue(updated);
            lastStartTimestamp = current;

            handler.postDelayed(this, 1000);
        }
    };

    public LiveData<Boolean> getIsRecording() { return isRecording; }
    public LiveData<Long> getElapsedTime() { return elapsedTime; }
    public LiveData<Long> getStartTimestamp() { return startTimestamp; }

    private final MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();

    private final ActivityRepository activityRepository;
    private final SportRepository sportRepository;

    public ActivityDataRecordedViewModel(ActivityRepository activityRepository, SportRepository sportRepository) {
        this.activityRepository = activityRepository;
        this.sportRepository = sportRepository;
        loadSports();
    }

    public void startRecording() {
        if (Boolean.TRUE.equals(isRecording.getValue())) return;

        long now = System.currentTimeMillis();

        // set start only the first time
        if (startTimestamp.getValue() == 0L) {
            startTimestamp.setValue(now);
        }

        lastStartTimestamp = now;
        handler.post(timerRunnable);
        isRecording.setValue(true);
    }

    public void stopRecording() {
        if (!Boolean.TRUE.equals(isRecording.getValue())) return;

        handler.removeCallbacks(timerRunnable);
        long current = System.currentTimeMillis();
        long updated = elapsedTime.getValue() + (current - lastStartTimestamp);
        elapsedTime.setValue(updated);

        isRecording.setValue(false);
    }

    private void loadSports() {
        List<Sport> sports = sportRepository.getSports();
        sportsLiveData.setValue(sports);
    }

    public LiveData<List<Sport>> getSports() {
        return sportsLiveData;
    }

    public void saveActivity(String userId, String name, String sport, String description) {
        Activity activity = new Activity();
        activity.setUserId(userId);
        if (name.isEmpty() || name.isBlank()){
            activity.setName("New activity");
        } else
            activity.setName(name);
        if (sport.isEmpty() || sport.isBlank()){
            activity.setName("Hiking");
        } else
            activity.setSport(sport);
        activity.setDuration(elapsedTime.getValue());
        // DISTANCE
        activity.setStartTime(startTimestamp.getValue());
        // ELEVATION GAIN
        // ELEVATION LOSS
        activity.setDescription(description);
        activityRepository.addActivity(userId, activity);
    }

    /*
    public void resetRecording() {
        handler.removeCallbacks(timerRunnable);
        isRecording.setValue(false);
        elapsedTime.setValue(0L);
        startTimestamp.setValue(0L);
    }

     */
}
