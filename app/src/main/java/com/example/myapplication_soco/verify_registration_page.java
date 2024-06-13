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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class verify_registration_page extends AppCompatActivity {
    private static final String TAG = "verifyRegistrationPage";
    EditText otpInput;
    Button confirmButton;
    FirebaseAuth mAuth;
    String mVerificationId;
    String firstName, lastName;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_registration_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        otpInput = findViewById(R.id.otpInput);
        confirmButton = findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        mVerificationId = intent.getStringExtra("verificationId");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");

        confirmButton.setOnClickListener(v -> {
            String code = otpInput.getText().toString();
            if (code.isEmpty()) {
                Toast.makeText(verify_registration_page.this, "Please enter the OTP.", Toast.LENGTH_LONG).show();
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
                        Log.d(TAG, "signInWithCredential:success"+ getCurrentTimestamp());
                        Toast.makeText(verify_registration_page.this, "Verification successful.", Toast.LENGTH_LONG).show();
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        addUserToFirestore(uid);
                        Intent intent = new Intent(verify_registration_page.this, selfie_page.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "onVerificationFailed at " + getCurrentTimestamp(), task.getException());
                        Toast.makeText(verify_registration_page.this, "Verification failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void addUserToFirestore(String uid) {
        String phoneNumber = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
//        assert phoneNumber != null;
        phoneNumber = phoneNumber.substring(2);

        Map<String, Object> user = new HashMap<>();
        user.put("Document ID", uid);
        user.put("phoneNumber", phoneNumber);
        user.put("firstName", firstName);
        user.put("lastName", lastName);

        db.collection("profiles").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!"+ getCurrentTimestamp());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    Toast.makeText(verify_registration_page.this, "Error saving user data.", Toast.LENGTH_LONG).show();
                });
    }
    private String getCurrentTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
