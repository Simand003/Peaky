package com.example.peaky.repository.sport;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.peaky.R;
import com.example.peaky.model.Sport;
import com.example.peaky.source.SportDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SportRepository {
    private final Context context;
    private final SportDataSource sportDataSource;

    public SportRepository(Context context, SportDataSource sportDataSource) {
        this.context = context;
        this.sportDataSource = sportDataSource;
    }

    public List<Sport> getSports() {
        List<Sport> sportsList = new ArrayList<>();

        try {
            InputStream is = context.getResources().openRawResource(R.raw.sports);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("mountain_sports");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String iconName = obj.getString("icon");

                int iconId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());

                if (iconId == 0) {
                    iconId = R.drawable.sport_ic_hiking;
                }

                sportsList.add(new Sport(name, iconId));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return sportsList;
    }

    public void setDefaultEquipmentForSport(String sportName, String type, String equipmentId, String userId) {
        sportDataSource.setDefaultEquipmentForSport(sportName, type, equipmentId, userId);
    }

    public void getSportsWhereEquipmentIsDefault(String userId, String equipmentId, SportDataSource.OnSportsResultListener listener) {
        sportDataSource.getSportsWhereEquipmentIsDefault(userId, equipmentId, listener);
    }
}

