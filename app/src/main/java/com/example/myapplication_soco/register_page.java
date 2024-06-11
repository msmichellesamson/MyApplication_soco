package com.example.myapplication_soco;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
public class register_page extends AppCompatActivity {
    EditText firstNameInput, lastNameInput, phoneNumberInput;
    Button loginButton, registerButton;
    FirebaseAuth mAuth;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        firstNameInput = findViewById(R.id.textInputEditText2);
        lastNameInput = findViewById(R.id.textInputEditText3);
        phoneNumberInput = findViewById(R.id.textInputEditText4);
        loginButton = findViewById(R.id.button2);
        registerButton = findViewById(R.id.button);

        // Define what happens when the "Register" button is clicked
        registerButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String phoneNumber = phoneNumberInput.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(register_page.this, "All fields are required.", Toast.LENGTH_LONG).show();
            } else {
                startPhoneNumberVerification(phoneNumber);
            }
        });

        loginButton.setOnClickListener(v -> startActivity(new Intent(register_page.this, signin_page.class)));
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This method is triggered in some cases without the need for manual code entry
                Log.d("TAG", "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w("TAG", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(register_page.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(register_page.this, "Too many attempts.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                Log.d("TAG", "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
                Intent intent = new Intent(register_page.this, verify_page.class);
                intent.putExtra("verificationId", mVerificationId);
                intent.putExtra("resendToken", mResendToken);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);

                startActivity(intent);
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity for callback binding
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}



