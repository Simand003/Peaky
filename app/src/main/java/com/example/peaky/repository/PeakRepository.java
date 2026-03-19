package com.example.peaky.repository;

import com.example.peaky.model.Peak;
import com.example.peaky.source.peak.PeakDataSource;

public class PeakRepository {

    private final PeakDataSource peakDataSource;

    public PeakRepository(PeakDataSource peakDataSource) {
        this.peakDataSource = peakDataSource;
    }

    public void addPeak(Peak peak) {
        peakDataSource.addPeak(peak);
    }

}
