package com.example.hotel_app;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_app.appointment.AppointmentFragment;
import com.example.hotel_app.rooms.RoomsFragment;
import com.example.hotel_app.users.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        UsersFragment usersFragment = new UsersFragment();
        RoomsFragment roomsFragment = new RoomsFragment();
        AppointmentFragment appointmentFragment = new AppointmentFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, usersFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.user) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, usersFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, roomsFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.swap) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, appointmentFragment).commit();
                return true;
            }
            return false;
        });
    }
}