package com.example.hotel_app.users;

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
import com.example.hotel_app.users.activities.CreateActivity;
import com.example.hotel_app.users.models.UserDB;
import com.example.hotel_app.users.models.ViewModelUsers;
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
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout refreshLayout;
    static public ViewModelUsers viewModelUsers;
    private View view;
    private Button createUser;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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
        view = inflater.inflate(R.layout.fragment_users, container, false);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        createUser = view.findViewById(R.id.createUser);

        viewModelUsers = new ViewModelProvider(this).get(ViewModelUsers.class);
        AllUsers allUsers = new AllUsers(viewModelUsers);
        getParentFragmentManager().beginTransaction().replace(R.id.usersContainer, allUsers).commit();

        createUsers();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setOnRefreshListener(
                () -> {
                    viewModelUsers.clear();
                    createUsers();
                }
        );

        createUser.setOnClickListener(v -> {
            Intent i;
            i = new Intent(view.getContext(), CreateActivity.class);
            startActivity(i);
        });
    }

    private void createUsers() {
        refreshLayout.setRefreshing(true);
        APIService.getAllUsers(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ArrayList<UserDB> userDBS = new ArrayList<>();

                String responseBody = response.body().string();
                JsonArray array = new Gson().fromJson(responseBody, JsonArray.class);
                for (JsonElement object : array.getAsJsonArray()) {
                    UserDB user = new UserDB(
                            object.getAsJsonObject().get("user_id").getAsInt(),
                            object.getAsJsonObject().get("name").getAsString(),
                            object.getAsJsonObject().get("password").getAsString(),
                            object.getAsJsonObject().get("email").getAsString(),
                            object.getAsJsonObject().get("admin").getAsBoolean()
                    );
                    userDBS.add(user);
                }
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> getNeededUsers(userDBS));
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

    public void getNeededUsers(ArrayList<UserDB> users) {
        for (UserDB user : users) {
            viewModelUsers.addUser(user);
        }
        refreshLayout.setRefreshing(false);
    }
}