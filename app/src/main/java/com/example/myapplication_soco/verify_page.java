package com.example.myapplication_soco;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
public class verify_page extends AppCompatActivity {

    EditText otpInput;
    Button confirmButton;
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_page);

        mAuth = FirebaseAuth.getInstance();

        otpInput = findViewById(R.id.editTextText2); // Assume you have an EditText for OTP input
        confirmButton = findViewById(R.id.button5);

        // Get verificationId and resendToken from the intent
        Intent intent = getIntent();
        mVerificationId = intent.getStringExtra("verificationId");
        mResendToken = intent.getParcelableExtra("resendToken");

        // Confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String code = otpInput.getText().toString();
            if (code.isEmpty()) {
                Toast.makeText(verify_page.this, "Please enter the OTP.", Toast.LENGTH_LONG).show();
            } else {
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        Toast.makeText(verify_page.this, "Verification successful.", Toast.LENGTH_LONG).show();
                        // Navigate to main activity or home screen
                        startActivity(new Intent(verify_page.this, selfie_page.class));
                        finish();
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(verify_page.this, "Verification failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static class verify_selife_page extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_verify_selfie_page);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}