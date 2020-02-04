package com.example.ticcattoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ShowIcon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_icon);

        final Intent intent = new Intent(this,  HomePage.class);

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                finish();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1000);
    }
}
