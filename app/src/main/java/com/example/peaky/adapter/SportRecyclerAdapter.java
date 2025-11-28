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
import com.example.peaky.ui.home.record_activity.saveactivity.OnSportSelectedListener;

import java.util.List;
import java.util.ArrayList;

public class SportRecyclerAdapter extends RecyclerView.Adapter<SportRecyclerAdapter.SportViewHolder> {

    private final Context context;
    private final List<Sport> sports;
    private String selectedSportName = null; // Usa il nome sport come id
    private final OnSportSelectedListener listener;

    public SportRecyclerAdapter(Context context, List<Sport> sports, OnSportSelectedListener listener) {
        this.context = context;
        this.sports = new ArrayList<>(sports);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_sport, parent, false);
        return new SportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportViewHolder holder, int position) {
        Sport sport = sports.get(position);
        holder.textView.setText(sport.getName());
        holder.iconImageView.setImageResource(sport.getIcon());

        boolean isSelected = sport.getName().equals(selectedSportName);

        holder.checkImageView.setImageResource(isSelected
                ? R.drawable.baseline_radio_button_checked_24
                : R.drawable.baseline_radio_button_unchecked_24);

        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                selectedSportName = null;
            } else {
                selectedSportName = sport.getName();
            }
            notifyDataSetChanged();

            if (listener != null) {
                listener.onSportSelected(selectedSportName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sports.size();
    }

    public String getSelectedSportName() {
        return selectedSportName;
    }

    public void setSelectedSportByName(String sportName) {
        selectedSportName = sportName;
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

