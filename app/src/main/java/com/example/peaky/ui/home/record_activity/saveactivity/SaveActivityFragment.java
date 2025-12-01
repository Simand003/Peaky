package com.example.peaky.ui.home.record_activity.saveactivity;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.peaky.R;
import com.example.peaky.adapter.SportRecyclerAdapter;
import com.example.peaky.model.Activity;
import com.example.peaky.repository.ActivityRepository;
import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.source.SportDataSource;
import com.example.peaky.source.activity.ActivityFirestoreDataSource;
import com.example.peaky.source.activity.ActivityLocalDataSource;
import com.example.peaky.ui.home.record_activity.ActivityDataRecordedViewModel;
import com.example.peaky.ui.home.record_activity.ActivityDataRecordedViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SaveActivityFragment extends Fragment {

    private Button buttonBack, buttonSave, buttonAddPeak;
    private BottomNavigationView bottomNavigationView;

    private TextInputEditText textPeaks, textSport;

    private TextInputEditText textName, textDescription;
    private View bottomSheetPeaks, bottomSheetSports;
    private BottomSheetBehavior<View> bottomSheetPeaksBehavior, bottomSheetSportsBehavior;

    private RecyclerView recyclerViewSport, recyclerViewPeaks;

    private ActivityDataRecordedViewModel activityDataRecordedViewModel;
    private SportRepository sportRepository;
    private ActivityRepository activityRepository;

    private String selectedSportName = null;

    FirebaseUser currentUser;
    String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SportDataSource sportDataSource = new SportDataSource(FirebaseFirestore.getInstance());
        sportRepository = new SportRepository(requireContext(), sportDataSource);
        ActivityLocalDataSource activityLocalDataSource = new ActivityLocalDataSource();
        ActivityFirestoreDataSource activityFirestoreDataSource = new ActivityFirestoreDataSource(FirebaseFirestore.getInstance());
        activityRepository = new ActivityRepository(requireContext(), activityFirestoreDataSource, activityLocalDataSource);
        activityDataRecordedViewModel = new ViewModelProvider(requireActivity()/*, factory*/).get(ActivityDataRecordedViewModel.class);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
    }

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

        textName = view.findViewById(R.id.textField_activity_name);
        textDescription = view.findViewById(R.id.textField_description);
        textPeaks = view.findViewById(R.id.textField_peaks);
        textSport = view.findViewById(R.id.textField_sport);

        bottomSheetPeaks = view.findViewById(R.id.bottomSheet_peaks);
        bottomSheetPeaksBehavior = BottomSheetBehavior.from(bottomSheetPeaks);
        bottomSheetPeaksBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetSports = view.findViewById(R.id.bottomSheet_sport);
        bottomSheetSportsBehavior = BottomSheetBehavior.from(bottomSheetSports);
        bottomSheetSportsBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        recyclerViewSport = view.findViewById(R.id.recycler_view_sports);
        recyclerViewSport.setLayoutManager(new LinearLayoutManager(requireContext()));
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
            Long elapsed = activityDataRecordedViewModel.getElapsedTime().getValue();
            long duration = elapsed != null ? elapsed : 0L;
            Log.d("SaveActivityFragment", "Elapsed time in ms: " + duration);

            if (duration < 60_000) { // meno di un minuto
                showDurationTooShortDialog();
                return;
            }
            saveActivity();

            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build();

            NavHostFragment.findNavController(this)
                    .navigate(R.id.homeFragment, null, navOptions);

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
            SportRecyclerAdapter adapter = (SportRecyclerAdapter) recyclerViewSport.getAdapter();
            if (adapter != null) {
                adapter.setSelectedSportByName(selectedSportName);
            }
        });

        activityDataRecordedViewModel.getSports().observe(getViewLifecycleOwner(), sports -> {
            if (sports != null && !sports.isEmpty()) {
                SportRecyclerAdapter adapter = new SportRecyclerAdapter(requireContext(), sports, sportName -> {
                    selectedSportName = sportName; // aggiorna la variabile nel fragment
                    textSport.setText(sportName != null ? sportName : ""); // aggiorna il TextInputEditText
                    bottomSheetSportsBehavior.setState(BottomSheetBehavior.STATE_HIDDEN); // chiudi il bottom sheet
                });
                recyclerViewSport.setAdapter(adapter);
            }
        });
    }

    private void showDurationTooShortDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Activity too short")
                .setMessage("The activity must be at least 1 minute long to be saved.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void saveActivity() {
        activityDataRecordedViewModel.saveActivity(
                userId,
                textName.getText().toString(),
                textSport.getText().toString(),
                textDescription.getText().toString()
        );
    }
}
