package com.example.peaky.ui.home.record_activity.record;

import static com.example.peaky.ui.home.record_activity.record.RecordFragment.FOUND;
import static com.example.peaky.ui.home.record_activity.record.RecordFragment.NOT_FOUND;
import static com.example.peaky.ui.home.record_activity.record.RecordFragment.SEARCHING;

import android.location.Location;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.peaky.R;
import com.example.peaky.model.Sport;
import com.example.peaky.repository.OSMRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;

import org.osmdroid.util.GeoPoint;

public class RecordViewModel extends ViewModel {

    private final OSMRepository osmRepository;
    private final MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> buttonMarginBottom = new MutableLiveData<>(16);

    public RecordViewModel(OSMRepository osmRepository) {
        this.osmRepository = osmRepository;
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

    public void adjustButtonPosition(View bottomSheet) {
        View parent = (View) bottomSheet.getParent();

        int offset = parent.getHeight() - bottomSheet.getTop();
        if (offset < 0) offset = 0;

        buttonMarginBottom.setValue(offset + 16);
    }

    public void resetButtonPosition() {
        buttonMarginBottom.setValue(16);
    }
}



