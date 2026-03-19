package com.example.peaky.ui.home.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.peaky.R;
import com.example.peaky.adapter.AddPeakAdapter;
import com.example.peaky.model.Peak;
import com.example.peaky.repository.OSMRepository;
import com.example.peaky.source.osm.OSMDataSource;
import com.example.peaky.util.Constants;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private OSMRepository osmRepository;

    private MapView mapView;
    private Marker userMarker;

    private ImageButton addPeakButton, goToPositionButton;

    private LinearLayout bottomSheet;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private RecyclerView recyclerPeaks;

    private boolean isAddModeActive = false;

    private List<Peak> allPeaks = new ArrayList<>();

    public MapsFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        osmRepository = new OSMRepository(new OSMDataSource());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = view.findViewById(R.id.mapView);

        addPeakButton = view.findViewById(R.id.button_add_peak);
        goToPositionButton = view.findViewById(R.id.button_go_to_position);

        bottomSheet = view.findViewById(R.id.bottom_sheet_add_peaks);
        recyclerPeaks = view.findViewById(R.id.recycler_add_peaks);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        initializeMap();
        setupMapLongPress();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPeakButton.setOnClickListener(v -> {
            if (!isAddModeActive) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Do you want to add a peak to the database?")
                        .setMessage("To add a peak to the database, press and hold on the peak.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            isAddModeActive = true;
                            addPeakButton.setImageResource(R.drawable.ic_cross);
                        })
                        .setNegativeButton(Constants.CANCEL, null)
                        .show();
            } else {
                isAddModeActive = false;
                addPeakButton.setImageResource(R.drawable.ic_add);
            }
        });
    }

    // --------------- MAP -----------------

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

    private void setupMapLongPress() {

        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                if (!isAddModeActive) return false;

                handleLongPress(p);
                return true;
            }
        };

        mapView.getOverlays().add(new MapEventsOverlay(receiver));
    }

    private void handleLongPress(GeoPoint point) {

        osmRepository.getNearbyPeaks(
                point.getLatitude(),
                point.getLongitude(),
                new OSMDataSource.Callback() {

                    @Override
                    public void onSuccess(List<Peak> peaks) {

                        requireActivity().runOnUiThread(() -> {

                            if (peaks.isEmpty()) {
                                Toast.makeText(getContext(),
                                        "No peaks found",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (peaks.size() == 1) {
                                onPeakSelected(peaks.get(0));
                            } else {
                                showPeaksBottomSheet(peaks);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        "Error fetching peaks",
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }

    private void showPeaksBottomSheet(List<Peak> peaks) {

        recyclerPeaks.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerPeaks.setAdapter(new AddPeakAdapter(peaks, peak -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            onPeakSelected(peak);
        }));

        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void onPeakSelected(Peak peak) {
        Toast.makeText(getContext(),
                "Selected: " + peak.getName(),
                Toast.LENGTH_SHORT).show();
    }
}
