package com.example.myapplication_soco;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.ImageView;
import android.widget.Switch;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class settings_page extends AppCompatActivity {
    Button DeleteAccount;
    Button SignOut;
    TextView change_profile_picture;
    TextView Terms_conditions;
    TextView Privacy_Policy;
    TextView Contact_us;
    TextView UserName;
    ImageView backButton, profilePic;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_page);


        DeleteAccount = findViewById(R.id.DeleteButton);
        SignOut = findViewById(R.id.SignOutButton);
        change_profile_picture = findViewById(R.id.change_profile_picture);
        Terms_conditions = findViewById(R.id.Terms_conditions);
        Privacy_Policy = findViewById(R.id.Privacy_Policy);
        Contact_us = findViewById(R.id.Contact_us);
        SwitchCompat notificationSwitch = findViewById(R.id.switch1);
        SwitchCompat BluetoothSwitch = findViewById(R.id.switch2);
        backButton = findViewById(R.id.backButton);
        UserName = findViewById(R.id.UserName);
        profilePic = findViewById(R.id.profilePic);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();;

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( settings_page.this, home_page.class);
                startActivity(intent);
            }
        });

        setTermsAndConditions();
        setPrivacyAndPolicy();
        ContactUsLink();
        loadUserName(uid);
        loadProfilePic(uid);

        change_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MainActivity when the delete account button is clicked
                Intent intent = new Intent(settings_page.this, selfie_page.class);
                startActivity(intent);
            }
        });

        notificationSwitch.setChecked(NotificationHelper.areNotificationsEnabled(this));

        // Set an onCheckedChangeListener on the switch
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable notifications
                NotificationHelper.enableNotifications(settings_page.this);
                Toast.makeText(settings_page.this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Disable notifications
                NotificationHelper.disableNotifications(settings_page.this);
                Toast.makeText(settings_page.this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });


//        DeleteAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the MainActivity when the delete account button is clicked
//                Intent intent = new Intent(settings_page.this, startup_page.class);
//                startActivity(intent);
//            }
//        });
//        SignOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the MainActivity when the delete account button is clicked
//                Intent intent = new Intent(settings_page.this, signin_page.class);
//                startActivity(intent);
//            }
//        });
    }
    private void setTermsAndConditions() {
//        Log.i(TAG, "Setting terms and conditions at " + getCurrentTimestamp());
        String text = "Terms and conditions";
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
        Terms_conditions.setText(ss);
        Terms_conditions.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setPrivacyAndPolicy() {
//        Log.i(TAG, "Setting terms and conditions at " + getCurrentTimestamp());
        String text = "Privacy Policy";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://thesocoapp.com/privacy-policy/"));
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
        Privacy_Policy.setText(ss);
        Privacy_Policy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void ContactUsLink() {
//        Log.i(TAG, "Setting up Contact Us link at " + getCurrentTimestamp());
        String text = "contact us.";
        SpannableString ss = new SpannableString(text);
        ClickableSpan contactUsSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                // Create an intent to open the default email client with pre-filled data
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@thesocoapp.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");

                // Verify that the intent will resolve to an activity
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No email app found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(ds.linkColor);  // Set the color of the link
            }
        };

        int start = 0; // Start index for "click here"
        int end = text.length();   // End index for "click here"
        ss.setSpan(contactUsSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        Contact_us.setText(ss);
        Contact_us.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void loadUserName(String uid) {
        db.collection("profiles")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String fullName = "";

                            if (firstName != null) {
                                fullName += firstName;
                            }

                            if (lastName != null) {
                                fullName += " " + lastName;
                            }

                            if (fullName.trim().isEmpty()) {
                                UserName.setText("User name not available");
                            } else {
                                UserName.setText(fullName.trim());
                            }
                        } else {
                            UserName.setText("User not found");
                        }
                    } else {
                        UserName.setText("Failed to load user data");
                    }
                })
                .addOnFailureListener(e -> UserName.setText("Error: " + e.getMessage()));
    }
    private void loadProfilePic(String uid) {
        db.collection("profiles")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("profilePicUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Use Glide to load and resize the image
                                Glide.with(this)
                                        .load(imageUrl)
                                        .centerCrop() // Crop if necessary
                                        .placeholder(R.drawable.placeholder) // Placeholder image while loading
                                        .error(R.drawable.error_image) // Error image if loading fails
                                        .override(100, 100) // Resize dimensions
                                        .into(profilePic);
                            }
                        }
                    } else {
                        //Log.d(TAG, "Error getting document: ", task.getException());
                    }
                });
    }

}