package com.example.peaky.repository.equipment;

import android.content.Context;
import android.util.Log;

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
    private final Map<String, String> typeToCategoryMap = new HashMap<>();

    public EquipmentRepository(Context context,
                                EquipmentDataSource equipmentDataSource) {
        this.context = context;
        this.equipmentDataSource = equipmentDataSource;
        loadCategoriesFromJSON();
    }

    private void loadCategoriesFromJSON() {
        try {
            // Leggi il file JSON da res/raw/equipment.json
            InputStream is = context.getResources().openRawResource(R.raw.equipment);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray categoriesArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                String categoryName = categoryObject.getString("name");

                ArrayList<String> types = new ArrayList<>();
                JSONArray typesArray = categoryObject.getJSONArray("types");
                for (int j = 0; j < typesArray.length(); j++) {
                    String type = typesArray.getString(j);
                    types.add(type);

                    // Mappa inversa: tipo → categoria
                    typeToCategoryMap.put(type, categoryName);
                }

                // Mappa principale: categoria → lista tipi
                equipmentCategoryTypesMap.put(categoryName, types);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EquipmentRepository", "Errore nel parsing del JSON: " + e.getMessage());
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

    public void getUserEquipment(String userId, EquipmentDataSource.EquipmentCallback callback) {
        equipmentDataSource.getUserEquipment(userId, callback);
    }

    public String getCategoryForType(String typeName) {
        return typeToCategoryMap.get(typeName);
    }

    public void deleteEquipment(String userId, String equipmentId, EquipmentDataSource.OnDeleteListener listener) {
        equipmentDataSource.deleteEquipment(userId, equipmentId, listener);
    }
}
