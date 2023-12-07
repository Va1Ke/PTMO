package com.example.hotel_app.rooms.activities;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_app.R;
import com.example.hotel_app.rooms.models.PhotoItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoadImgActivity extends AppCompatActivity {
    private ImageView addPhoto;
    private ImageView closePage;
    private ImageView photosContainer;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_img);
        addPhoto = findViewById(R.id.addPhoto);
        closePage = findViewById(R.id.closePage);
        photosContainer = findViewById(R.id.photosContainer);
        PhotoItem photoItem = new PhotoItem();

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri selectedUri = clipData.getItemAt(i).getUri();
                                    String mimeType = getContentResolver().getType(selectedUri);
                                    if (mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"))) {
                                        String base64File = encodeToBase64(selectedUri);
                                        photoItem.setPath(selectedUri.toString());
                                        photoItem.setPhoto(base64File);
                                        photosContainer.setImageURI(selectedUri);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Incorrect media type", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Uri selectedUri = data.getData();
                                assert selectedUri != null;
                                String mimeType = getContentResolver().getType(selectedUri);
                                if (mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"))) {
                                    String base64File = encodeToBase64(selectedUri);
                                    photoItem.setPath(selectedUri.toString());
                                    photoItem.setPhoto(base64File);
                                    photosContainer.setImageURI(selectedUri);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Incorrect media type", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );

        addPhoto.setOnClickListener(v -> openGallery());
        closePage.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("intent", "photos");
            if (photoItem.getPath() != null) {
                resultIntent.putExtra("photoUri", photoItem.getPath());
                setResult(RESULT_OK, resultIntent);
            } else {
                setResult(RESULT_CANCELED, resultIntent);
            }
            finish();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        someActivityResultLauncher.launch(intent);
    }

    private String encodeToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}