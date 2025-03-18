package com.example.peaky.ui.home.record;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.peaky.R;
import com.example.peaky.adapter.SportAdapter;
import com.example.peaky.model.Sport;
import com.example.peaky.repository.SportRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class RecordFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private Spinner spinner;

    private Button buttonBack;
    private ImageButton buttonRecordAction, buttonRecordEnd, buttonRecordedData, buttonReporterTools;
    private View bottomSheetData, bottomSheetReporter;
    private BottomSheetBehavior<View> bottomSheetDataBehavior, bottomSheetReporterBehavior;

    private boolean isRecording = false;

    private SportRepository sportRepository;
    private RecordViewModel recordViewModel;

    public RecordFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        //Initializing the buttons
        buttonBack = view.findViewById(R.id.button_back);
        buttonRecordAction = view.findViewById(R.id.button_record_action);
        buttonRecordEnd = view.findViewById(R.id.button_record_end);
        buttonRecordedData = view.findViewById(R.id.button_recorded_data);
        buttonReporterTools = view.findViewById(R.id.button_reporters_tool);

        //Initializing the bottom sheets
        bottomSheetData = view.findViewById(R.id.bottomSheet_data);
        bottomSheetDataBehavior = BottomSheetBehavior.from(bottomSheetData);
        bottomSheetReporter = view.findViewById(R.id.bottomSheet_reporter);
        bottomSheetReporterBehavior = BottomSheetBehavior.from(bottomSheetReporter);

        //Setting the behavior of the bottom sheets
        bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        spinner = view.findViewById(R.id.spinner_sports);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sportRepository = new SportRepository(requireContext());
        RecordViewModelFactory factory = new RecordViewModelFactory(sportRepository);
        recordViewModel = new ViewModelProvider(this, factory).get(RecordViewModel.class);

        recordViewModel.getSports().observe(getViewLifecycleOwner(), new Observer<List<Sport>>() {
            @Override
            public void onChanged(List<Sport> sports) {
                SportAdapter adapter = new SportAdapter(requireContext(), sports);
                spinner.setAdapter(adapter);
            }
        });

        buttonBack.setOnClickListener(v -> {
            getActivity().onBackPressed();
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        });

        buttonRecordAction.setOnClickListener(v -> {
            if (!isRecording) {
                buttonRecordAction.setImageResource(R.drawable.record_pause);
                buttonRecordEnd.setVisibility(View.GONE);
                isRecording = true;
            } else {
                buttonRecordAction.setImageResource(R.drawable.record_continue);
                buttonRecordEnd.setVisibility(View.VISIBLE);
                isRecording = false;
            }
        });

        buttonRecordedData.setOnClickListener(v -> {
            bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        buttonReporterTools.setOnClickListener(v -> {
            bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }
}