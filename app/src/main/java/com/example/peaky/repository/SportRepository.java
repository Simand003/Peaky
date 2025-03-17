package com.example.peaky.repository;

import android.content.Context;

import com.example.peaky.R;
import com.example.peaky.model.Sport;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SportRepository {

    public List<Sport> getSports(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.sports);
            Reader reader = new InputStreamReader(is);
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            Type listType = new TypeToken<List<Sport>>() {}.getType();
            return new Gson().fromJson(jsonObject.get("mountain_sports"), listType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}