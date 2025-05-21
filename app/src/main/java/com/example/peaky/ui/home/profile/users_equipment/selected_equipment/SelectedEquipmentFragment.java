package com.example.peaky.ui.home.profile.users_equipment.selected_equipment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.peaky.R;
import com.example.peaky.model.equipment.Equipment;
import com.example.peaky.repository.sport.SportRepository;
import com.example.peaky.repository.sport.SportRepositoryFactory;

import java.util.List;
import java.util.Locale;

public class SelectedEquipmentFragment extends Fragment {

    private TextView selectedEquipment, textBrand, textModel, textPurchaseDate, textPrice,
            textNotes, textDistance, textElevation, textUses, textPeaksReached, textDefaultSports;
    private Button buttonBack;
    private LinearLayout notesContainer, defaultSportsContainer;

    private SelectedEquipmentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SportRepository sportRepository = SportRepositoryFactory.getSportRepository(requireContext());
        viewModel = new SelectedEquipmentViewModel(sportRepository);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_equipment, container, false);

        // Header
        selectedEquipment = view.findViewById(R.id.selected_equipment);
        buttonBack = view.findViewById(R.id.button_back);

        // Binding valori dai layout inclusi
        textBrand = view.findViewById(R.id.brand_row).findViewById(R.id.value);
        textModel = view.findViewById(R.id.model_row).findViewById(R.id.value);
        textPurchaseDate = view.findViewById(R.id.date_row).findViewById(R.id.value);
        textPrice = view.findViewById(R.id.price_row).findViewById(R.id.value);
        textDistance = view.findViewById(R.id.distance_row).findViewById(R.id.value);
        textElevation = view.findViewById(R.id.elevation_row).findViewById(R.id.value);
        textUses = view.findViewById(R.id.uses_row).findViewById(R.id.value);
        textPeaksReached = view.findViewById(R.id.peaks_row).findViewById(R.id.value);
        textNotes = view.findViewById(R.id.text_notes_content);
        textDefaultSports = view.findViewById(R.id.text_deafult_sports_content);

        notesContainer = view.findViewById(R.id.notes_container);
        defaultSportsContainer = view.findViewById(R.id.deafult_sports_container);

        ((TextView) view.findViewById(R.id.brand_row).findViewById(R.id.label)).setText(R.string.brand);
        ((TextView) view.findViewById(R.id.model_row).findViewById(R.id.label)).setText(R.string.model);
        ((TextView) view.findViewById(R.id.date_row).findViewById(R.id.label)).setText(R.string.purchase_date);
        ((TextView) view.findViewById(R.id.price_row).findViewById(R.id.label)).setText(R.string.price);
        ((TextView) view.findViewById(R.id.distance_row).findViewById(R.id.label)).setText(R.string.distance_min);
        ((TextView) view.findViewById(R.id.elevation_row).findViewById(R.id.label)).setText(R.string.elevation_gain_min);
        ((TextView) view.findViewById(R.id.uses_row).findViewById(R.id.label)).setText(R.string.uses);
        ((TextView) view.findViewById(R.id.peaks_row).findViewById(R.id.label)).setText(R.string.peaks_reached);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Equipment equipment = (Equipment) getArguments().getSerializable("equipment");
        List<String> associatedSports = getArguments().getStringArrayList("associatedSports");

        populateTextView(equipment);

        if (associatedSports != null && !associatedSports.isEmpty()) {
            defaultSportsContainer.setVisibility(View.VISIBLE);
            StringBuilder s = new StringBuilder();
            for (String sport : associatedSports) {
                s.append(sport).append(", ");
            }
            if (s.length() > 0) s.setLength(s.length() - 2);
            textDefaultSports.setText(s.toString());
        } else {
            defaultSportsContainer.setVisibility(View.GONE);
        }

        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    public void populateTextView(Equipment equipment) {
        if (equipment != null) {
            selectedEquipment.setText(equipment.getBrand() + " " + equipment.getModel());
            textBrand.setText(equipment.getBrand());
            textModel.setText(equipment.getModel());
            if (equipment.getPurchase_date() != null) {
                textPurchaseDate.setText(equipment.getFormattedPurchaseDate());
            } else {
                View dateRow = getView().findViewById(R.id.date_row);
                    dateRow.setVisibility(View.GONE);
            }
            if (equipment.getFormattedPurchaseDate() != null) {
                textPurchaseDate.setText(equipment.getFormattedPurchaseDate());
            } else {
                View dateRow = getView().findViewById(R.id.date_row);
                if (dateRow != null) dateRow.setVisibility(View.GONE);
            }
            if (equipment.getPrice() != 0) {
                textPrice.setText(String.format(Locale.getDefault(), "%.2f€", equipment.getPrice()));
            } else {
                View priceRow = getView().findViewById(R.id.price_row);
                if (priceRow != null) priceRow.setVisibility(View.GONE);
            }
            if (equipment.getDistance() != 0) {
                textDistance.setText(String.format(Locale.getDefault(), "%.2f km", equipment.getDistance()));
            } else {
                View distanceRow = getView().findViewById(R.id.distance_row);
                if (distanceRow != null) distanceRow.setVisibility(View.GONE);
            }
            if (equipment.getElevation_gain() != 0) {
                textElevation.setText(String.format(Locale.getDefault(), "%.2f m", equipment.getElevation_gain()));
            } else {
                View elevationRow = getView().findViewById(R.id.elevation_row);
                if (elevationRow != null) elevationRow.setVisibility(View.GONE);
            }
            if (equipment.getUses() != 0) {
                textUses.setText(String.format(Locale.getDefault(), "%d", equipment.getUses()));
            } else {
                View usesRow = getView().findViewById(R.id.uses_row);
                if (usesRow != null) usesRow.setVisibility(View.GONE);
            }
            if (equipment.getPeaks_reached() != 0) {
                textPeaksReached.setText(String.format(Locale.getDefault(), "%d", equipment.getPeaks_reached()));
            } else {
                View peaksRow = getView().findViewById(R.id.peaks_row);
                if (peaksRow != null) peaksRow.setVisibility(View.GONE);
            }
            if (equipment.getNotes() != null && !equipment.getNotes().isEmpty()) {
                textNotes.setText(equipment.getNotes());
            } else {
                notesContainer.setVisibility(View.GONE);
            }

        }
    }
}
