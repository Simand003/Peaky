package com.example.peaky.ui.login;

import static com.example.peaky.util.Constants.RC_SIGN_IN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.peaky.R;
import com.example.peaky.ui.login.viewmodel.UserViewModel;
import com.example.peaky.ui.login.viewmodel.UserViewModelFactory;
import com.example.peaky.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private UserViewModel userViewModel;

    private TextInputLayout containerPassword;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, loginGoogleButton;

    private GoogleSignInClient googleSignInClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this, new UserViewModelFactory()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.textField_email);
        passwordEditText = view.findViewById(R.id.textField_password);
        containerPassword = view.findViewById(R.id.container_password);
        loginButton = view.findViewById(R.id.button_login);
        loginGoogleButton = view.findViewById(R.id.loginWithGoogleButton);

        addTextWatchers();

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (isEmailOk(email) & isPasswordOk(password)) {
                userViewModel.login(email, password);
            }
        });

        observeAuthentication();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        loginGoogleButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        NavController navController = Navigation.findNavController(requireView());
        userViewModel.checkUserDataAndNavigate(navController, requireContext(), true);
    }

    private void addTextWatchers() {
        // Aggiungi TextWatcher per la password (senza errori immediati)
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Rimuove l'errore mentre l'utente scrive, senza visualizzare l'errore immediatamente
                passwordEditText.setError(null);
                containerPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Rimuovi gli errori quando l'utente interagisce con i campi
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passwordEditText.setError(null);
                containerPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
    }

    private void observeAuthentication() {
        userViewModel.getIsAuthenticated().observe(getViewLifecycleOwner(), isAuthenticated -> {
            if (isAuthenticated) {
                NavController navController = Navigation.findNavController(requireView());
                userViewModel.checkUserDataAndNavigate(navController, requireContext(), true);
            }
        });

        userViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                hideKeyboard();
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    userViewModel.signInWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            emailEditText.setError(getString(R.string.error_email_login));
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            passwordEditText.setError(getString(R.string.error_password_login));
            containerPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}