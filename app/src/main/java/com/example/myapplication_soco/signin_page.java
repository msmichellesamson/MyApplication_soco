package com.example.myapplication_soco;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.concurrent.TimeUnit;
public class signin_page extends AppCompatActivity {
    EditText phoneNumberInput;
    Button loginButton, registerButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin_page);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        phoneNumberInput = findViewById(R.id.editTextText);
        loginButton = findViewById(R.id.button3);
        registerButton = findViewById(R.id.button4);
        loginButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(signin_page.this, "Phone number is required.", Toast.LENGTH_LONG).show();
            } else {
                checkPhoneNumberRegistered(phoneNumber);
            }
        });
        registerButton.setOnClickListener(v -> startActivity(new Intent(signin_page.this, register_page.class)));
    }
    private void checkPhoneNumberRegistered(String phoneNumber) {
        db.collection("profiles")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        startPhoneNumberVerification(phoneNumber);
                    } else if (task.getResult().isEmpty()) {
                        Toast.makeText(signin_page.this, "Phone number not registered. Please register first.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(signin_page.this, "Failed to check phone number: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(signin_page.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                Intent intent = new Intent(signin_page.this, verify_page.class);
                intent.putExtra("verificationId", mVerificationId);
                intent.putExtra("resendToken", mResendToken);
                startActivity(intent);
            }
        };
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        startActivity(new Intent(signin_page.this, selfie_page.class));
                    } else {
                        Toast.makeText(signin_page.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}