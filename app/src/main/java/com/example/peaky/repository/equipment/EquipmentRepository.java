package com.example.peaky.repository.equipment;

import android.content.Context;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;

import com.example.peaky.R;
import com.example.peaky.model.equipment.Equipment;
import com.example.peaky.source.EquipmentDataSource;

import java.util.HashMap;
import java.util.Map;

public class EquipmentRepository {

    private final Context context;
    private final Map<String, ArrayList<String>> equipmentCategoryTypesMap = new HashMap<>();
    private final MutableLiveData<ArrayList<String>> equipmentTypesLiveData = new MutableLiveData<>();
    private final EquipmentDataSource equipmentDataSource;

    public EquipmentRepository(Context context,
                                EquipmentDataSource equipmentDataSource) {
        this.context = context;
        this.equipmentDataSource = equipmentDataSource;
        loadCategoriesFromJSON();
    }

    private void loadCategoriesFromJSON() {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.equipment);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray categoriesArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                String name = categoryObject.getString("name");

                ArrayList<String> types = new ArrayList<>();
                JSONArray typesArray = categoryObject.getJSONArray("types");
                for (int j = 0; j < typesArray.length(); j++) {
                    types.add(typesArray.getString(j));
                }

                equipmentCategoryTypesMap.put(name, types);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getCategories() {
        return new ArrayList<>(equipmentCategoryTypesMap.keySet());
    }

    public ArrayList<String> getTypesForCategory(String category) {
        return equipmentCategoryTypesMap.getOrDefault(category, new ArrayList<>());
    }

    public void addEquipment(String userId, Equipment equipment) {
        equipmentDataSource.addEquipment(userId, equipment);
    }
}
