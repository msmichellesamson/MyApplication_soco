package com.example.myapplication_soco;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.Objects;

public class check_selfie_page extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private ImageView imageView;
    private String imagePath;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_selfie);

        imageView = findViewById(R.id.imageView);
        Button retakeButton = findViewById(R.id.retake_button);
        Button saveButton = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }

        retakeButton.setOnClickListener(v -> {
            Intent intent = new Intent(check_selfie_page.this, selfie_page.class);
            startActivity(intent);
            finish();
        });

        saveButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                uploadSelfieToFirebase(uid, imagePath);
            } else {
                requestPermissions();
            }
        });
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                uploadSelfieToFirebase(uid, imagePath);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadSelfieToFirebase(String userId, String imagePath) {
        Uri file = Uri.fromFile(new File(imagePath));
        StorageReference storageRef = storage.getReference();
        StorageReference selfiesRef = storageRef.child("users/" + userId + "/" + file.getLastPathSegment());

        UploadTask uploadTask = selfiesRef.putFile(file);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            selfiesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                saveSelfieUrlToFirestore(downloadUrl);
            }).addOnFailureListener(e -> {
                Toast.makeText(check_selfie_page.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(check_selfie_page.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveSelfieUrlToFirestore(String downloadUrl) {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.collection("profiles").document(uid)
                .update("profilePicUrl", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(check_selfie_page.this, "Selfie saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(check_selfie_page.this, home_page.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(check_selfie_page.this, "Failed to save selfie URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
