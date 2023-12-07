package com.example.hotel_app.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.hotel_app.User;
import com.example.hotel_app.rooms.models.Room;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("CustomX509TrustManager")
public class APIService {
    private static final String SERVER_DOMAIN = "http://ec2-3-17-163-215.us-east-2.compute.amazonaws.com:8000/";
    private static final String PREF_NAME = "MyPrefs";

    public static APIService APIService;

    private final Context context;

    private APIService(Context context) {
        this.context = context.getApplicationContext();
    }

    private Call currentCall;

    private OkHttpClient initClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    public static synchronized APIService getInstance(Context context) {
        if (APIService == null) {
            APIService = new APIService(context);
        }
        return APIService;
    }

    public void getAllUsers(final Callback callback) {
        cancelNetworkRequest();
        makeNetworkRequest(SERVER_DOMAIN + "users/", callback);
    }

    public void getRooms(final Callback callback) {
        cancelNetworkRequest();
        makeNetworkRequest(SERVER_DOMAIN + "available/room/", callback);
    }


    private void makeNetworkRequest(String url, final Callback callback) {
        OkHttpClient client = initClient();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();
        currentCall = client.newCall(request);
        currentCall.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    assert response.body() != null;
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    private void cancelNetworkRequest() {
        if (currentCall != null) {
            currentCall.cancel();
            currentCall = null;
        }
    }

    public void login(String username, String password, final Callback callback) {
        OkHttpClient client = initClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(mediaType, "");

        Request request = new Request.Builder()
                .url(SERVER_DOMAIN + "login/?email=" + username + "&password=" + password)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    String token = parseTokenFromJson(responseBody);
                    setToken(token);
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void deleteUserByEmail(String email, final Callback callback) {
        OkHttpClient client = initClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "user/delete/?email=" + email)
                .delete();

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void deleteRoom(int roomNumber, final Callback callback) {
        OkHttpClient client = initClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "room/delete/?room_number=" + roomNumber)
                .delete();

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void logout() {
        setToken("");
    }

    public boolean verifyToken(final Callback callback) {
        OkHttpClient client = initClient();

        String access = getToken();
        if (access != null && !access.isEmpty()) {
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("token", getToken());
            Gson gson = new Gson();
            String json = gson.toJson(jsonRequest);
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(mediaType, json);
            Request.Builder requestBuilder = new Request.Builder()
                    .url(SERVER_DOMAIN + "verify-token/")
                    .post(requestBody);

            for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }

            Request request = requestBuilder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        callback.onResponse(call, response);
                    } else {
                        callback.onFailure(call, new IOException(response.message()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(call, e);
                }
            });
        } else {
            return false;
        }
        return true;
    }

    private String parseTokenFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getToken());
        headers.put("accept", "application/json");
        return headers;
    }

    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString("access", "");
    }

    private void setToken(String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("access", token);
        editor.apply();
    }

    public void registerUser(User data, final Callback callback) {
        OkHttpClient client = initClient();
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.addProperty("name", data.getName());
        jsonRequest.addProperty("password", data.getPassword());
        jsonRequest.addProperty("email", data.getEmail());

        Gson gson = new Gson();
        String json = gson.toJson(jsonRequest);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "user/register/")
                .post(requestBody);
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void createRoom(Room data, final Callback callback) {
        OkHttpClient client = initClient();
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.addProperty("room_number", data.getRoomNumber());
        jsonRequest.addProperty("kind", data.getKind());
        jsonRequest.addProperty("number_of_beds", data.getNumberOfBeds());
        jsonRequest.addProperty("price_per_night", data.getPricePerNight());

        Gson gson = new Gson();
        String json = gson.toJson(jsonRequest);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "room/add/")
                .post(requestBody);
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void updateRoom(Room data, final Callback callback) {
        OkHttpClient client = initClient();
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.addProperty("room_number", data.getRoomNumber());
        jsonRequest.addProperty("kind", data.getKind());
        jsonRequest.addProperty("number_of_beds", data.getNumberOfBeds());
        jsonRequest.addProperty("price_per_night", data.getPricePerNight());

        Gson gson = new Gson();
        String json = gson.toJson(jsonRequest);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "room/update/")
                .put(requestBody);
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void postPhoto(String kind, String photo, final Callback callback) {
        OkHttpClient client = initClient();
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.addProperty("kind", kind);
        jsonRequest.addProperty("photo", photo);

        Gson gson = new Gson();
        String json = gson.toJson(jsonRequest);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "photo/")
                .post(requestBody);
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void updatePhoto(String kind, String photo, final Callback callback) {
        OkHttpClient client = initClient();
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.addProperty("kind", kind);
        jsonRequest.addProperty("photo", photo);

        Gson gson = new Gson();
        String json = gson.toJson(jsonRequest);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "photo/")
                .put(requestBody);
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void getPhoto(int roomNumber, final Callback callback) {
        OkHttpClient client = initClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "photo/?room_number=" + roomNumber)
                .get();

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void getRoomByUserEmail(String email, final Callback callback) {
        OkHttpClient client = initClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "room-by-user/?user_email=" + email)
                .get();

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void getUserByUserEmail(String email, final Callback callback) {
        OkHttpClient client = initClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "user-by-email/?email=" + email)
                .get();

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void appointToRoom(String email, int roomNumber, final Callback callback) {
        OkHttpClient client = initClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, "");

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "room/rent/?user_email=" + email + "&" + "room_number=" + roomNumber)
                .post(requestBody);

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    public void leaveRoom(String email, final Callback callback) {
        OkHttpClient client = initClient();

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, "");

        Request.Builder requestBuilder = new Request.Builder()
                .url(SERVER_DOMAIN + "room/leave/?user_email=" + email)
                .post(requestBody);

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }
        });
    }
}