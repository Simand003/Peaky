package com.example.peaky.source.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.peaky.model.user.GenderUser;
import com.example.peaky.model.Result;
import com.example.peaky.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserFirestoreFirebaseDataSource {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public UserFirestoreFirebaseDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> saveUserData(String name, String surname, String birthDate, String gender) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = firestore.collection("users").document(userId);

            // Creiamo una mappa con i dati
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("name", name);
            userUpdates.put("surname", surname);
            userUpdates.put("birth_date", birthDate);
            userUpdates.put("gender", gender);

            // Aggiorniamo i dati nel database
            userRef.set(userUpdates, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                    .addOnFailureListener(e -> resultLiveData.setValue(false));
        } else {
            resultLiveData.setValue(false);
        }

        return resultLiveData;
    }

    public LiveData<Result<User>> getUserData(String userId) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();
        DocumentReference userRef = firestore.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                DocumentSnapshot dataSnapshot = task.getResult();

                // Estrai i dati dell'utente
                String name = dataSnapshot.getString("name");
                String surname = dataSnapshot.getString("surname");
                String genderString = dataSnapshot.getString("gender");
                String birthDateString = dataSnapshot.getString("birth_date");

                // Converti la data di nascita se presente
                Date birthDate = null;
                if (birthDateString != null) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        birthDate = dateFormat.parse(birthDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                GenderUser gender = GenderUser.fromString(genderString);

                // Crea l'oggetto User
                User user = new User(userId, null, name, surname, birthDate, gender);

                // Imposta il risultato
                resultLiveData.setValue(new Result.Success<>(user));
            } else {
                resultLiveData.setValue(new Result.Error<>(new Exception("User not found")));
            }
        }).addOnFailureListener(e -> resultLiveData.setValue(new Result.Error<>(e)));

        return resultLiveData;
    }

    public LiveData<Result<Boolean>> isUserRegistrationComplete(String userId) {
        MutableLiveData<Result<Boolean>> resultLiveData = new MutableLiveData<>();
        DocumentReference userRef = firestore.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                DocumentSnapshot dataSnapshot = task.getResult();

                String name = dataSnapshot.getString("name");
                String surname = dataSnapshot.getString("surname");
                String genderString = dataSnapshot.getString("gender");

                GenderUser gender = GenderUser.fromString(genderString);

                // Controlla se tutti i campi sono completi
                boolean isComplete = name != null && surname != null && gender != null;
                resultLiveData.setValue(new Result.Success<>(isComplete));
            } else {
                resultLiveData.setValue(new Result.Error<>(new Exception("User not found")));
            }
        }).addOnFailureListener(e -> resultLiveData.setValue(new Result.Error<>(e)));

        return resultLiveData;
    }
}
