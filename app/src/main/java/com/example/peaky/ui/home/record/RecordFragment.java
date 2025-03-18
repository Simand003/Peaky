package com.example.peaky.ui.home.record;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.peaky.R;
import com.example.peaky.adapter.SportAdapter;
import com.example.peaky.model.Sport;
import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.SportRepository;
import com.example.peaky.source.osm.OSMDataSource;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class RecordFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private Spinner spinner;

    private Button buttonBack;
    private ImageButton buttonRecordAction, buttonRecordEnd, buttonRecordedData, buttonReporterTools;
    private View bottomSheetData, bottomSheetReporter;
    private BottomSheetBehavior<View> bottomSheetDataBehavior, bottomSheetReporterBehavior;

    private MapView mapView;

    private boolean isRecording = false;

    private SportRepository sportRepository;
    private RecordViewModel recordViewModel;
    private OSMRepository osmRepository;

    private FusedLocationProviderClient fusedLocationClient;

    public RecordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        sportRepository = new SportRepository(requireContext());
        osmRepository = new OSMRepository(new OSMDataSource());
        RecordViewModelFactory factory = new RecordViewModelFactory(sportRepository, osmRepository);
        recordViewModel = new ViewModelProvider(this, factory).get(RecordViewModel.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        //Initializing the buttons
        buttonBack = view.findViewById(R.id.button_back);
        buttonRecordAction = view.findViewById(R.id.button_record_action);
        buttonRecordEnd = view.findViewById(R.id.button_record_end);
        buttonRecordedData = view.findViewById(R.id.button_recorded_data);
        buttonReporterTools = view.findViewById(R.id.button_reporters_tool);

        //Initializing the bottom sheets
        bottomSheetData = view.findViewById(R.id.bottomSheet_data);
        bottomSheetDataBehavior = BottomSheetBehavior.from(bottomSheetData);
        bottomSheetReporter = view.findViewById(R.id.bottomSheet_reporter);
        bottomSheetReporterBehavior = BottomSheetBehavior.from(bottomSheetReporter);

        //Setting the behavior of the bottom sheets
        bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        spinner = view.findViewById(R.id.spinner_sports);

        mapView = view.findViewById(R.id.mapView);
        setupMap();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recordViewModel.getMapLocation().observe(getViewLifecycleOwner(), this::updateMap);

        recordViewModel.getSports().observe(getViewLifecycleOwner(), new Observer<List<Sport>>() {
            @Override
            public void onChanged(List<Sport> sports) {
                SportAdapter adapter = new SportAdapter(requireContext(), sports);
                spinner.setAdapter(adapter);
            }
        });

        buttonBack.setOnClickListener(v -> {
            getActivity().onBackPressed();
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        });

        buttonRecordAction.setOnClickListener(v -> {
            if (!isRecording) {
                buttonRecordAction.setImageResource(R.drawable.record_pause);
                buttonRecordEnd.setVisibility(View.GONE);
                isRecording = true;
            } else {
                buttonRecordAction.setImageResource(R.drawable.record_continue);
                buttonRecordEnd.setVisibility(View.VISIBLE);
                isRecording = false;
            }
        });

        buttonRecordedData.setOnClickListener(v -> {
            bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        buttonReporterTools.setOnClickListener(v -> {
            bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

    }

    private void setupMap() {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapView.setBuiltInZoomControls(false);

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se non hai i permessi, chiedi l'autorizzazione (in questo caso non stiamo gestendo il caso dei permessi)
            return;
        }

        // Ottieni la posizione corrente
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            // Se la posizione è disponibile, centrare la mappa sulla posizione dell'utente
            GeoPoint userLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mapView.getController().setZoom(18.0);
            mapView.getController().setCenter(userLocation);

            // Aggiungi un marker per la posizione dell'utente
            Marker marker = new Marker(mapView);
            marker.setPosition(userLocation);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle("Posizione Utente");
            mapView.getOverlays().add(marker);

            mapView.invalidate();
        } else {
            // Se la posizione non è disponibile, puoi gestirla (ad esempio, mostra un messaggio all'utente)
            Toast.makeText(requireContext(), "Impossibile ottenere la posizione", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    private void updateMap(GeoPoint location) {
        if (location != null) {
            mapView.getController().setCenter(location);

            Marker marker = new Marker(mapView);
            marker.setPosition(location);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle("Posizione OSM");
            mapView.getOverlays().add(marker);

            mapView.invalidate();
        }
    }

     */

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}