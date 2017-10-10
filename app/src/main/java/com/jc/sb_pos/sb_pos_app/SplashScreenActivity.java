package com.jc.sb_pos.sb_pos_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Call the Login Screen
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashScreenActivity.this, LogoScreenActivity.class);
                startActivity(intent);
                finish();

            }
        };

        Timer t = new Timer();
        t.schedule(task, 5000);
    }
}
