package com.example.peaky.ui.home.record_activity.record;

import static com.example.peaky.util.Constants.CANCEL;
import static com.example.peaky.util.Constants.GO_TO_SETTINGS;
import static com.example.peaky.util.Constants.LOCATION_NECESSARY_FOR_RECORDING;
import static com.example.peaky.util.Constants.LOCATION_PERMISSION_PERMANENTLY_DENIED;
import static com.example.peaky.util.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.peaky.util.Constants.LOCATION_PERMISSION_REQUIRED;
import static com.example.peaky.util.Constants.OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.peaky.R;
import com.example.peaky.repository.ActivityRepository;
import com.example.peaky.repository.OSMRepository;
import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.source.SportDataSource;
import com.example.peaky.source.activity.ActivityFirestoreDataSource;
import com.example.peaky.source.activity.ActivityLocalDataSource;
import com.example.peaky.source.osm.OSMDataSource;
import com.example.peaky.ui.home.HomeActivity;
import com.example.peaky.ui.home.record_activity.ActivityDataRecordedViewModel;
import com.example.peaky.ui.home.record_activity.ActivityDataRecordedViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

public class RecordFragment extends Fragment {

    // STRINGHE RICERCA GPS
    public static final String FOUND = "Found";
    public static final String SEARCHING = "Searching";
    public static final String NOT_FOUND = "Not found";

    private BottomNavigationView bottomNavigationView;

    private LinearLayout linearLayoutGPSLocator, buttonsContainer;
    private TextView textGPSLocator, textAltitude, textViewTimer, textViewDistance;
    private static final long SEARCH_TIMEOUT = 10000;
    private long lastLocationUpdate = 0;
    private FusedLocationProviderClient locationClient;
    private Handler handler;
    private boolean isSearching = false;

    private Button buttonBack;
    private ImageButton buttonGoToPosition, buttonRecordAction, buttonRecordEnd, buttonRecordedData,
            buttonReporterTools;
    private View bottomSheetData, bottomSheetReporter;
    private BottomSheetBehavior<View> bottomSheetDataBehavior, bottomSheetReporterBehavior;

    private MapView mapView;
    private Marker userMarker;

    private boolean returningFromSettings = false;

    private boolean isFirstLocationUpdate = true;
    private boolean isCentralizing = false;

    private RecordViewModel recordViewModel;
    private SportRepository sportRepository;
    private OSMRepository osmRepository;
    private ActivityRepository activityRepository;
    private ActivityDataRecordedViewModel activityDataRecordedViewModel;

    private boolean callbacksAdded = false;

    public RecordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        osmRepository = new OSMRepository(new OSMDataSource());

        SportDataSource sportDataSource = new SportDataSource(FirebaseFirestore.getInstance());
        sportRepository = new SportRepository(requireContext(), sportDataSource);

        RecordViewModelFactory factory = new RecordViewModelFactory(osmRepository);
        recordViewModel = new ViewModelProvider(this, factory).get(RecordViewModel.class);

        ActivityLocalDataSource activityLocalDataSource = new ActivityLocalDataSource();
        ActivityFirestoreDataSource activityFirestoreDataSource = new ActivityFirestoreDataSource(FirebaseFirestore.getInstance());
        activityRepository = new ActivityRepository(requireContext(), activityFirestoreDataSource, activityLocalDataSource);

