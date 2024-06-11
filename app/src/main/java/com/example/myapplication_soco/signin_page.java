package com.example.myapplication_soco;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberInput.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(signin_page.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkUserExists(phoneNumber);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the register page
                Intent intent = new Intent(signin_page.this, register_page.class);
                startActivity(intent);
            }
        });
    }
    private void checkUserExists(String phoneNumber) {
        db.collection("profiles")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            String formattedPhoneNumber = "+1" + phoneNumber;
                            startPhoneNumberVerification(formattedPhoneNumber);
                        } else {
                            Toast.makeText(signin_page.this, "User does not exist. Please register.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(signin_page.this, "Error checking user.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Auto-retrieval or instant validation has succeeded
                Log.d("TAG", "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w("TAG", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(signin_page.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(signin_page.this, "Too many attempts.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d("TAG", "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
                Intent intent = new Intent(signin_page.this, verify_page.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("phoneNumber", phoneNumber);
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