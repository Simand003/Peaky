package com.example.peaky.ui.home;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.peaky.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ottieni il NavHostFragment dal FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navHostFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);

        // Verifica che il NavHostFragment sia stato trovato
        if (navHostFragment instanceof NavHostFragment) {
            // Ottieni il NavController dal NavHostFragment
            navController = ((NavHostFragment) navHostFragment).getNavController();
        } else {
            // Gestisci il caso in cui il NavHostFragment non è stato trovato
            throw new IllegalStateException("NavHostFragment non trovato");
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Usa l'if invece di switch per fare la navigazione
            if (item.getItemId() == R.id.navigation_home) {
                navController.navigate(R.id.homeFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_maps) {
                navController.navigate(R.id.mapsFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_record) {
                navController.navigate(R.id.recordFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_challenges) {
                navController.navigate(R.id.achievementsFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_profile) {
                navController.navigate(R.id.profileFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        int currentDestination = navController.getCurrentDestination().getId();

        if (currentDestination == R.id.saveActivityFragment) {
            navController.popBackStack(R.id.navigation_record, false);
        } else if (currentDestination == R.id.navigation_record) {
            navController.popBackStack(R.id.navigation_home, false);
        } else {
            super.onBackPressed();
        }
    }

}