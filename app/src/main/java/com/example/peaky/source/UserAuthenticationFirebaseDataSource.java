package com.example.peaky.source;

import static com.example.peaky.util.Constants.EXISTING_ACCOUNT_ERROR_MESSAGE;
import static com.example.peaky.util.Constants.FIREBASE_DATABASE;
import static com.example.peaky.util.Constants.INVALID_EMAIL_OR_PASSWORD_ERROR_MESSAGE;
import static com.example.peaky.util.Constants.LOGIN_SUCCESSFUL_MESSAGE;
import static com.example.peaky.util.Constants.NO_EXISTING_ACCOUNT_ERROR_MESSAGE;
import static com.example.peaky.util.Constants.REGISTER_SUCCESSFUL_MESSAGE;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.peaky.model.Result;
import com.example.peaky.model.User;
import com.example.peaky.repository.UserResponseCallback;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAuthenticationFirebaseDataSource {

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationFirebaseDataSource() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Result<String>> registerUser(String email, String password) {
        MutableLiveData<Result<String>> resultLiveData = new MutableLiveData<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), null, null, null, null);

                            // Salva l'utente nel Realtime Database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE).getReference("users");

                            databaseReference.child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            resultLiveData.setValue(new Result.Success<>(REGISTER_SUCCESSFUL_MESSAGE));
                                        } else {
                                            resultLiveData.setValue(new Result.Error<>(dbTask.getException()));
                                        }
                                    });
                        }
                    } else {
                        // Gestisci errore quando l'email è già registrata
                        if (task.getException() != null && task.getException().getMessage().contains("email address is already in use")) {
                            resultLiveData.setValue(new Result.Error<>(new Exception(EXISTING_ACCOUNT_ERROR_MESSAGE)));
                        } else {
                            resultLiveData.setValue(new Result.Error<>(task.getException()));
                        }
                    }
                });

        return resultLiveData;
    }

    public LiveData<Result<String>> loginUser(String email, String password) {
        MutableLiveData<Result<String>> resultLiveData = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resultLiveData.setValue(new Result.Success<>(LOGIN_SUCCESSFUL_MESSAGE));
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            resultLiveData.setValue(new Result.Error<>(new Exception(NO_EXISTING_ACCOUNT_ERROR_MESSAGE)));
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            resultLiveData.setValue(new Result.Error<>(new Exception(INVALID_EMAIL_OR_PASSWORD_ERROR_MESSAGE)));
                        } else {
                            resultLiveData.setValue(new Result.Error<>(task.getException()));
                        }
                    }
                });

        return resultLiveData;
    }

    public void authenticateWithGoogle(String idToken, UserResponseCallback<Result<User>> callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), null, null, null, null);

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE).getReference("users");

                            // Controlla se l'utente esiste già nel database
                            databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // Se l'utente non esiste, lo salviamo nel database
                                        databaseReference.child(firebaseUser.getUid()).setValue(user);
                                    }
                                    callback.onComplete(new Result.Success<>(user));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    callback.onComplete(new Result.Error<>(error.toException()));
                                }
                            });

                        } else {
                            callback.onComplete(new Result.Error<>(new Exception("FirebaseUser is null")));
                        }
                    } else {
                        callback.onComplete(new Result.Error<>(task.getException()));
                    }
                });
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public LiveData<String[]> getGoogleUserData() {
        MutableLiveData<String[]> userDataLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null && currentUser.getDisplayName() != null) {
            String displayName = currentUser.getDisplayName();
            Log.d("Firebase", "Google Display Name: " + displayName);

            String[] nameParts = displayName.split(" ", 2); // Divide in nome e cognome
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            userDataLiveData.setValue(new String[]{firstName, lastName});
        } else {
            userDataLiveData.setValue(new String[]{"", ""});
        }
        return userDataLiveData;
    }

    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }
}