        activityDataRecordedViewModel = new ViewModelProvider(requireActivity(),
                new ActivityDataRecordedViewModelFactory(activityRepository, sportRepository))
                .get(ActivityDataRecordedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        //Initializing the text view
        //textAltitude = view.findViewById(R.id.textAltitude);
        textViewTimer = view.findViewById(R.id.textView_chronometer);
        textViewDistance = view.findViewById(R.id.textView_distance);

        //Initializing the buttons
        buttonsContainer = view.findViewById(R.id.buttons_container);
        buttonBack = view.findViewById(R.id.button_back);
        buttonGoToPosition = view.findViewById(R.id.button_go_to_position);
        buttonRecordAction = view.findViewById(R.id.button_record_action);
        buttonRecordEnd = view.findViewById(R.id.button_record_end);
        buttonRecordEnd.setVisibility(View.GONE);
        buttonRecordedData = view.findViewById(R.id.button_recorded_data);
        buttonReporterTools = view.findViewById(R.id.button_reporters_tool);

        //Initializing the linear layout for the GPS locator
        linearLayoutGPSLocator = view.findViewById(R.id.linearLayoutGPSLocator);
        textGPSLocator = view.findViewById(R.id.textGPSLocator);
        handler = new Handler();
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        //Initializing the bottom sheets
        bottomSheetData = view.findViewById(R.id.bottomSheet_data);
        bottomSheetDataBehavior = BottomSheetBehavior.from(bottomSheetData);
        bottomSheetReporter = view.findViewById(R.id.bottomSheet_reporter);
        bottomSheetReporterBehavior = BottomSheetBehavior.from(bottomSheetReporter);

        //Setting the behavior of the bottom sheets
        bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mapView = view.findViewById(R.id.mapView);

        checkLocationPermission();
        setupObservers();
        setupListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                if (!isCentralizing) {
                    buttonGoToPosition.setImageResource(R.drawable.record_position_empty);
                }
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });

        recordViewModel.getButtonMarginBottom().observe(getViewLifecycleOwner(), marginBottom -> {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) buttonsContainer.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, 16, marginBottom);
            buttonsContainer.setLayoutParams(params);
        });

        setupBottomSheetCallbacks();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeMap();
            startLocationUpdates();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initializeMap() {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        mapController.setCenter(new GeoPoint(45.8566, 9.3972));

        userMarker = new Marker(mapView);
        userMarker.setIcon(getResources().getDrawable(R.drawable.position_marker));
        mapView.getOverlays().add(userMarker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap();
                startLocationUpdates();
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermanentDenialDialog();
                } else {
                    showPermissionDeniedDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(LOCATION_PERMISSION_REQUIRED)
                .setMessage(LOCATION_NECESSARY_FOR_RECORDING)
                .setPositiveButton(OK, (dialog, which) -> checkLocationPermission())
                .setNegativeButton(CANCEL, (dialog, which) -> closeFragment())
                .setCancelable(false)
                .show();
    }

    private void showPermanentDenialDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(LOCATION_PERMISSION_REQUIRED)
                .setMessage(LOCATION_PERMISSION_PERMANENTLY_DENIED)
                .setPositiveButton(GO_TO_SETTINGS, (dialog, which) -> {
                    returningFromSettings = true;
                    openAppSettings();
                })
                .setNegativeButton(CANCEL, (dialog, which) -> closeFragment())
                .setCancelable(false)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void closeFragment() {
        if (isAdded()) {
            handler.removeCallbacksAndMessages(null);
            locationClient.removeLocationUpdates(locationCallback);

            if (mapView != null) {
                mapView.onPause();
                mapView.onDetach();
            }

            getParentFragmentManager().beginTransaction().remove(this).commit();

            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (returningFromSettings) {
            returningFromSettings = false;
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                initializeMap();
                startLocationUpdates();
            } else {
                if (bottomNavigationView != null) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                }
            }
        }

        if (mapView != null) {
            mapView.onResume();
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                boolean rec = Boolean.TRUE.equals(activityDataRecordedViewModel.getIsRecording().getValue());
                if (!rec) {
                    closeFragment();
                } else {
                    if (bottomNavigationView != null) {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        LinearLayout popup = getView().findViewById(R.id.recording_popup_container);
                        popup.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        callbacksAdded = false;

        if (mapView != null) {
            mapView.onPause();
            mapView.onDetach();
        }

        locationClient.removeLocationUpdates(locationCallback);
        handler.removeCallbacksAndMessages(null);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000) // Cerca ogni 5 secondi
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        cambiaStato(0);
        isSearching = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastLocationUpdate > SEARCH_TIMEOUT) {
                    cambiaStato(2);
                    isSearching = false;
                }

                handler.postDelayed(this, 5000);
            }
        }, 5000);


        locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || locationResult.getLocations().isEmpty()) {
                return;
            }

            Location location = locationResult.getLastLocation();

            activityDataRecordedViewModel.addLocationPoint(location);

            if (location != null) {
                isSearching = false;
                cambiaStato(1);
                lastLocationUpdate = System.currentTimeMillis();

                //double altitude = location.getAltitude();
                //textAltitude.setText(String.format(Locale.getDefault(), "%.2f m", altitude));

                GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                if (userMarker != null) {
                    userMarker.setPosition(userLocation);
                }

                if (isFirstLocationUpdate) {
                    isFirstLocationUpdate = false;
                    IMapController mapController = mapView.getController();
                    buttonGoToPosition.setImageResource(R.drawable.record_position_full);
                    isCentralizing = true;
                    handler.postDelayed(() -> isCentralizing = false, 2000);
                    mapController.animateTo(userLocation);

                }
            }
        }

    };

    private void cambiaStato(int stato) {
        linearLayoutGPSLocator.getBackground().setTint(ContextCompat.getColor(requireContext(), recordViewModel.getBackgroundColor(stato)));
        textGPSLocator.setText(recordViewModel.getTextForState(stato));
    }

    private void centerOnUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    IMapController mapController = mapView.getController();
                    buttonGoToPosition.setImageResource(R.drawable.record_position_full);
                    isCentralizing = true;
                    handler.postDelayed(() -> isCentralizing = false, 1500);
                    mapController.animateTo(userLocation);
                }
            });

        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupBottomSheetCallbacks() {
        if (callbacksAdded) return;

        BottomSheetBehavior.BottomSheetCallback sharedCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                adjustButtonsForBothSheets();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                adjustButtonsForBothSheets();
            }
        };

        bottomSheetDataBehavior.addBottomSheetCallback(sharedCallback);
        bottomSheetReporterBehavior.addBottomSheetCallback(sharedCallback);

        callbacksAdded = true;

        buttonRecordedData.setOnClickListener(v -> {
            if (bottomSheetReporterBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        buttonReporterTools.setOnClickListener(v -> {
            if (bottomSheetDataBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            bottomSheetReporterBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    private void adjustButtonsForBothSheets() {
        View sheetToConsider = null;

        // Priorità: lo sheet più "alto" vince
        if (bottomSheetReporterBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                bottomSheetReporterBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING ||
                bottomSheetReporterBehavior.getState() == BottomSheetBehavior.STATE_SETTLING) {

            sheetToConsider = bottomSheetReporter;
        }
        else if (bottomSheetDataBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                bottomSheetDataBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING ||
                bottomSheetDataBehavior.getState() == BottomSheetBehavior.STATE_SETTLING) {

            sheetToConsider = bottomSheetData;
        }

        if (sheetToConsider != null) {
            recordViewModel.adjustButtonPosition(sheetToConsider);
        } else {
            recordViewModel.resetButtonPosition();
        }
    }

    private void setupObservers() {
        activityDataRecordedViewModel.getIsRecording().observe(getViewLifecycleOwner(), isRecording -> {
            long time = activityDataRecordedViewModel.getElapsedTime().getValue();

            if (isRecording) {
                buttonRecordAction.setImageResource(R.drawable.record_pause);
                buttonRecordEnd.setVisibility(View.GONE);
            } else {
                buttonRecordAction.setImageResource(R.drawable.record_continue);

                // Mostra il tasto END solo se hai registrato almeno un secondo
                if (time > 0) {
                    buttonRecordEnd.setVisibility(View.VISIBLE);
                } else {
                    buttonRecordEnd.setVisibility(View.GONE);
                }
            }
        });

        activityDataRecordedViewModel.getElapsedTime().observe(getViewLifecycleOwner(), time -> {
            textViewTimer.setText(formatTime(time));
        });
        activityDataRecordedViewModel.getDistance().observe(getViewLifecycleOwner(), distance -> {
            textViewDistance.setText(formatDistance(distance));
        });

        activityDataRecordedViewModel.getPolylinePoints().observe(getViewLifecycleOwner(), points -> {
            if (mapView == null) return;

            mapView.getOverlayManager().removeIf(overlay -> overlay instanceof Polyline);

            Polyline polyline = new Polyline();
            polyline.setPoints(points);
            mapView.getOverlayManager().add(polyline);

            mapView.invalidate();
        });

    }

    private void setupListeners() {
        // START / PAUSE
        buttonRecordAction.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(activityDataRecordedViewModel.getIsRecording().getValue())) {
                activityDataRecordedViewModel.stopRecording();
            } else {
                activityDataRecordedViewModel.startRecording();
                if (bottomSheetDataBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetDataBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        // END → vai al fragment successivo
        buttonRecordEnd.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.saveActivityFragment);
        });

        buttonGoToPosition.setOnClickListener(v -> centerOnUserLocation());

        buttonBack.setOnClickListener(v -> {
            boolean rec = Boolean.TRUE.equals(activityDataRecordedViewModel.getIsRecording().getValue());
            if (!rec) {
                closeFragment();
            } else {
                if (bottomNavigationView != null) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                }
            }

        });
    }

    private String formatTime(long ms) {
        long sec = ms / 1000;
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String formatDistance(double distanceInMeters) {
        double distanceInKm = distanceInMeters / 1000.0;
        return String.format("%.2f km", distanceInKm);
    }
}

// TODO: AGGIUNGERE ANIMAZIONE MARKER POSIZIONE