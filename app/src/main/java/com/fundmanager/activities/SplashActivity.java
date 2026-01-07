package com.fundmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.fundmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        // Delay for splash screen
        new Handler().postDelayed(() -> {
            checkUserLoggedIn();
        }, SPLASH_DELAY);
    }

    private void checkUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            // User is logged in, go to MainActivity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // User is not logged in, go to LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }
}
