package com.example.hotel_app.appointment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotel_app.R;
import com.example.hotel_app.User;
import com.example.hotel_app.rooms.models.Room;
import com.example.hotel_app.services.APIService;
import com.example.hotel_app.users.models.UserDB;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchByUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchByUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private View view;
    private String mParam1;
    private String mParam2;
    private EditText search;
    private Button button;
    private TextView room;
    private TextView user;
    private Button buttonLeave;
    private Spinner spinner;
    private StringBuilder selectedRoom;

    public SearchByUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchByUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchByUserFragment newInstance(String param1, String param2) {
        SearchByUserFragment fragment = new SearchByUserFragment();
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
        view = inflater.inflate(R.layout.fragment_search_by_user, container, false);

        search = view.findViewById(R.id.search);
        button = view.findViewById(R.id.button);
        room = view.findViewById(R.id.roomText);
        user = view.findViewById(R.id.userText);
        buttonLeave = view.findViewById(R.id.buttonLeave);

        final UserDB[] userDB = {null};
        final Room[] roomDB = {null};

        spinner = view.findViewById(R.id.select_response_spinner);

        selectedRoom = new StringBuilder();
        selectedRoom.append("Nothing");

        APIService.APIService.getRooms(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                JsonArray array = new Gson().fromJson(responseBody, JsonArray.class);
                ArrayList<String> rooms = new ArrayList<>();
                for (JsonElement object : array.getAsJsonArray()) {
                    Room roomFromDB = new Room(
                            object.getAsJsonObject().get("room_number").getAsInt(),
                            object.getAsJsonObject().get("kind").getAsString(),
                            object.getAsJsonObject().get("number_of_beds").getAsInt(),
                            object.getAsJsonObject().get("price_per_night").getAsInt()
                    );
                    rooms.add(roomFromDB.getRoomNumber() + "|" + roomFromDB.getKind() + "|" + roomFromDB.getNumberOfBeds() + "|" + roomFromDB.getPricePerNight());
                }
                call.cancel();
                requireActivity().runOnUiThread(() -> {
                    spinnerSettings(rooms);
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }
        });


        button.setOnClickListener(v -> {
            if (!search.getText().toString().isEmpty()) {
                APIService.APIService.getUserByUserEmail(search.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        call.cancel();
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(view.getContext().getApplicationContext(), "Not found", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        call.cancel();
                        String responseBody = response.body().string();
                        if (!responseBody.equals("null")) {
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

                            APIService.APIService.getRoomByUserEmail(userDB[0].getEmail(), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    call.cancel();
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(view.getContext().getApplicationContext(), "Room was not found", Toast.LENGTH_SHORT).show();
                                        spinner.setVisibility(View.VISIBLE);
                                        buttonLeave.setText("Appoint");
                                    });
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    call.cancel();
                                    String responseBody = response.body().string();
                                    if (!responseBody.equals("null")) {
                                        JsonObject object = new Gson().fromJson(responseBody, JsonObject.class);
                                        roomDB[0] = new Room(
                                                object.getAsJsonObject().get("room_number").getAsInt(),
                                                object.getAsJsonObject().get("kind").getAsString(),
                                                object.getAsJsonObject().get("number_of_beds").getAsInt(),
                                                object.getAsJsonObject().get("price_per_night").getAsInt()
                                        );
                                        requireActivity().runOnUiThread(() -> {
                                            spinner.setVisibility(View.INVISIBLE);
                                            room.setText("" + roomDB[0].getRoomNumber() + "|" + roomDB[0].getKind() + "|" + roomDB[0].getNumberOfBeds() + "|" + roomDB[0].getPricePerNight());
                                            buttonLeave.setText("Leave");
                                        });
                                    } else {
                                        requireActivity().runOnUiThread(() -> {
                                            Toast.makeText(view.getContext().getApplicationContext(), "Room was not found", Toast.LENGTH_SHORT).show();
                                            spinner.setVisibility(View.VISIBLE);
                                            buttonLeave.setText("Appoint");
                                        });
                                    }
                                }
                            });
                        }
                        else {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(view.getContext().getApplicationContext(), "Not found", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            }
        });

        buttonLeave.setOnClickListener(v -> {
            if (buttonLeave.getText().toString().equals("Leave")) {
                APIService.APIService.leaveRoom(userDB[0].getEmail(), new Callback() {
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
                });
            } else {
                if (!selectedRoom.toString().equals("Nothing")) {
                    APIService.APIService.appointToRoom(userDB[0].getEmail(), Integer.parseInt(selectedRoom.toString()), new Callback() {
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
                                Toast.makeText(view.getContext().getApplicationContext(), "Appoint was successful", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        });

        return view;
    }

    private void spinnerSettings(ArrayList<String> rooms) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext().getApplicationContext(),
                R.layout.spinner_item,
                rooms);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom.setLength(0);
                selectedRoom.append(rooms.get(position).split("\\|")[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoom.setLength(0);
                selectedRoom.append("Nothing");
            }
        });
    }
}