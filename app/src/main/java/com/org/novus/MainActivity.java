package com.org.novus;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Handler mHandler;
    Runnable mNextActivityCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Handler mHandler = new Handler();
        mHandler = new Handler();
        mNextActivityCallback = new Runnable() {
            @Override
            public void run() {
                // Intent to jump to the next activity
                Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish(); // so the splash activity goes away
            }

        };
        mHandler.postDelayed(mNextActivityCallback, 2000L);
    }
}
