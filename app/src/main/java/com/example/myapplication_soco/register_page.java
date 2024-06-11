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
import java.util.concurrent.TimeUnit;
import android.text.Spanned;

public class register_page extends AppCompatActivity {
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        signInButton = findViewById(R.id.signInButtn);
        registerButton = findViewById(R.id.regButton);
        textViewTerms = findViewById(R.id.editTextTextMultiLine);

        // Set terms and conditions text with clickable link
        setTermsAndConditions();

        registerButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String phoneNumber = phoneNumberInput.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(register_page.this, "Please fill all fields.", Toast.LENGTH_LONG).show();
            } else {
                checkUserExists(phoneNumber);
            }
        });

        signInButton.setOnClickListener(v -> startActivity(new Intent(register_page.this, signin_page.class)));
    }

    private void setTermsAndConditions() {
        String text = "By registering, you agree to the terms and conditions and privacy policy";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://thesocoapp.com/terms-conditions/"));
                startActivity(browserIntent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);  // Set to false if you don't want underlines
            }
        };
        int start = text.indexOf("terms and conditions");
        int end = start + "terms and conditions".length();
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewTerms.setText(ss);
        textViewTerms.setMovementMethod(LinkMovementMethod.getInstance());  // Make the link clickable
    }

    private void checkUserExists(String phoneNumber) {
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
                            Toast.makeText(register_page.this, "Phone number already in use. Please sign in.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(register_page.this, "Error. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
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




