package com.example.peaky.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peaky.R;
import com.example.peaky.model.Peak;

import java.util.List;

public class AddPeakAdapter extends RecyclerView.Adapter<AddPeakAdapter.ViewHolder> {

    public interface OnPeakClickListener {
        void onPeakClick(Peak peak);
    }

    private List<Peak> peaks;
    private OnPeakClickListener listener;

    public AddPeakAdapter(List<Peak> peaks, OnPeakClickListener listener) {
        this.peaks = peaks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_peak, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Peak peak = peaks.get(position);

        holder.name.setText(peak.getName());
        holder.altitude.setText(peak.getAltitude() + " m");

        holder.itemView.setOnClickListener(v -> listener.onPeakClick(peak));
    }

    @Override
    public int getItemCount() {
        return peaks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, altitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.peak_name);
            altitude = itemView.findViewById(R.id.peak_altitude);
        }
    }
}
