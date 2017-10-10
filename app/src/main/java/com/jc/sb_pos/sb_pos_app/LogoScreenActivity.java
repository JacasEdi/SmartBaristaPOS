package com.jc.sb_pos.sb_pos_app;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogoScreenActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_screen);

        // Call the Login Screen Activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                startActivity(new Intent(LogoScreenActivity.this, LoginScreenActivity.class));
                finish(); // kill the current activity
            }
        }, 3000);
    }
}
