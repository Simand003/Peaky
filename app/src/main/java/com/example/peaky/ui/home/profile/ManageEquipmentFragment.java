package com.example.peaky.ui.home.profile;

import static com.example.peaky.util.Constants.EQUIPMENT_SAVE;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageEquipmentFragment extends Fragment {

    private ManageEquipmentViewModel viewModel;
    private Button buttonBack, buttonSave;
    private Spinner spinnerEquipmentCategory, spinnerEquipment;
    private TextInputEditText textFieldBrand, textFieldModel, textFieldDate, textFieldPrice,textFieldNotes;
    private TextView equipmentTextView, defaultSportsTextView;
    private RecyclerView recyclerViewSport;
    private BottomSheetBehavior<View> bottomSheetBehaviorDefaultSports;
    private SportRecyclerAdapter sportAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EquipmentRepository equipmentRepository = EquipmentRepositoryFactory.getEquipmentRepository(requireContext());
        SportRepository sportRepository = SportRepositoryFactory.getSportRepository(requireContext());
        ManageEquipmentViewModelFactory factory = new ManageEquipmentViewModelFactory(equipmentRepository, sportRepository);
        viewModel = new ViewModelProvider(this, factory).get(ManageEquipmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_equipment, container, false);

        // Inizializzazione dei bottoni
        buttonBack = view.findViewById(R.id.button_back);
        buttonSave = view.findViewById(R.id.button_save);

        // Inizializzazione degli spinner
        spinnerEquipmentCategory = view.findViewById(R.id.spinner_equipment_category);
        spinnerEquipment = view.findViewById(R.id.spinner_equipment);

        // Imposta inizialmente un solo item fittizio nello spinnerEquipment
        ArrayList<String> initialTypes = new ArrayList<>();
        initialTypes.add(SELECT_AN_EQUIPMENT);
        ArrayAdapter<String> initialAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, initialTypes);
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipment.setAdapter(initialAdapter);

        // Initiaziling the edit text
        textFieldBrand = view.findViewById(R.id.textField_brand);
        textFieldModel = view.findViewById(R.id.textField_model);
        textFieldDate = view.findViewById(R.id.select_data);
        textFieldPrice = view.findViewById(R.id.textField_price);
        textFieldNotes = view.findViewById(R.id.textField_notes);

        equipmentTextView = view.findViewById(R.id.textView_equipment);
        defaultSportsTextView = view.findViewById(R.id.button_default_sport);

        //Initializing Recycler View
        recyclerViewSport = view.findViewById(R.id.recycler_view_sports);
        viewModel.getSports().observe(getViewLifecycleOwner(), sports -> {
            if (sports != null && !sports.isEmpty()) {
                sportAdapter = new SportRecyclerAdapter(requireContext(), sports);
                recyclerViewSport.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerViewSport.setAdapter(sportAdapter);
            }
        });

        //Initializing bottom sheet
        bottomSheetBehaviorDefaultSports = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheet_default_sports));
        bottomSheetBehaviorDefaultSports.setState(BottomSheetBehavior.STATE_HIDDEN);

        viewModel.getCategories().observe(requireActivity(), this::populateEquipmentCategorySpinner);
        viewModel.getTypes().observe(requireActivity(), this::populateEquipmentTypeSpinner);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Listener per il bottone indietro
        buttonBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.popBackStack();
        });

        // Listener per il bottone salva
        buttonSave.setOnClickListener(v -> {
            saveEquipment();
            getParentFragmentManager().popBackStack();
        });

        textFieldDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // Formatta la data nel formato gg/mm/aaaa
                        String selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        textFieldDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        defaultSportsTextView.setOnClickListener(v -> {
            if (bottomSheetBehaviorDefaultSports.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorDefaultSports.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetBehaviorDefaultSports.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.resetButtonPosition();

                    if (sportAdapter != null) {
                        List<Sport> selectedSports = sportAdapter.getSelectedSports();

                        if (!selectedSports.isEmpty()) {
                            String joinedNames = "";
                            if (!selectedSports.isEmpty()) {
                                List<String> names = new ArrayList<>();
                                for (Sport sport : selectedSports) {
                                    names.add(sport.getName());
                                }
                                joinedNames = TextUtils.join(", ", names);
                                defaultSportsTextView.setText(joinedNames);
                            } else {
                                defaultSportsTextView.setText("Nessuno sport selezionato");
                            }
                        } else {
                            defaultSportsTextView.setText(R.string.no_sport_selected);
                        }
                    }
                } else {
                    viewModel.adjustButtonPosition(bottomSheet);
                }
            }


            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                viewModel.adjustButtonPosition(bottomSheet);
            }
        });

    }

    private void populateEquipmentCategorySpinner(ArrayList<String> categories) {
        categories.add(0, SELECT_A_CATEGORY);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipmentCategory.setAdapter(adapter);

        spinnerEquipmentCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Se è stata selezionata una categoria valida
                    String selectedCategory = categories.get(position);
                    viewModel.onCategorySelected(selectedCategory);

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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void populateEquipmentTypeSpinner(ArrayList<String> types) {
        types.add(0, SELECT_AN_EQUIPMENT);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipment.setAdapter(adapter);

        spinnerEquipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedType = types.get(position);
                    // Azione da eseguire quando viene selezionato un tipo valido (opzionale)
                } else {
                    // L'utente ha selezionato "Seleziona un tipo...", nessuna azione (opzionale)
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveEquipment() {
        // Raccogli i dati dai campi di input
        String equipmentName = spinnerEquipment.getSelectedItem().toString();  // Nome del tipo di equipaggiamento
        String brand = textFieldBrand.getText().toString().trim();  // Marca
        String model = textFieldModel.getText().toString().trim();  // Modello
        String purchaseDateString = textFieldDate.getText().toString();  // Data di acquisto (stringa)
        String priceText = textFieldPrice.getText().toString().trim();  // Prezzo
        double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);  // Prezzo (se non vuoto)
        String notes = textFieldNotes.getText().toString().trim();  // Note

        // Ottieni la lista degli sport selezionati dal RecyclerView nel BottomSheet
        List<Sport> selectedDefaultSports = sportAdapter.getSelectedSports();  // Ottieni gli sport selezionati

        // Ottieni l'ID dell'utente corrente da Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        // Gestione della data di acquisto
        Date purchaseDate = null;
        if (!purchaseDateString.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                purchaseDate = sdf.parse(purchaseDateString);  // Converte la stringa della data in un oggetto Date
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Formato data non valido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Crea l'oggetto EquipmentType (il tipo di equipaggiamento selezionato)
        EquipmentType equipmentType = new EquipmentType(equipmentName);

        // Creazione dell'oggetto Equipment con i dati raccolti
        Equipment equipment = new Equipment(
                null,  // L'ID sarà generato automaticamente dal database (Firestore)
                userId,  // ID dell'utente corrente
                equipmentType,  // Usa il nome come String, non l'oggetto EquipmentType
                brand,  // Marca
                model,  // Modello
                purchaseDate,  // Data di acquisto
                price,  // Prezzo
                notes,  // Note
                0.0,  // Distanza (inizialmente 0.0)
                0.0,  // Elevazione (inizialmente 0.0)
                0,  // Usi (inizialmente 0)
                0   // Picchi raggiunti (inizialmente 0)
        );

        // Salva l'equipaggiamento tramite il ViewModel
        viewModel.addEquipment(userId, equipment);

        // Se l'utente ha selezionato degli sport da associare all'equipaggiamento, aggiorna gli sport
        if (!selectedDefaultSports.isEmpty()) {
            for (Sport sport : selectedDefaultSports) {
                // Usa getName() per ottenere il nome del tipo come stringa
                viewModel.setDefaultEquipmentForSport(sport.getName(), equipment.getType().getName(), equipment);
            }
        }

        // Mostra un messaggio di conferma e torna indietro
        Toast.makeText(requireContext(), EQUIPMENT_SAVE, Toast.LENGTH_SHORT).show();
    }
}