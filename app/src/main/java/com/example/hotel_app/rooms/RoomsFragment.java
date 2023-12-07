package com.example.hotel_app.rooms;

import static com.example.hotel_app.services.APIService.APIService;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hotel_app.R;
import com.example.hotel_app.rooms.activities.CreateRoomActivity;
import com.example.hotel_app.rooms.models.Room;
import com.example.hotel_app.rooms.models.ViewModelRooms;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private Button createRoom;
    private SwipeRefreshLayout refreshLayout;
    static public ViewModelRooms viewModelRooms;
    public RoomsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoomsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomsFragment newInstance(String param1, String param2) {
        RoomsFragment fragment = new RoomsFragment();
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
        view = inflater.inflate(R.layout.fragment_rooms, container, false);

        refreshLayout = view.findViewById(R.id.swipeRefresh);
        createRoom = view.findViewById(R.id.createRoom);

        viewModelRooms = new ViewModelProvider(this).get(ViewModelRooms.class);
        AllRooms allRooms = new AllRooms(viewModelRooms);
        getParentFragmentManager().beginTransaction().replace(R.id.roomsContainer, allRooms).commit();

        createRooms();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setOnRefreshListener(
                () -> {
                    viewModelRooms.clear();
                    createRooms();
                }
        );

        createRoom.setOnClickListener(v -> {
            Intent i;
            i = new Intent(view.getContext(), CreateRoomActivity.class);
            startActivity(i);
        });
    }
    private void createRooms() {
        refreshLayout.setRefreshing(true);
        APIService.getRooms(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ArrayList<Room> rooms = new ArrayList<>();

                String responseBody = response.body().string();
                JsonArray array = new Gson().fromJson(responseBody, JsonArray.class);
                for (JsonElement object : array.getAsJsonArray()) {
                    Room room = new Room(
                            object.getAsJsonObject().get("room_number").getAsInt(),
                            object.getAsJsonObject().get("kind").getAsString(),
                            object.getAsJsonObject().get("number_of_beds").getAsInt(),
                            object.getAsJsonObject().get("price_per_night").getAsInt()
                    );
                    rooms.add(room);
                }
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> getNeededRooms(rooms));
                }
                call.cancel();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                call.cancel();
            }
        });
    }
    public void getNeededRooms(ArrayList<Room> rooms) {
        for (Room room : rooms) {
            viewModelRooms.addRoom(room);
        }
        refreshLayout.setRefreshing(false);
    }
}