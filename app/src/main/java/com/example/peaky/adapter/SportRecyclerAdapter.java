package com.example.peaky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peaky.R;
import com.example.peaky.model.Sport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class SportRecyclerAdapter extends RecyclerView.Adapter<SportRecyclerAdapter.SportViewHolder> {

    private final Context context;
    private final List<Sport> sports;
    private final Set<String> selectedSportNames; // Usa il nome sport come id

    public SportRecyclerAdapter(Context context, List<Sport> sports) {
        this.context = context;
        this.sports = new ArrayList<>(sports);
        this.selectedSportNames = new HashSet<>();
    }

    @NonNull
    @Override
    public SportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_default_sport, parent, false);
        return new SportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportViewHolder holder, int position) {
        Sport sport = sports.get(position);
        holder.textView.setText(sport.getName());

        boolean isSelected = selectedSportNames.contains(sport.getName());
        holder.checkImageView.setImageResource(isSelected
                ? R.drawable.baseline_radio_button_checked_24
                : R.drawable.baseline_radio_button_unchecked_24);

        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                selectedSportNames.remove(sport.getName());
            } else {
                selectedSportNames.add(sport.getName());
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return sports.size();
    }

    public List<Sport> getSelectedSports() {
        List<Sport> selected = new ArrayList<>();
        for (Sport sport : sports) {
            if (selectedSportNames.contains(sport.getName())) {
                selected.add(sport);
            }
        }
        return selected;
    }

    public void updateSports(List<Sport> newSports) {
        sports.clear();
        sports.addAll(newSports);
        notifyDataSetChanged();
    }

    public void setSelectedSportsByName(List<String> sportNames) {
        selectedSportNames.clear();
        if (sportNames != null) {
            selectedSportNames.addAll(sportNames);
        }
        notifyDataSetChanged();
    }

    static class SportViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView, checkImageView;
        TextView textView;

        public SportViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_sport);
            checkImageView = itemView.findViewById(R.id.image_view_check);
            textView = itemView.findViewById(R.id.text);
        }
    }
}

