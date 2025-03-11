package com.example.peaky.ui.login;

import static com.example.peaky.util.Constants.BIRTH_DATE_INFO_MESSAGE;
import static com.example.peaky.util.Constants.FIREBASE_DATABASE;
import static com.example.peaky.util.Constants.GENDER_INFO_MESSAGE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.peaky.R;
import com.example.peaky.model.GenderUser;
import com.example.peaky.ui.home.HomeActivity;
import com.example.peaky.ui.login.viewmodel.UserViewModel;
import com.example.peaky.ui.login.viewmodel.UserViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserInfoFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextInputEditText nameEditText, surnameEditText, dateEditText, genderEditText;
    private TextInputLayout containerDate, containerGender;
    private Button confirmButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserViewModelFactory factory = new UserViewModelFactory();
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameEditText = view.findViewById(R.id.textField_name);
        surnameEditText = view.findViewById(R.id.textField_surname);
        dateEditText = view.findViewById(R.id.textField_date);
        genderEditText = view.findViewById(R.id.textField_gender);

        containerDate = view.findViewById(R.id.container_date);
        containerGender = view.findViewById(R.id.container_gender);

        userViewModel.getGoogleUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null && userData.length > 0) {
                nameEditText.setText(userData[0]);
                surnameEditText.setText(userData[1]);
            }
        });

        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // Formatta la data nel formato gg/mm/aaaa
                        String selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        dateEditText.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Gestisci il clic sull'icona di "Date of Birth"
        containerDate.setEndIconOnClickListener(v -> {
            // Crea il contenuto del pop-up (AlertDialog)
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Date of Birth Information");
            builder.setMessage(BIRTH_DATE_INFO_MESSAGE);
            builder.setPositiveButton("OK", null);  // Bottone OK per chiudere il pop-up

            // Mostra il pop-up
            builder.show();
        });

        genderEditText.setOnClickListener(v -> {
            // Carica il layout del dialog
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View genderDialogView = inflater.inflate(R.layout.dialog_gender_selection, null);

            // Crea l'AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select Gender");
            builder.setView(genderDialogView);

            // Aggiungi i listener per i pulsanti di OK e Cancel
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Trova il RadioGroup dal layout
                RadioGroup radioGroup = genderDialogView.findViewById(R.id.radioGroup_gender);

                // Ottieni l'ID del RadioButton selezionato
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // Mappa l'ID selezionato al corrispondente valore dell'enum GenderUser
                GenderUser selectedGender = GenderUser.PREFER_NOT_TO_SAY; // Default

                if (selectedId == R.id.radioMale) {
                    selectedGender = GenderUser.MALE;
                } else if (selectedId == R.id.radioFemale) {
                    selectedGender = GenderUser.FEMALE;
                } else if (selectedId == R.id.radioNonBinary) {
                    selectedGender = GenderUser.NON_BINARY;
                } else if (selectedId == R.id.radioPreferNotToSay) {
                    selectedGender = GenderUser.PREFER_NOT_TO_SAY;
                }

                // Usa il metodo getGenderLabel() per ottenere il valore leggibile
                String formattedGender = selectedGender.getGenderLabel();

                // Una volta selezionato, aggiorna il campo di testo
                genderEditText.setText(formattedGender);
            });

            builder.setNegativeButton("Cancel", null); // Annulla l'operazione

            // Mostra il dialog
            builder.show();
        });

        // Gestisci il clic sull'icona di "Gender"
        containerGender.setEndIconOnClickListener(v -> {
            // Crea il contenuto del pop-up (AlertDialog)
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Gender Information");
            builder.setMessage(GENDER_INFO_MESSAGE);
            builder.setPositiveButton("OK", null);  // Bottone OK per chiudere il pop-up

            // Mostra il pop-up
            builder.show();
        });

        confirmButton = view.findViewById(R.id.button_confirm);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        confirmButton.setOnClickListener(v -> {
            if (currentUser != null) {

                String name = nameEditText.getText().toString().trim();
                String surname = surnameEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();
                String gender = genderEditText.getText().toString().trim();

                if (name.isEmpty() || surname.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                userViewModel.saveUserData(name, surname, date, gender)
                        .observe(getViewLifecycleOwner(), success -> {
                            if (success) {
                                Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Error saving data", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}