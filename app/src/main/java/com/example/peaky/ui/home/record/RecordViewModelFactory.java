package com.example.peaky.ui.home.record;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.SportRepository;

public class RecordViewModelFactory implements ViewModelProvider.Factory {

    private final SportRepository sportRepository;
    private final OSMRepository osmRepository;  // Aggiungi qui altri repository, come il repository della mappa

    public RecordViewModelFactory(SportRepository sportRepository, OSMRepository mapRepository) {
        this.sportRepository = sportRepository;
        this.osmRepository = mapRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(sportRepository, osmRepository);  // Passa qui tutti i repository necessari
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
