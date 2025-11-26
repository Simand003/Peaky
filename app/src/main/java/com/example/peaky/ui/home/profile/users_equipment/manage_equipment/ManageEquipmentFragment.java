/*

package com.example.peaky.ui.home.profile.users_equipment.manage_equipment;

import static com.example.peaky.util.Constants.EQUIPMENT_SAVE;
import static com.example.peaky.util.Constants.FILL_ALL_REQUIRED_FIELDS;
import static com.example.peaky.util.Constants.SELECT_AN_EQUIPMENT;
import static com.example.peaky.util.Constants.SELECT_A_CATEGORY;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peaky.R;
import com.example.peaky.adapter.SportRecyclerAdapter;
import com.example.peaky.model.Sport;
import com.example.peaky.model.equipment.EquipmentType;
import com.example.peaky.repository.equipment.EquipmentRepository;
import com.example.peaky.repository.equipment.EquipmentRepositoryFactory;
import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.repository.sport.SportRepositoryFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.peaky.model.equipment.Equipment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManageEquipmentFragment extends Fragment {

    private ManageEquipmentViewModel viewModel;

    private Button buttonBack, buttonSave;
    private Spinner spinnerEquipmentCategory, spinnerEquipment;
    private TextInputEditText textFieldBrand, textFieldModel, textFieldDate, textFieldPrice, textFieldNotes;
    private TextView equipmentTextView, defaultSportsTextView;
    private RecyclerView recyclerViewSport;
    private BottomSheetBehavior<View> bottomSheetBehaviorDefaultSports;
    private SportRecyclerAdapter sportAdapter;

    private Equipment equipmentToEdit = null;
    private ArrayList<String> associatedSports = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EquipmentRepository equipmentRepository = EquipmentRepositoryFactory.getEquipmentRepository(requireContext());
        SportRepository sportRepository = SportRepositoryFactory.getSportRepository(requireContext());
        ManageEquipmentViewModelFactory factory = new ManageEquipmentViewModelFactory(equipmentRepository, sportRepository);
        viewModel = new ViewModelProvider(this, factory).get(ManageEquipmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_equipment, container, false);

        // Inizializzazione UI
        buttonBack = view.findViewById(R.id.button_back);
        buttonSave = view.findViewById(R.id.button_save);
        spinnerEquipmentCategory = view.findViewById(R.id.spinner_equipment_category);
        spinnerEquipment = view.findViewById(R.id.spinner_equipment);
        textFieldBrand = view.findViewById(R.id.textField_brand);
        textFieldModel = view.findViewById(R.id.textField_model);
        textFieldDate = view.findViewById(R.id.select_data);
        textFieldPrice = view.findViewById(R.id.textField_price);
        textFieldNotes = view.findViewById(R.id.textField_notes);
        equipmentTextView = view.findViewById(R.id.textView_equipment);
        defaultSportsTextView = view.findViewById(R.id.button_default_sport);
        recyclerViewSport = view.findViewById(R.id.recycler_view_sports);
        bottomSheetBehaviorDefaultSports = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheet_default_sports));
        bottomSheetBehaviorDefaultSports.setState(BottomSheetBehavior.STATE_HIDDEN);

        ArrayList<String> initialTypes = new ArrayList<>();
        initialTypes.add(SELECT_AN_EQUIPMENT);
        ArrayAdapter<String> initialAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, initialTypes);
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipment.setAdapter(initialAdapter);

        if (getArguments() != null) {
            associatedSports = getArguments().getStringArrayList("associatedSports");
            if (getArguments().containsKey("equipment")) {
                equipmentToEdit = (Equipment) getArguments().getSerializable("equipment");
                viewModel.setEquipmentToEdit(equipmentToEdit);
            }
        }
/*
        // Sport observer
        viewModel.getSports().observe(getViewLifecycleOwner(), sports -> {
            if (sports != null && !sports.isEmpty()) {
                sportAdapter = new SportRecyclerAdapter(requireContext(), sports);
                recyclerViewSport.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerViewSport.setAdapter(sportAdapter);

                if (equipmentToEdit != null && associatedSports != null) {
                    sportAdapter.setSelectedSportsByName(associatedSports);
                    updateDefaultSportsTextView();
                }
            }
        });

        viewModel.getCategories().observe(requireActivity(), this::populateEquipmentCategorySpinner);
        viewModel.getTypes().observe(requireActivity(), this::populateEquipmentTypeSpinner);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (equipmentToEdit != null) {
            loadEquipmentData(equipmentToEdit);
        }

        buttonBack.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack());
        buttonSave.setOnClickListener(v -> saveEquipment());

        textFieldDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                textFieldDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        defaultSportsTextView.setOnClickListener(v -> {
            if (bottomSheetBehaviorDefaultSports.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorDefaultSports.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetBehaviorDefaultSports.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.resetButtonPosition();
                    updateDefaultSportsTextView();
                } else {
                    viewModel.adjustButtonPosition(bottomSheet);
                }
            }

            @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                viewModel.adjustButtonPosition(bottomSheet);
            }
        });
    }

    private void updateDefaultSportsTextView() {
        if (sportAdapter != null) {
            List<Sport> selected = sportAdapter.getSelectedSports();
            if (!selected.isEmpty()) {
                List<String> names = new ArrayList<>();
                for (Sport sport : selected) names.add(sport.getName());
                defaultSportsTextView.setText(TextUtils.join(", ", names));
            } else {
                defaultSportsTextView.setText(R.string.no_sport_selected);
            }
        }
    }

    private void populateEquipmentCategorySpinner(ArrayList<String> categories) {
        categories.add(0, SELECT_A_CATEGORY);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipmentCategory.setAdapter(adapter);

        spinnerEquipmentCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    viewModel.onCategorySelected(categories.get(position));
                    spinnerEquipment.setAlpha(1.0f);
                    spinnerEquipment.setEnabled(true);
                    equipmentTextView.setAlpha(1.0f);
                } else {
                    spinnerEquipment.setSelection(0);
                    spinnerEquipment.setAlpha(0.5f);
                    spinnerEquipment.setEnabled(false);
                    equipmentTextView.setAlpha(0.5f);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



    private void populateEquipmentTypeSpinner(ArrayList<String> types) {
        types.add(0, SELECT_AN_EQUIPMENT);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipment.setAdapter(adapter);

        if (equipmentToEdit != null) {
            int pos = types.indexOf(equipmentToEdit.getType().getName());
            if (pos >= 0) spinnerEquipment.setSelection(pos + 1);
        }
    }

    private void saveEquipment() {
        String equipmentName = spinnerEquipment.getSelectedItem().toString();
        String brand = textFieldBrand.getText().toString().trim();
        String model = textFieldModel.getText().toString().trim();
        String purchaseDateString = textFieldDate.getText().toString();
        String priceText = textFieldPrice.getText().toString().trim();
        double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);
        String notes = textFieldNotes.getText().toString().trim();

        if (spinnerEquipment.getSelectedItemPosition() == 0 || brand.isEmpty() || model.isEmpty()) {
            Toast.makeText(requireContext(), FILL_ALL_REQUIRED_FIELDS, Toast.LENGTH_SHORT).show();
            return;
        }

        List<Sport> selectedSports = sportAdapter.getSelectedSports();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        Date purchaseDate = null;
        if (!purchaseDateString.isEmpty()) {
            try {
                purchaseDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(purchaseDateString);
            } catch (ParseException e) {
                Toast.makeText(requireContext(), "Formato data non valido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        EquipmentType equipmentType = new EquipmentType(equipmentName);
        Equipment equipment;

        if (viewModel.getEquipmentToEdit() != null) {
            // Modifica
            equipment = viewModel.getEquipmentToEdit();
            equipment.setType(equipmentType);
            equipment.setBrand(brand);
            equipment.setModel(model);
            equipment.setPurchase_date(purchaseDate);
            equipment.setPrice(price);
            equipment.setNotes(notes);

            viewModel.updateEquipment(userId, equipment);
        } else {
            // Creazione
            equipment = new Equipment(
                    null, userId, equipmentType, brand, model, purchaseDate, price,
                    notes, 0.0, 0.0, 0, 0
            );
            viewModel.addEquipment(userId, equipment);
        }

        for (Sport sport : selectedSports) {
            viewModel.setDefaultEquipmentForSport(sport.getName(), equipment.getType().getName(), equipment);
        }

        getParentFragmentManager().popBackStack();
    }

    private void loadEquipmentData(Equipment equipment) {
        textFieldBrand.setText(equipment.getBrand());
        textFieldModel.setText(equipment.getModel());
        textFieldDate.setText(equipment.getFormattedPurchaseDate());
        textFieldPrice.setText(String.valueOf(equipment.getPrice()));
        textFieldNotes.setText(equipment.getNotes());
    }
}


 */
