package com.example.peaky.ui.home.saveactivity;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.peaky.R;

public class SaveActivityFragment extends Fragment {

    private SaveActivityViewModel saveActivityViewModel;

    public static SaveActivityFragment newInstance() {
        return new SaveActivityFragment();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_activity, container, false);

        return view;
    }


}