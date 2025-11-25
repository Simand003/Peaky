package com.example.peaky.ui.home.record.saveactivity;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.peaky.R;
import com.example.peaky.ui.home.HomeFragment;
import com.example.peaky.ui.home.record.RecordFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

public class SaveActivityFragment extends Fragment {

    private Button buttonBack, buttonSave, buttonAddPeak;
    private BottomNavigationView bottomNavigationView;

    private TextInputEditText textPeaks, textSport;

    private View bottomSheetPeaks, bottomSheetSports;
    private BottomSheetBehavior<View> bottomSheetPeaksBehavior, bottomSheetSportsBehavior;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_activity, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        buttonBack = view.findViewById(R.id.button_back);
        buttonSave = view.findViewById(R.id.button_save);
        buttonAddPeak = view.findViewById(R.id.button_add_peak);

        textPeaks = view.findViewById(R.id.textField_peaks);
        textSport = view.findViewById(R.id.textField_sport);

        bottomSheetPeaks = view.findViewById(R.id.bottomSheet_peaks);
        bottomSheetPeaksBehavior = BottomSheetBehavior.from(bottomSheetPeaks);
        bottomSheetPeaksBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetSports = view.findViewById(R.id.bottomSheet_sport);
        bottomSheetSportsBehavior = BottomSheetBehavior.from(bottomSheetSports);
        bottomSheetSportsBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        buttonSave.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
            getParentFragmentManager().popBackStack();

            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, homeFragment);
            transaction.commit();

            bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavController navController = Navigation.findNavController(view);
                        navController.popBackStack();
                    }
                });



        textPeaks.setOnClickListener(v -> {
            if (bottomSheetSportsBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetSportsBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            bottomSheetPeaksBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        textSport.setOnClickListener(v -> {
            if (bottomSheetPeaksBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetPeaksBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            bottomSheetSportsBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }
}
