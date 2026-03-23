package com.example.peaky.source.osm;

import androidx.annotation.NonNull;

import com.example.peaky.model.Peak;

import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OSMDataSource {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";

    private OkHttpClient client = new OkHttpClient();

    public interface Callback {
        void onSuccess(List<Peak> peaks);
        void onError(Exception e);
    }

    public void getPeaksInArea(double north, double south, double east, double west, Callback callback) {

        String query = "[out:json];node[\"natural\"=\"peak\"]("
                + south + "," + west + "," + north + "," + east + ");out;";

        RequestBody body = RequestBody.create(
                query,
                MediaType.parse("text/plain")
        );

        Request request = new Request.Builder()
                .url(OVERPASS_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    callback.onError(new Exception("HTTP error"));
                    return;
                }

                String json = response.body().string();
                List<Peak> peaks = parsePeaks(json);

                callback.onSuccess(peaks);
            }
        });
    }

    private List<Peak> parsePeaks(String json) {

        List<Peak> peaks = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray elements = root.getJSONArray("elements");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject obj = elements.getJSONObject(i);

                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");

                JSONObject tags = obj.optJSONObject("tags");

                if (tags == null) continue;

                String name = tags.optString("name", "Unknown peak");
                String eleStr = tags.optString("ele", "0");

                int elevation = 0;
                try {
                    eleStr = eleStr.toLowerCase().replace("m", "").trim();
                    eleStr = eleStr.replace(",", ".");
                    double eleDouble = Double.parseDouble(eleStr);
                    elevation = (int) eleDouble;
                } catch (Exception ignored) {}

                if (name.equalsIgnoreCase("Unknown peak") || elevation == 0) {
                    continue;
                }

                peaks.add(new Peak(
                        name,
                        lat,
                        lon,
                        elevation
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return peaks;
    }
}



