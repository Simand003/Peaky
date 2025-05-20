package com.example.peaky.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peaky.R;
import com.example.peaky.model.equipment.Equipment;

import java.util.List;
import java.util.Locale;

public class EquipmentRecyclerAdapter extends RecyclerView.Adapter<EquipmentRecyclerAdapter.ViewHolder> {

    private final List<Equipment> equipmentList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Equipment equipment);
    }

    public EquipmentRecyclerAdapter(List<Equipment> list, OnItemClickListener listener) {
        this.equipmentList = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, info;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textView_name);
            info = view.findViewById(R.id.textView_info);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);
        holder.name.setText(equipment.getBrand() + " " + equipment.getModel());

        double price = equipment.getPrice() != null ? equipment.getPrice() : 0.0;
        holder.info.setText(String.format(Locale.getDefault(), "%.2f€", price));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(equipment));
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }
}

