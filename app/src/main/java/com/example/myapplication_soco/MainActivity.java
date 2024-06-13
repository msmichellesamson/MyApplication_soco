package com.example.myapplication_soco;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logoImageView = findViewById(R.id.logo);

        // Load fade-in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);
        fadeInAnimation.setDuration(1000); // Adjust duration as needed

        // Set listener to start scale animation after fade-in animation completes
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // No implementation needed
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start scale animation
                logoImageView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.logo_enlarge_animation));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No implementation needed
            }
        });

        // Start fade-in animation
        logoImageView.setVisibility(ImageView.VISIBLE); // Ensure visibility is set before starting animation
        logoImageView.startAnimation(fadeInAnimation);

        // Post delayed handler to navigate after SPLASH_DURATION
        new Handler().postDelayed(() -> {
            // Check if the user is signed in
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // User is signed in, navigate to home page
                startActivity(new Intent(MainActivity.this, home_page.class));
            } else {
                // No user is signed in, navigate to login page
                startActivity(new Intent(MainActivity.this, startup_page.class));
            }
            finish();
        }, SPLASH_DURATION);
    }
}