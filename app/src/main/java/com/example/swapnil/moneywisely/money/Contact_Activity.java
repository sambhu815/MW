package com.example.swapnil.moneywisely.money;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Contact_Activity extends AppCompatActivity {
    public static final String TAG = Contact_Activity.class.getSimpleName();

    Toolbar toolbar;
    SupportUtil support;

    OkHttpClient client;
    Request request;
    Typeface typeface;

    String str_success, str_mesg;
    String s, s1, s2, s3, s4, s5, s6;

    TextView tv_name, tv_call, tv_sms, tv_mail, tv_skype, tv_add, tv_web;
    TextView txt_name, txt_call, txt_sms, txt_mail, txt_skype, txt_add, txt_web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact us");

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

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_call = (TextView) findViewById(R.id.tv_call);
        tv_sms = (TextView) findViewById(R.id.tv_sms);
        tv_mail = (TextView) findViewById(R.id.tv_mail);
        tv_skype = (TextView) findViewById(R.id.tv_skype);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_web = (TextView) findViewById(R.id.tv_web);

        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_call = (TextView) findViewById(R.id.txt_call);
        txt_sms = (TextView) findViewById(R.id.txt_sms);
        txt_mail = (TextView) findViewById(R.id.txt_mail);
        txt_skype = (TextView) findViewById(R.id.txt_skype);
        txt_add = (TextView) findViewById(R.id.txt_add);
        txt_web = (TextView) findViewById(R.id.txt_web);

        txt_name.setTypeface(typeface);
        txt_call.setTypeface(typeface);
        txt_sms.setTypeface(typeface);
        txt_mail.setTypeface(typeface);
        txt_skype.setTypeface(typeface);
        txt_add.setTypeface(typeface);
        txt_web.setTypeface(typeface);

        if (support.checkInternetConnectivity()) {
            new GetContact().execute();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Please Check your Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }
    }

    private class GetContact extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Contact_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                RequestBody reqbody = RequestBody.create(null, new byte[0]);

                request = new Request.Builder().url(AppConstant.getContact).post(reqbody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);
                int status = response.code();

                if (response.code() == 400) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    return response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String json = response.body().string();
                                        JSONObject jsonObject = new JSONObject(json);
                                        Log.e("getContact ->", "" + jsonObject);

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {

                                            JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                            for (int i = 0; i < jsondata.length(); i++) {
                                                JSONObject object = jsondata.getJSONObject(0);
                                                JSONObject object1 = jsondata.getJSONObject(1);
                                                JSONObject object2 = jsondata.getJSONObject(2);
                                                JSONObject object3 = jsondata.getJSONObject(3);
                                                JSONObject object4 = jsondata.getJSONObject(4);
                                                JSONObject object5 = jsondata.getJSONObject(5);
                                                JSONObject object6 = jsondata.getJSONObject(6);

                                                s = object.getString(AppConstant.TAG_contact_desc);
                                                s1 = object1.getString(AppConstant.TAG_contact_desc);
                                                s2 = object2.getString(AppConstant.TAG_contact_desc);
                                                s3 = object3.getString(AppConstant.TAG_contact_desc);
                                                s4 = object4.getString(AppConstant.TAG_contact_desc);
                                                s5 = object5.getString(AppConstant.TAG_contact_desc);
                                                s6 = object6.getString(AppConstant.TAG_contact_desc);
                                            }

                                            tv_name.setText(s);
                                            tv_call.setText(s1);
                                            tv_sms.setText(s2);
                                            tv_mail.setText(s3);
                                            tv_skype.setText(s4);
                                            tv_add.setText(s5);
                                            tv_web.setText(s6);

                                        } else {
                                            str_mesg = jsonObject.getString("message");
                                            alert();
                                        }
                                    } catch (final JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final SocketTimeoutException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                e.printStackTrace();
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                e.printStackTrace();
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final NullPointerException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final Exception e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Contact_Activity.this);

                                    alertDialogBuilder.setTitle(getResources().getString(R.string.error));

                                    alertDialogBuilder
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Contact_Activity.this);

        alertDialogBuilder.setTitle(str_mesg);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
