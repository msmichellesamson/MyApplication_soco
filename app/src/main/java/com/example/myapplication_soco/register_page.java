package com.example.myapplication_soco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class register_page extends AppCompatActivity {
    Button registerButton;
    Button loginButton;
    TextView first_name, last_name, phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        registerButton = findViewById(R.id.button);
        loginButton = findViewById(R.id.button2);
        first_name = findViewById(R.id.textInputEditText2);
        last_name = findViewById(R.id.textInputEditText3);
        phonenumber = findViewById(R.id.textInputEditText4);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity when the register button is clicked
                Intent intent = new Intent(register_page.this, verify_page.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity when the register button is clicked
                Intent intent = new Intent(register_page.this, signin_page.class);
                startActivity(intent);
            }
        });

    }
}



