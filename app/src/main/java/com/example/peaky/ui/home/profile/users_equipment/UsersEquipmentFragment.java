package com.example.peaky.ui.home.profile.users_equipment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.peaky.R;
import com.example.peaky.adapter.EquipmentRecyclerAdapter;
import com.example.peaky.model.equipment.Equipment;
import com.example.peaky.repository.equipment.EquipmentRepository;
import com.example.peaky.repository.equipment.EquipmentRepositoryFactory;
import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.repository.sport.SportRepositoryFactory;
import com.example.peaky.source.SportDataSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UsersEquipmentFragment extends Fragment {

    private UsersEquipmentViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private Button buttonBack;
    ImageButton buttonAdd;

    private RecyclerView recyclerViewClothing, recyclerViewFootwear, recyclerViewTools, recyclerViewWearables, recyclerViewBikes;
    private LinearLayout containerClothing, containerFootwear, containerTools, containerWearables, containerBikes;
    private TextView statsClothing, statsFootwear, statsTools, statsWearables, statsBikes;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EquipmentRepository equipmentRepository = EquipmentRepositoryFactory.getEquipmentRepository(requireContext());
        SportRepository sportRepository = SportRepositoryFactory.getSportRepository(requireContext());
        viewModel = new UsersEquipmentViewModel(equipmentRepository, sportRepository);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_equipment, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        //Initializing the buttons
        buttonBack = view.findViewById(R.id.button_back);
        buttonAdd = view.findViewById(R.id.button_add);

        // Containers
        containerClothing = view.findViewById(R.id.container_clothing);
        containerFootwear = view.findViewById(R.id.container_footwear);
        containerTools = view.findViewById(R.id.container_tools);
        containerWearables = view.findViewById(R.id.container_wearables);
        containerBikes = view.findViewById(R.id.container_bikes);

        // RecyclerViews
        recyclerViewClothing = view.findViewById(R.id.recyclerView_clothing);
        recyclerViewFootwear = view.findViewById(R.id.recyclerView_footwear);
        recyclerViewTools = view.findViewById(R.id.recyclerView_tools);
        recyclerViewWearables = view.findViewById(R.id.recyclerView_wearables);
        recyclerViewBikes = view.findViewById(R.id.recyclerView_bikes);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.loadUserEquipment(userId);

        // Observe equipment data
        viewModel.getEquipmentByType().observe(getViewLifecycleOwner(), map -> {
            setupRecycler(map.get("Technical Clothing"), recyclerViewClothing, containerClothing);
            setupRecycler(map.get("Footwear"), recyclerViewFootwear, containerFootwear);
            setupRecycler(map.get("Equipment"), recyclerViewTools, containerTools);
            setupRecycler(map.get("Wearables"), recyclerViewWearables, containerWearables);
            setupRecycler(map.get("Bikes"), recyclerViewBikes, containerBikes);
        });

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

    private void setupRecycler(List<Equipment> list, RecyclerView recyclerView, View container) {
        if (list == null || list.isEmpty()) {
            container.setVisibility(View.GONE);
        } else {
            container.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(new EquipmentRecyclerAdapter(list, equipment -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                // Carica gli sport associati *prima* di navigare
                viewModel.getSportsWhereEquipmentIsDefault(userId, equipment.getId(), new SportDataSource.OnSportsResultListener() {
                    @Override
                    public void onResult(List<String> sports) {
                        // Una volta caricati, passa tutto nel bundle
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("equipment", equipment);
                        bundle.putStringArrayList("associatedSports", new ArrayList<>(sports)); // lista serializzabile

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_equipmentFragment_to_selectedEquipmentFragment, bundle);
                    }

                    @Override
                    public void onError(Exception e) {
                        // Anche in caso di errore, fai la navigazione senza sport associati
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("equipment", equipment);
                        bundle.putStringArrayList("associatedSports", new ArrayList<>());

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_equipmentFragment_to_selectedEquipmentFragment, bundle);
                    }
                });
            }));
        }
    }

}