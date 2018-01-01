package com.example.swapnil.moneywisely.money;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.PrefManager;

public class Splash_Activity extends AppCompatActivity {

    PrefManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        manager = new PrefManager(this);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);

                    Intent i;
                    if (manager.isLoggedIn()) {
                        i = new Intent(getApplicationContext(), Home_Activity.class);
                    } else {
                        i = new Intent(getApplicationContext(), Login_Activity.class);
                    }
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }
}
