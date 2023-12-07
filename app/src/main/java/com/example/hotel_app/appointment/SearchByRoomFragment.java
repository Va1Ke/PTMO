package com.example.hotel_app.appointment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotel_app.R;
import com.example.hotel_app.rooms.models.Room;
import com.example.hotel_app.services.APIService;
import com.example.hotel_app.users.models.UserDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchByRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchByRoomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText search;
    private Button button;
    private TextView room;
    private TextView user;
    private Button buttonLeave;
    private ImageView image;

    public SearchByRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SerchByRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchByRoomFragment newInstance(String param1, String param2) {
        SearchByRoomFragment fragment = new SearchByRoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serch_by_room, container, false);

        search = view.findViewById(R.id.search);
        button = view.findViewById(R.id.button);
        room = view.findViewById(R.id.roomText);
        user = view.findViewById(R.id.userText);
        buttonLeave = view.findViewById(R.id.buttonLeave);
        image = view.findViewById(R.id.image);
        final UserDB[] userDB = {null};
        final Room[] roomDB = {null};

        button.setOnClickListener(v -> {
            if (!search.getText().toString().isEmpty()) {
                APIService.APIService.getRoomByUserEmail(search.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        call.cancel();
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(view.getContext().getApplicationContext(), "Room was not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        call.cancel();
                        String responseBody = response.body().string();

                        if (!responseBody.equals("null")){
                            JsonObject object = new Gson().fromJson(responseBody, JsonObject.class);
                            roomDB[0] = new Room(
                                    object.getAsJsonObject().get("room_number").getAsInt(),
                                    object.getAsJsonObject().get("kind").getAsString(),
                                    object.getAsJsonObject().get("number_of_beds").getAsInt(),
                                    object.getAsJsonObject().get("price_per_night").getAsInt()
                            );
                            requireActivity().runOnUiThread(() -> {
                                room.setText("" + roomDB[0].getRoomNumber() + "|" + roomDB[0].getKind() + "|" + roomDB[0].getNumberOfBeds() + "|" + roomDB[0].getPricePerNight());
                            });
                            APIService.APIService.getUserByUserEmail(search.getText().toString(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    call.cancel();
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(view.getContext().getApplicationContext(), "Getting user " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    call.cancel();
                                    String responseBody = response.body().string();
                                    JsonObject object = new Gson().fromJson(responseBody, JsonObject.class);
                                    userDB[0] = new UserDB(
                                            object.getAsJsonObject().get("user_id").getAsInt(),
                                            object.getAsJsonObject().get("name").getAsString(),
                                            object.getAsJsonObject().get("password").getAsString(),
                                            object.getAsJsonObject().get("email").getAsString(),
                                            object.getAsJsonObject().get("admin").getAsBoolean()
                                    );
                                    requireActivity().runOnUiThread(() -> {
                                        user.setText("" + userDB[0].getEmail() + "|" + userDB[0].getName());
                                    });
                                }
                            });

                            APIService.APIService.getPhoto(roomDB[0].getRoomNumber(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    call.cancel();
                                    System.out.println(e.getMessage());
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(view.getContext().getApplicationContext(), "Getting photo " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    String responseBody = response.body().string();
                                    if (!responseBody.equals("null")){
                                        JsonObject object = new Gson().fromJson(responseBody, JsonObject.class);
                                        if (!object.getAsJsonObject().isJsonNull()){
                                            requireActivity().runOnUiThread(()->{
                                                String photo = object.getAsJsonObject().get("photo").getAsString();
                                                byte[] decodedBytes = Base64.decode(photo, Base64.DEFAULT);
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                                image.setImageBitmap(bitmap);
                                            });

                                        }
                                    } else {
                                        requireActivity().runOnUiThread(() -> {
                                            Toast.makeText(view.getContext().getApplicationContext(), "Getting photo error", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    call.cancel();
                                }
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(view.getContext().getApplicationContext(), "Room was not found", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            }
        });

        buttonLeave.setOnClickListener(v -> APIService.APIService.leaveRoom(userDB[0].getEmail(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(view.getContext().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                call.cancel();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(view.getContext().getApplicationContext(), "Room has been left", Toast.LENGTH_SHORT).show();
                });
            }
        }));

        return view;
    }
}