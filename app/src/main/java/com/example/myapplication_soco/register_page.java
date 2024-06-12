package com.example.myapplication_soco;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.text.Spanned;
public class register_page extends AppCompatActivity {
    private static final String TAG = "RegisterPage";
    EditText firstNameInput, lastNameInput, phoneNumberInput;
    Button signInButton, registerButton;
    TextView textViewTerms;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        Log.i(TAG, "Register page created at " + getCurrentTimestamp());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        signInButton = findViewById(R.id.signInButton1);
        registerButton = findViewById(R.id.registerButton1);
        textViewTerms = findViewById(R.id.textTCLink);

        setTermsAndConditions();

        registerButton.setOnClickListener(v -> {
            Log.i(TAG, "Register button clicked at " + getCurrentTimestamp());
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String phoneNumber = phoneNumberInput.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(register_page.this, "Please fill all fields.", Toast.LENGTH_LONG).show();
            } else {
                checkIfUserExists(phoneNumber);
            }
        });
        signInButton.setOnClickListener(v -> {
            Log.i(TAG, "Sign in button clicked at " + getCurrentTimestamp());
            startActivity(new Intent(register_page.this, signin_page.class));
        });
    }

    private void setTermsAndConditions() {
        Log.i(TAG, "Setting terms and conditions at " + getCurrentTimestamp());
        String text = "By registering, you agree to the terms and conditions and privacy policy.";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://thesocoapp.com/terms-conditions/"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        int start = 0;
        int end = start + text.length();
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewTerms.setText(ss);
        textViewTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void checkIfUserExists(String phoneNumber) {
        Log.i(TAG, "Checking if user exists for phone number: " + phoneNumber + " at " + getCurrentTimestamp());
        db.collection("profiles")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            String formattedPhoneNumber = "+1" + phoneNumber;
                            startPhoneNumberVerification(formattedPhoneNumber);
                        } else {
                            Log.i(TAG, "User with phone number " + phoneNumber + " already exists at " + getCurrentTimestamp());
                            Toast.makeText(register_page.this, "Phone number already in use. Please sign in.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e(TAG, "Error checking user existence at " + getCurrentTimestamp(), task.getException());
                        Toast.makeText(register_page.this, "Error. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        Log.i(TAG, "Starting phone number verification for: " + phoneNumber + " at " + getCurrentTimestamp());
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This method is triggered in some cases without the need for manual code entry
                Log.d(TAG, "onVerificationCompleted:" + credential + " at " + getCurrentTimestamp());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed at " + getCurrentTimestamp(), e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(register_page.this, "Please check the number and try again.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(register_page.this, "Malicious activity detected. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId + " at " + getCurrentTimestamp());

                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
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
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}




