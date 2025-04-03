package com.example.peaky.ui.home.profile;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.peaky.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EquipmentFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Button buttonBack;
    ImageButton buttonAdd;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipment, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        //Initializing the buttons
        buttonBack = view.findViewById(R.id.button_back);
        buttonAdd = view.findViewById(R.id.button_add);

        //Initializing the recycler view
        recyclerView = view.findViewById(R.id.recyclerView_clothing);
        recyclerView = view.findViewById(R.id.recyclerView_footwear);
        recyclerView = view.findViewById(R.id.recyclerView_tools);
        recyclerView = view.findViewById(R.id.recyclerView_wearables);
        recyclerView = view.findViewById(R.id.recyclerView_bikes);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.popBackStack();
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        buttonAdd.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_equipmentFragment_to_manageEquipmentFragment);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavController navController = Navigation.findNavController(view);
                        navController.popBackStack();
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}