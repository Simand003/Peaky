package com.example.peaky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.peaky.R;
import com.example.peaky.model.Sport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class SportRecyclerAdapter extends RecyclerView.Adapter<SportRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Sport> sportList;
    private Set<Integer> selectedPositions = new HashSet<>(); // Set per tracciare tutte le posizioni selezionate

    public SportRecyclerAdapter(Context context, List<Sport> sportList) {
        this.context = context;
        this.sportList = sportList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconSport, checkIcon;
        TextView name;

        public ViewHolder(View view) {
            super(view);
            iconSport = view.findViewById(R.id.icon_sport);
            checkIcon = view.findViewById(R.id.image_view_check);
            name = view.findViewById(R.id.text);
        }
    }

    @Override
    public SportRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_default_sport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SportRecyclerAdapter.ViewHolder holder, int position) {
        Sport sport = sportList.get(position);
        holder.name.setText(sport.getName());
        holder.iconSport.setImageResource(sport.getIcon());

        // Imposta l'icona di check in base alla selezione
        if (selectedPositions.contains(position)) {
            holder.checkIcon.setImageResource(R.drawable.baseline_radio_button_checked_24); // Icona selezionata
        } else {
            holder.checkIcon.setImageResource(R.drawable.baseline_radio_button_unchecked_24); // Icona non selezionata
        }

        // Gestisci il clic sul singolo elemento
        holder.itemView.setOnClickListener(v -> {
            // Cambia lo stato di selezione: se selezionato, deseleziona, se non selezionato, seleziona
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position); // Deseleziona
            } else {
                selectedPositions.add(position); // Seleziona
            }
            // Notifica che il dataset è cambiato per aggiornare la view
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return sportList.size();
    }

    public List<Sport> getSelectedSports() {
        List<Sport> selectedSports = new ArrayList<>();
        for (Integer position : selectedPositions) {
            selectedSports.add(sportList.get(position));
        }
        return selectedSports;
    }
}
