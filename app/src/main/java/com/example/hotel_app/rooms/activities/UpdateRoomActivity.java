package com.example.hotel_app.rooms.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hotel_app.R;
import com.example.hotel_app.rooms.models.Room;
import com.example.hotel_app.services.APIService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateRoomActivity extends AppCompatActivity {

    private EditText number;
    private EditText beds;
    private EditText price;
    private Spinner spinner;
    private Button button;
    private Button photo;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_room);
        Intent intent = getIntent();
        Room room = (Room) Objects.requireNonNull(intent.getBundleExtra("bundle")).get("room");

        number = findViewById(R.id.number);
        beds = findViewById(R.id.beds);
        price = findViewById(R.id.price);
        spinner = findViewById(R.id.select_response_spinner);
        button = findViewById(R.id.submit);
        photo = findViewById(R.id.photo);
        String bufKind = "";
        StringBuilder base64Image = new StringBuilder();

        if (room != null) {
            number.setText("" + room.getRoomNumber());
            beds.setText("" + room.getNumberOfBeds());
            price.setText("" + room.getPricePerNight());
            bufKind = room.getKind();
        }

        String[] kinds = {"Standart", "Business", "President"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                kinds);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(arrayAdapter);
        StringBuilder kind = new StringBuilder();
        kind.append("Nothing");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kind.setLength(0);
                kind.append(kinds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                kind.setLength(0);
                kind.append("Nothing");
            }
        });

        button.setOnClickListener(v -> {
            if (!number.getText().toString().isEmpty() && !beds.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !kind.toString().equals("Nothing")) {
                APIService.APIService.updateRoom(new Room(Integer.parseInt(number.getText().toString()), kind.toString(), Integer.parseInt(beds.getText().toString()), Integer.parseInt(price.getText().toString())), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                        call.cancel();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        call.cancel();

                        if (!base64Image.toString().isEmpty()) {
                                APIService.APIService.updatePhoto(kind.toString(), base64Image.toString(), new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        call.cancel();
                                        System.out.println(e.getMessage());
                                        APIService.APIService.postPhoto(kind.toString(), base64Image.toString(), new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                                                call.cancel();
                                                System.out.println(e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                call.cancel();
                                                runOnUiThread(() -> {
                                                    Toast.makeText(getApplicationContext(), "Room has been updated", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        call.cancel();
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Room has been updated", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                                    }
                                });

                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Room has been updated", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    }
                });
            }
        });

        photo.setOnClickListener(v -> {
            Intent intentOpen = new Intent(getApplicationContext(), LoadImgActivity.class);
            someActivityResultLauncher.launch(intentOpen);
        });


        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (Objects.equals(data.getStringExtra("intent"), "photos")) {
                                Uri savedUri = Uri.parse(data.getStringExtra("photoUri"));
                                try (InputStream inputStream = getContentResolver().openInputStream(savedUri);) {
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    if (bitmap != null) {
                                        base64Image.append(encodeBitmapToBase64(bitmap));
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });
    }

    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}