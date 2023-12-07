package com.example.hotel_app.users;

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
import com.example.hotel_app.users.models.ViewModelUsers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllUsers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ViewModelUsers viewModelUsers;
    private MyUsersAdapter myUsersAdapter;

    public AllUsers() {
        // Required empty public constructor
    }

    public AllUsers(ViewModelUsers viewModelUsers) {
        // Required empty public constructor
        this.viewModelUsers = viewModelUsers;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static AllUsers newInstance(String param1, String param2) {
        AllUsers fragment = new AllUsers();
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
        return inflater.inflate(R.layout.fragment_all_users, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        try {
            viewModelUsers.getAllUsers().observe(getViewLifecycleOwner(), userItems -> {
                myUsersAdapter = new MyUsersAdapter(getContext(), userItems);
                recyclerView.setAdapter(myUsersAdapter);
                myUsersAdapter.notifyDataSetChanged();
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}