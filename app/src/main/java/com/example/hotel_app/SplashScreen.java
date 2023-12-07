package com.example.hotel_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.hotel_app.services.APIService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private TextView textView;
    private int dotsCount = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //For day mode theme

        setContentView(R.layout.activity_splash_screen);

        textView = findViewById(R.id.splash_screen_text);
        startTextAnimation();
        APIService apiService = APIService.getInstance(getApplicationContext());
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            boolean result = apiService.verifyToken(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Intent i;
                        i = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(i);
                        call.cancel();
                        finish();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        Intent i;
                        i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        call.cancel();
                        finish();
                    });
                }
            });
            if (!result){
                Intent i;
                i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No internet connection.")
                        .setTitle("Error")
                        .setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                Intent i;
                i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }
    }


    private void startTextAnimation() {
        int interval = 500;

        Handler handler = new Handler();
        Runnable textAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                updateTextWithDots(dotsCount);

                // Increment dotsCount, reset if it reaches 3 (you can adjust the number of dots here)
                dotsCount = (dotsCount + 1) % 4;

                // Schedule the next text update after the interval
                handler.postDelayed(this, interval);
            }
        };
        handler.post(textAnimationRunnable);
    }

    private void updateTextWithDots(int dotsCount) {
        StringBuilder text = new StringBuilder("Loading");
        for (int i = 0; i < dotsCount; i++) {
            text.append(".");
        }
        textView.setText(text.toString());
    }
}