package com.example.myapplication_soco;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class verify_page extends AppCompatActivity {

    EditText otpInput;
    Button confirmButton;
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String firstName, lastName;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        otpInput = findViewById(R.id.otpInput); // Assume you have an EditText for OTP input
        confirmButton = findViewById(R.id.confirmButton);

        // Get verificationId and resendToken from the intent
        Intent intent = getIntent();
        mVerificationId = intent.getStringExtra("verificationId");
        mResendToken = intent.getParcelableExtra("resendToken");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");


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
                        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
                        checkUserExists(phoneNumber);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(verify_page.this, "Verification failed.", Toast.LENGTH_LONG).show();
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
                            Log.d("TAG", "User already exists in the database.");
                        } else {
                            addUserToFirestore(phoneNumber);
                        }
                        startActivity(new Intent(verify_page.this, selfie_page.class));
                    } else {
                        Toast.makeText(verify_page.this, "Error checking user.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToFirestore(String uid) {
        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        Map<String, Object> user = new HashMap<>();
        user.put("Document ID", uid);
        user.put("phoneNumber", phoneNumber);
        user.put("firstName", firstName);
        user.put("lastName", lastName);

        db.collection("profiles").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Error writing document", e);
                    Toast.makeText(verify_page.this, "Error saving user data.", Toast.LENGTH_LONG).show();
                });
    }

}