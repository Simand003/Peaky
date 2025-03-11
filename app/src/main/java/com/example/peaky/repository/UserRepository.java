package com.example.peaky.repository;

import androidx.lifecycle.LiveData;

import com.example.peaky.model.Result;
import com.example.peaky.model.User;
import com.example.peaky.source.UserAuthenticationFirebaseDataSource;
import com.example.peaky.source.UserFirestoreFirebaseDataSource;

public class UserRepository {

    private final UserAuthenticationFirebaseDataSource userAuthFirebaseDataSource;
    private final UserFirestoreFirebaseDataSource userRealTimeDatabaseDataSource;

    public UserRepository(UserAuthenticationFirebaseDataSource userAuthenticationFirebaseDataSource,
                          UserFirestoreFirebaseDataSource userFirestoreFirebaseDataSource) {
        this.userAuthFirebaseDataSource = userAuthenticationFirebaseDataSource;
        this.userRealTimeDatabaseDataSource = userFirestoreFirebaseDataSource;
    }

    public LiveData<Result<String>> register(String email, String password) {
        return userAuthFirebaseDataSource.registerUser(email, password);
    }

    public LiveData<Result<String>> login(String email, String password) {
        return userAuthFirebaseDataSource.loginUser(email, password);
    }

    public void authenticateWithGoogle(String idToken, UserResponseCallback<Result<User>> callback) {
        userAuthFirebaseDataSource.authenticateWithGoogle(idToken, callback);
    }

    public void signOut() {
        userAuthFirebaseDataSource.signOut();
    }

    public LiveData<String[]> getGoogleUserData() {
        return userAuthFirebaseDataSource.getGoogleUserData();
    }

    public LiveData<Boolean> saveUserData(String name, String surname, String birthDate, String gender) {
        return userRealTimeDatabaseDataSource.saveUserData(name, surname, birthDate, gender);
    }

    public LiveData<Result<User>> getUserData(String userId) {
        return userRealTimeDatabaseDataSource.getUserData(userId);
    }

    public LiveData<Result<Boolean>> isUserRegistrationComplete(String userId) {
        return userRealTimeDatabaseDataSource.isUserRegistrationComplete(userId);
    }

    public boolean isUserLoggedIn() {
        return userAuthFirebaseDataSource.isUserLoggedIn();
    }

    public String getCurrentUserId() {
        return userAuthFirebaseDataSource.getCurrentUserId();
    }
}
