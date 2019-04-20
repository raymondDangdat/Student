package com.example.raymond.student;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeScreen extends AppCompatActivity {
    //timeout for the splash screen
    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginItent = new Intent(WelcomeScreen.this, LoginActivity.class);
                startActivity(loginItent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
