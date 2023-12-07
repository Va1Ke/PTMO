package com.example.hotel_app.users;

import static com.example.hotel_app.users.UsersFragment.viewModelUsers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_app.R;
import com.example.hotel_app.services.APIService;
import com.example.hotel_app.users.models.UserDB;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyUsersAdapter extends RecyclerView.Adapter<MyUsersAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<UserDB> users;
    private View view;

    public MyUsersAdapter() {
    }

    public MyUsersAdapter(Context context, ArrayList<UserDB> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.list_user_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserDB user = users.get(position);
        holder.user.setText("" + user.getName());

        holder.delete.setOnClickListener(v -> {
            APIService.APIService.deleteUserByEmail(user.getEmail(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    viewModelUsers.deleteUser(user);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        Button delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user);
            delete = itemView.findViewById(R.id.deleteUser);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String task);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
