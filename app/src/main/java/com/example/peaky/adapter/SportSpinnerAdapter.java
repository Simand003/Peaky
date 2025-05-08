package com.example.peaky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peaky.R;
import com.example.peaky.model.Sport;

import java.util.List;

public class SportSpinnerAdapter extends ArrayAdapter<Sport> {

    public SportSpinnerAdapter(Context context, List<Sport> sports) {
        super(context, 0, sports);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_sport, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.spinner_icon);
        TextView text = convertView.findViewById(R.id.spinner_name);

        Sport sport = getItem(position);

        if (sport != null) {
            text.setText(sport.getName());
            icon.setImageResource(sport.getIcon());
        }

        return convertView;
    }
}