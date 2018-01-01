package com.example.swapnil.moneywisely.money;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.SupportUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FoegetPass_Activity extends AppCompatActivity {
    public static final String TAG = FoegetPass_Activity.class.getSimpleName();

    Toolbar toolbar;
    SharedPreferences pref;

    SupportUtil support;
    OkHttpClient client;
    Request request;

    String str_success, str_title, str_link, str_mesg;
    EditText edt_mail;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        support = new SupportUtil(this);
        pref = getSharedPreferences("test", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forget Password");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        edt_mail = (EditText) findViewById(R.id.edt_email);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }
}
