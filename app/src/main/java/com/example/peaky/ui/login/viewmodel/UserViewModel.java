package com.example.peaky.ui.login.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import com.example.peaky.R;
import com.example.peaky.model.Result;
import com.example.peaky.model.user.User;
import com.example.peaky.repository.user.UserRepository;
import com.example.peaky.ui.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
    private final MutableLiveData<String> authenticationError = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);
    private final UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<User> getAuthenticatedUser() {
        return authenticatedUser;
    }

    public MutableLiveData<Boolean> getIsAuthenticated() {
        return isAuthenticated;
    }

    public MutableLiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void register(String email, String password) {
        userRepository.register(email, password).observeForever(result -> {
            if (result instanceof Result.Success) {
                statusMessage.setValue(((Result.Success<String>) result).getData());
                isAuthenticated.setValue(true);
            } else if (result instanceof Result.Error) {
                statusMessage.setValue(((Result.Error<String>) result).getException().getMessage());
            }
        });
    }

    public void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Ottieni l'oggetto FirebaseUser
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            // Recupera i dati dell'utente dal Realtime Database
                            userRepository.getUserData(uid).observeForever(result -> {
                                if (result instanceof Result.Success) {
                                    User user = ((Result.Success<User>) result).getData();
                                    authenticatedUser.setValue(user);
                                    isAuthenticated.setValue(true);
                                } else if (result instanceof Result.Error) {
                                    statusMessage.setValue("Error fetching user data");
                                    isAuthenticated.setValue(false);
                                }
                            });
                        }
                    } else {
                        // Se il login fallisce, imposta isAuthenticated a false
                        isAuthenticated.setValue(false);
                    }
                });
    }

    public void signInWithGoogle(String idToken) {
        userRepository.authenticateWithGoogle(idToken, result -> {
            if (result instanceof Result.Success) {
                User user = ((Result.Success<User>) result).getData();
                authenticatedUser.postValue(user);
            } else if (result instanceof Result.Error) {
                Exception exception = ((Result.Error<User>) result).getException();
                authenticationError.postValue(exception.getMessage());
            }
        });
    }

    public void signOut() {
        userRepository.signOut();
        authenticatedUser.setValue(null);
        isAuthenticated.setValue(false);
    }

    public LiveData<String[]> getGoogleUserData() {
        return userRepository.getGoogleUserData();
    }

    public LiveData<Boolean> saveUserData(String name, String surname, String birthDate, String gender) {
        return userRepository.saveUserData(name, surname, birthDate, gender);
    }

    public void checkUserDataAndNavigate(NavController navController, Context context, boolean fromLoginFragment) {
        getAuthenticatedUser().observeForever(user -> {
            if (user != null) {
                String userId = user.getUid();

                userRepository.isUserRegistrationComplete(userId).observeForever(result -> {
                    if (result instanceof Result.Success) {
                        boolean isComplete = ((Result.Success<Boolean>) result).getData();

                        if (isComplete) {
                            // Dati completi, naviga alla schermata principale
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } else {
                            // Dati incompleti, naviga al fragment UserInfo
                            navigateToUserInfo(navController, fromLoginFragment);
                        }
                    } else if (result instanceof Result.Error) {
                        statusMessage.setValue("Error checking user data");
                    }
                });
            }
        });
    }

    private void navigateToUserInfo(NavController navController, boolean fromLoginFragment) {
        int currentDestinationId = navController.getCurrentDestination() != null
                ? navController.getCurrentDestination().getId()
                : -1;

        // Naviga solo se non sei già in fragment_user_info
        if (currentDestinationId != R.id.fragment_user_info) {
            int action = fromLoginFragment ? R.id.action_fragmentLogin_to_fragmentUserInfo
                    : R.id.action_fragmentSignUp_to_fragmentUserInfo;

            navController.navigate(action, null, new NavOptions.Builder()
                    .setPopUpTo(R.id.fragment_login, true)  // Rimuove fragment_login dalla back stack
                    .build());
        } else {
            Log.d("UserViewModel", "Already in fragment_user_info, not navigating.");
        }
    }

    public LiveData<Result<Boolean>> isUserRegistrationComplete (String userId) {
        return userRepository.isUserRegistrationComplete(userId);
    }

    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }

    public String getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }
}