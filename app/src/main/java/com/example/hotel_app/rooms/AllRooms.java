package com.example.hotel_app.rooms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_app.R;
import com.example.hotel_app.rooms.models.ViewModelRooms;
import com.example.hotel_app.users.MyUsersAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllRooms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllRooms extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewModelRooms viewModelRooms;
    private RecyclerView recyclerView;
    private MyRoomsAdapter myRoomsAdapter;

    public AllRooms() {
        // Required empty public constructor
    }
    public AllRooms(ViewModelRooms viewModelRooms) {
        this.viewModelRooms = viewModelRooms;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllRooms.
     */
    // TODO: Rename and change types and number of parameters
    public static AllRooms newInstance(String param1, String param2) {
        AllRooms fragment = new AllRooms();
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
        return inflater.inflate(R.layout.fragment_all_rooms, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        try {
            viewModelRooms.getAllRooms().observe(getViewLifecycleOwner(), roomItems -> {
                myRoomsAdapter = new MyRoomsAdapter(getContext(), roomItems);
                recyclerView.setAdapter(myRoomsAdapter);
                myRoomsAdapter.notifyDataSetChanged();
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}