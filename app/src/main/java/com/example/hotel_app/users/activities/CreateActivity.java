package com.example.hotel_app.users.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_app.R;
import com.example.hotel_app.User;
import com.example.hotel_app.services.APIService;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateActivity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private EditText email;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(view -> {
            if (!name.getText().toString().isEmpty() && !Objects.requireNonNull(password.getText()).toString().isEmpty() && !Objects.requireNonNull(email.getText()).toString().isEmpty()) {
                APIService.APIService.registerUser(new User(
                        name.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString()
                ), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show());
                        System.out.println(e.getMessage());
                        call.cancel();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        call.cancel();
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "User has been created", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}