package com.example.hotel_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_app.services.APIService;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText login;
    private TextInputEditText password;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        APIService apiService = APIService.getInstance(getApplicationContext());
        submit.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                if (!login.getText().toString().isEmpty() && !Objects.requireNonNull(password.getText()).toString().isEmpty()) {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Handler().postDelayed(progressDialog::dismiss, 2000);

                    apiService.login(login.getText().toString(), Objects.requireNonNull(password.getText()).toString(), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            if (Objects.equals(e.getMessage(), "Bad Request")) {
                                runOnUiThread(() -> {
                                    progressDialog.dismiss();
                                    showError("Incorrect login or password");
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Some error", Toast.LENGTH_SHORT).show());
                            }
                            call.cancel();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            runOnUiThread(() -> {
                                Intent intent;
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                            progressDialog.dismiss();
                            call.cancel();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!NetworkUtils.isNetworkAvailable(this)) {
                    showError("No internet connection.");
                }
            }
        });
    }

    private void showError(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Error")
                .setPositiveButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}