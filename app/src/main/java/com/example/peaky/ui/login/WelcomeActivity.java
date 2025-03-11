package com.example.peaky.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.peaky.R;
import com.example.peaky.model.Result;
import com.example.peaky.ui.home.HomeActivity;
import com.example.peaky.ui.login.viewmodel.UserViewModel;
import com.example.peaky.ui.login.viewmodel.UserViewModelFactory;

public class WelcomeActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this, new UserViewModelFactory()).get(UserViewModel.class);

        if (userViewModel.isUserLoggedIn()) {
            String userId = userViewModel.getCurrentUserId();

            if (userId != null) {
                userViewModel.isUserRegistrationComplete(userId).observe(this, result -> {
                    if (result instanceof Result.Success) {
                        Boolean isRegistrationComplete = ((Result.Success<Boolean>) result).getData();
                        if (isRegistrationComplete != null && isRegistrationComplete) {
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            userViewModel.signOut();
                            setContentView(R.layout.activity_welcome);
                        }
                    } else if (result instanceof Result.Error) {
                        userViewModel.signOut();
                        setContentView(R.layout.activity_welcome);
                    }
                });
            }
        } else {
            setContentView(R.layout.activity_welcome);
        }
    }
}
