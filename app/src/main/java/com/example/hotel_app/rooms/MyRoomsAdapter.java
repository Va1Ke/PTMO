package com.example.hotel_app.rooms;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.hotel_app.rooms.RoomsFragment.viewModelRooms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_app.R;
import com.example.hotel_app.rooms.activities.UpdateRoomActivity;
import com.example.hotel_app.rooms.models.Room;
import com.example.hotel_app.services.APIService;
import com.example.hotel_app.users.MyUsersAdapter;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyRoomsAdapter extends RecyclerView.Adapter<MyRoomsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Room> rooms;
    private View view;

    public MyRoomsAdapter() {
    }

    public MyRoomsAdapter(Context context, ArrayList<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.list_room_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.room.setText("" + room.getKind() + "|" + room.getRoomNumber() + "|" + room.getNumberOfBeds() + "|" + room.getPricePerNight());

        holder.delete.setOnClickListener(v -> {
            APIService.APIService.deleteRoom(room.getRoomNumber(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    viewModelRooms.deleteRoom(room);
                }
            });
        });

        holder.update.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext().getApplicationContext(), UpdateRoomActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("room", room);
            intent.putExtra("bundle", bundle);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView room;
        Button delete;
        Button update;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.room);
            delete = itemView.findViewById(R.id.deleteRoom);
            update = itemView.findViewById(R.id.updateRoom);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String task);
    }

    private MyUsersAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(MyUsersAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
}
