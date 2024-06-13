package com.example.myapplication_soco;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class verify_signin_page extends AppCompatActivity {
    private static final String TAG = "verifySigninPage";
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
        setContentView(R.layout.activity_verify_registration_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        otpInput = findViewById(R.id.otpInput);
        confirmButton = findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        mVerificationId = intent.getStringExtra("verificationId");
        mResendToken = intent.getParcelableExtra("resendToken");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");

        confirmButton.setOnClickListener(v -> {
            String code = otpInput.getText().toString();
            if (code.isEmpty()) {
                Toast.makeText(verify_signin_page.this, "Please enter the OTP.", Toast.LENGTH_LONG).show();
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
                        Log.d(TAG, "signInWithCredential:success "+ getCurrentTimestamp());
                        Toast.makeText(verify_signin_page.this, "Verification successful.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(verify_signin_page.this, home_page.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure "+ getCurrentTimestamp() + task.getException());
                        Toast.makeText(verify_signin_page.this, "Verification failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private String getCurrentTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}