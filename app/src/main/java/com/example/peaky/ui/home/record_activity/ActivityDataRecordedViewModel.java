package com.example.peaky.ui.home.record_activity;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.peaky.repository.ActivityRepository;

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

    private ActivityRepository activityRepository;

    public ActivityDataRecordedViewModel(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
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

    /*
    public void resetRecording() {
        handler.removeCallbacks(timerRunnable);
        isRecording.setValue(false);
        elapsedTime.setValue(0L);
        startTimestamp.setValue(0L);
    }

     */
}
