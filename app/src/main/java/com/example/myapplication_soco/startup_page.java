package com.example.myapplication_soco;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class startup_page extends AppCompatActivity {
    private static final String TAG = "StartupPage";
    Button registerButton;
    Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);
        Log.i(TAG, "App launched at " + getCurrentTimestamp());
        registerButton = findViewById(R.id.registerButton);
        signInButton = findViewById(R.id.signInButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startup_page.this, register_page.class);
                startActivity(intent);
                Log.i(TAG, "Register button clicked at " + getCurrentTimestamp());
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startup_page.this, signin_page.class);
                startActivity(intent);
                Log.i(TAG, "Sign in button clicked at " + getCurrentTimestamp());
            }
        });
    }
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}



