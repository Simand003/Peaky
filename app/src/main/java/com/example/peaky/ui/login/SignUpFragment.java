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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

public class SignUpFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText textInputEmail, textInputPassword, textInputPasswordConfirm;
    private TextInputLayout containerPassword, containerConfirmPassword;

    private GoogleSignInClient googleSignInClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserViewModelFactory factory = new UserViewModelFactory();
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inizializza le variabili
        Button signUpButton = view.findViewById(R.id.button_signUp);
        Button signInGoogleButton = view.findViewById(R.id.loginWithGoogleButton);
        textInputEmail = view.findViewById(R.id.textField_email);
        containerPassword = view.findViewById(R.id.container_password);
        textInputPassword = view.findViewById(R.id.textField_password);
        textInputPasswordConfirm = view.findViewById(R.id.textField_passwordConfirm);
        containerConfirmPassword = view.findViewById(R.id.container_passwordConfirm);

        addTextWatchers();  // Aggiungi TextWatchers per gestione degli errori

        // Listener per il bottone di registrazione
        signUpButton.setOnClickListener(v -> {
            String email = textInputEmail.getText().toString().trim();
            String password = textInputPassword.getText().toString().trim();
            String confirmPassword = textInputPasswordConfirm.getText().toString().trim();

            // Validazione
            if (isFormValid(email, password, confirmPassword)) {
                userViewModel.register(email, password);
            }
        });

        observeAuthentication();

        // Configura GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        signInGoogleButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        NavController navController = Navigation.findNavController(requireView());
        userViewModel.checkUserDataAndNavigate(navController, requireContext(), false);
    }

    private void addTextWatchers() {
        // Metodo generico per gestire i TextWatcher di password e conferma password
        setPasswordTextWatcher(textInputPassword, containerPassword);
        setPasswordTextWatcher(textInputPasswordConfirm, containerConfirmPassword);
    }

    private void setPasswordTextWatcher(TextInputEditText editText, TextInputLayout container) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setError(null);
                container.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setError(null);
                container.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
    }

    private boolean isFormValid(String email, String password, String confirmPassword) {
        boolean isEmailValid = isEmailOk(email);
        boolean isPasswordValid = isPasswordOk(password);
        boolean doPasswordsMatch = password.equals(confirmPassword);

        if (!isPasswordValid) {
            textInputPassword.setError(getString(R.string.error_password_login));
            containerPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        }

        if (!doPasswordsMatch) {
            textInputPasswordConfirm.setError(getString(R.string.error_password_confirm_login));
            containerConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
        }

        return isEmailValid && isPasswordValid && doPasswordsMatch;
    }

    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            textInputEmail.setError(getString(R.string.error_email_login));
            return false;
        }
        textInputEmail.setError(null);
        return true;
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            return false;
        }
        return true;
    }

    private void observeAuthentication() {
        // Mostra errori nella Snackbar
        userViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                hideKeyboard();
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Gestisce il login riuscito
        userViewModel.getIsAuthenticated().observe(getViewLifecycleOwner(), isSuccessful -> {
            if (Boolean.TRUE.equals(isSuccessful)) {
                Navigation.findNavController(requireView()).navigate(R.id.action_fragmentSignUp_to_fragmentUserInfo);
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

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}