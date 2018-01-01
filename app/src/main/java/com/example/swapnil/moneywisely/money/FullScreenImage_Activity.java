package com.example.swapnil.moneywisely.money;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.swapnil.moneywisely.R;
import com.squareup.picasso.Picasso;

public class FullScreenImage_Activity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences pref;
    String str_img, str_name, str_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        pref = getSharedPreferences("test", 0);
        str_img = pref.getString("img", null);
        str_name = pref.getString("name", null);
        str_back = pref.getString("back", null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (str_back.equals("tab")) {
                    Intent intent = new Intent(getApplicationContext(), Tab_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (str_back.equals("profile")) {
                    Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (str_back.equals("profile_view")) {
                    Intent intent = new Intent(getApplicationContext(), Profile_viewer_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Post_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);

        if (str_img.isEmpty()) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(this)
                    .load(str_img)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.drawable.progress_animation)
                    .into(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        if (str_back.equals("tab")) {
            Intent intent = new Intent(getApplicationContext(), Tab_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (str_back.equals("profile")) {
            Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (str_back.equals("profile_view")) {
            Intent intent = new Intent(getApplicationContext(), Profile_viewer_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), Post_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
