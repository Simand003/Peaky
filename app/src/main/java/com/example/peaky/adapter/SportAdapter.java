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

public class SportAdapter extends ArrayAdapter<Sport> {
    private Context context;
    private List<Sport> sportsList;

    public SportAdapter(Context context, List<Sport> sportsList) {
        super(context, R.layout.spinner_sport, sportsList);
        this.context = context;
        this.sportsList = sportsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_sport, parent, false);
        }

        Sport sport = sportsList.get(position);
        ImageView icon = convertView.findViewById(R.id.spinner_icon);
        TextView name = convertView.findViewById(R.id.spinner_name);

        int iconResId = context.getResources().getIdentifier(sport.getIcon(), "drawable", context.getPackageName());
        icon.setImageResource(iconResId);

        name.setText(sport.getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return sportsList.size();
    }
}

