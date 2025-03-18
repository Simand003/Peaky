package com.example.peaky.repository;

import android.content.Context;

import com.example.peaky.R;
import com.example.peaky.model.Sport;

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

    public SportRepository(Context context) {
        this.context = context;
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
}

