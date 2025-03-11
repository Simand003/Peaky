package com.example.peaky.source;

import static com.example.peaky.util.Constants.FIREBASE_DATABASE;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.peaky.model.GenderUser;
import com.example.peaky.model.Result;
import com.example.peaky.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserRealtimeDatabaseFirebaseDataSource {
    private final FirebaseAuth auth;
    private final DatabaseReference databaseReference;

    public UserRealtimeDatabaseFirebaseDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE).getReference("users");
    }

    public LiveData<Boolean> saveUserData(String name, String surname, String birthDate, String gender) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Creiamo una mappa con i dati
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("name", name);
            userUpdates.put("surname", surname);
            userUpdates.put("birth_date", birthDate);
            userUpdates.put("gender", gender);

            // Aggiorniamo i dati nel database
            databaseReference.child(userId).updateChildren(userUpdates)
                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                    .addOnFailureListener(e -> resultLiveData.setValue(false));
        } else {
            resultLiveData.setValue(false);
        }

        return resultLiveData;
    }

    public LiveData<Result<User>> getUserData(String userId) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Estrai i dati dell'utente
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);
                    String genderString = dataSnapshot.child("gender").getValue(String.class);
                    String dateOfBirthString = dataSnapshot.child("date_of_birth").getValue(String.class);

                    // Converti la data di nascita se è una stringa (es. "yyyy-MM-dd")
                    Date dateOfBirth = null;
                    if (dateOfBirthString != null) {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            dateOfBirth = dateFormat.parse(dateOfBirthString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    GenderUser gender = GenderUser.fromString(genderString);

                    // Crea l'oggetto User
                    User user = new User(userId, null, name, surname, dateOfBirth, gender);

                    // Imposta il risultato
                    resultLiveData.setValue(new Result.Success<>(user));
                } else {
                    resultLiveData.setValue(new Result.Error<>(new Exception("User not found")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resultLiveData.setValue(new Result.Error<>(databaseError.toException()));
            }
        });

        return resultLiveData;
    }

    public LiveData<Result<Boolean>> isUserRegistrationComplete(String userId) {
        MutableLiveData<Result<Boolean>> resultLiveData = new MutableLiveData<>();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);
                    String genderString = dataSnapshot.child("gender").getValue(String.class);

                    GenderUser gender = GenderUser.fromString(genderString);

                    // Crea l'oggetto User
                    boolean isComplete = name != null && surname != null && gender != null;
                    resultLiveData.setValue(new Result.Success<>(isComplete));
                } else {
                    resultLiveData.setValue(new Result.Error<>(new Exception("User not found")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resultLiveData.setValue(new Result.Error<>(databaseError.toException()));
            }
        });

        return resultLiveData;
    }


}
