package de.dennis.wasndasmeins.api;


import static okhttp3.internal.Util.EMPTY_REQUEST;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.dennis.wasndasmeins.MainActivity;
import de.dennis.wasndasmeins.model.Image;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String baseUrl = "http://192.168.178.152:3000";

    public static void sendTokenToAPI(String token) {

    }

    public static void sendAnswer(MainActivity context, boolean ok, String filename) {
        String url;
        if (ok) {
            url = baseUrl + "/update-done/" + filename;
        } else {
            url = baseUrl + "/nope/" + filename;
        }
        sendPost(context, url);
    }

    public static void getLatestImageUrl(MainActivity context) {
        String endpoint = baseUrl + "/latest";
        sendGet(context, endpoint);
    }

    private static void sendGet(MainActivity context, String url) {
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                context.showToast("Kein Bild gefunden");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String imageUrl = json.getString("url");
                        boolean done = json.getBoolean("done");

                        Image image = new Image();
                        image.setDone(done);
                        image.setImageUrl(imageUrl);

                        context.showImage(image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        context.showToast("Fehler beim Parsen vom Bild gefunden");
                    }
                }
            }
        });
    }

    private static void sendPost(MainActivity context, String url) {
        sendPost(context, url, null);
    }

    private static void sendPost(MainActivity context, String url, String json) {
        // MediaType für den JSON-Body
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        RequestBody body;
        if (json != null) {
            body = RequestBody.create(json, JSON);
        } else {
            body = EMPTY_REQUEST;
        }
        requestBuilder.post(body);

        Request request = requestBuilder.build();

        // Führe den Request asynchron aus
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Erfolgreiche Antwort
                    final String responseData = response.message();

                    context.showToast("Erfolg: " + responseData);
                } else {
                    // Fehler bei der Antwort
                    context.showToast("Fehler beim POST-Request");
                }
            }
        });
    }
}
