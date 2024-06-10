package com.example.myapplication_soco;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;

public class startup_page extends AppCompatActivity {
    Button registerButton;
    Button loginButton;

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        FirebaseApp.initializeApp(this);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);
        // Initialize the register button
        registerButton = findViewById(R.id.button_1);
        loginButton = findViewById(R.id.button_2);
        // Set OnClickListener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity when the register button is clicked
                Intent intent = new Intent(startup_page.this, register_page.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity when the register button is clicked
                Intent intent = new Intent(startup_page.this, signin_page.class);
                startActivity(intent);
            }
        });
    }
}



