package com.example.peaky.ui.home.record;

import static com.example.peaky.ui.home.record.RecordFragment.FOUND;
import static com.example.peaky.ui.home.record.RecordFragment.NOT_FOUND;
import static com.example.peaky.ui.home.record.RecordFragment.SEARCHING;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.peaky.R;
import com.example.peaky.model.Sport;
import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.sport.SportRepository;

import java.util.List;

import androidx.lifecycle.ViewModel;

public class RecordViewModel extends ViewModel {

    private final SportRepository sportRepository;
    private final OSMRepository osmRepository;
    private final MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> buttonMarginBottom = new MutableLiveData<>(16);

    private final MutableLiveData<Boolean> isRecording = new MutableLiveData<>(false);
    private final MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);

    private long lastStartTimestamp = 0L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long current = System.currentTimeMillis();
            long updated = elapsedTime.getValue() + (current - lastStartTimestamp);
            elapsedTime.setValue(updated);
            lastStartTimestamp = current;

            handler.postDelayed(this, 1000); // aggiorna ogni secondo
        }
    };

    public RecordViewModel(SportRepository sportRepository, OSMRepository osmRepository) {
        this.sportRepository = sportRepository;
        this.osmRepository = osmRepository;
        loadSports();
    }

    private void loadSports() {
        List<Sport> sports = sportRepository.getSports();
        sportsLiveData.setValue(sports);
    }

    public LiveData<List<Sport>> getSports() {
        return sportsLiveData;
    }

    public LiveData<Integer> getButtonMarginBottom() {
        return buttonMarginBottom;
    }

    public int getBackgroundColor(int stato) {
        switch (stato) {
            case 1: return R.color.green;
            case 2: return R.color.md_theme_error;
            default: return R.color.md_theme_primary;
        }
    }

    public String getTextForState(int stato) {
        switch (stato) {
            case 1: return FOUND;
            case 2: return NOT_FOUND;
            default: return SEARCHING;
        }
    }

    // 👉 FIX: usa posizione reale del bottomsheet, non la height
    public void adjustButtonPosition(View bottomSheet) {
        View parent = (View) bottomSheet.getParent();

        int offset = parent.getHeight() - bottomSheet.getTop();
        if (offset < 0) offset = 0;

        buttonMarginBottom.setValue(offset + 16);
    }

    public void resetButtonPosition() {
        buttonMarginBottom.setValue(16);
    }

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }

    // START registrazione
    public void startRecording() {
        if (Boolean.TRUE.equals(isRecording.getValue())) return;

        lastStartTimestamp = System.currentTimeMillis();
        handler.post(timerRunnable);
        isRecording.setValue(true);
    }

    // PAUSE registrazione
    public void stopRecording() {
        if (!Boolean.TRUE.equals(isRecording.getValue())) return;

        handler.removeCallbacks(timerRunnable);
        long current = System.currentTimeMillis();
        long updated = elapsedTime.getValue() + (current - lastStartTimestamp);
        elapsedTime.setValue(updated);

        isRecording.setValue(false);
    }

    // Resetta tutto (se utile in futuro)
    public void reset() {
        handler.removeCallbacks(timerRunnable);
        isRecording.setValue(false);
        elapsedTime.setValue(0L);
    }
}


