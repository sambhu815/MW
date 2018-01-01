package com.example.swapnil.moneywisely.money;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.MarketAdapter;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FamousMMB_Activity extends AppCompatActivity {
    public static final String TAG = EquityMarket_Activity.class.getSimpleName();

    Toolbar toolbar;

    ListView list_famous;
    MarketAdapter marketAdapter;
    ArrayList<HashMap<String, String>> marketList;

    SupportUtil support;
    OkHttpClient client;
    Request request;

    String str_success, str_title, str_link, str_image, str_mesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_famous_mmb);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        marketList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Famous MMB");

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

        list_famous = (ListView) findViewById(R.id.list_famous);

        if (support.checkInternetConnectivity()) {
            new GetFamousMMB().execute();
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

    private class GetFamousMMB extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(FamousMMB_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                RequestBody reqbody = RequestBody.create(null, new byte[0]);

                request = new Request.Builder().url(AppConstant.getFamousMMB).post(reqbody).build();

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
                                        Log.e("getFamousMMB ->", "" + jsonObject);

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {

                                            JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                            for (int i = 0; i < jsondata.length(); i++) {
                                                JSONObject object = jsondata.getJSONObject(i);

                                                str_title = object.getString(AppConstant.TAG_famousmmb_title);
                                                str_link = object.getString(AppConstant.TAG_famousmmb_link);
                                                str_image = object.getString(AppConstant.TAG_famousmmb_img);

                                                HashMap<String, String> market = new HashMap<String, String>();
                                                market.put(AppConstant.TAG_famousmmb_title, str_title);
                                                market.put(AppConstant.TAG_famousmmb_link, str_link);
                                                market.put(AppConstant.TAG_famousmmb_img, str_image);
                                                market.put(AppConstant.TAG_famousmmb_desc, object.getString(AppConstant.TAG_famousmmb_desc));

                                                marketList.add(market);
                                            }
                                            marketAdapter = new MarketAdapter(FamousMMB_Activity.this, marketList, "famous");
                                            list_famous.setAdapter(marketAdapter);

                                        } else {
                                            str_mesg = jsonObject.getString("message");
                                            alert();
                                        }
                                    } catch (final JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                Log.d(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final SocketTimeoutException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                e.printStackTrace();
                                                Log.d(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                e.printStackTrace();
                                                Log.d(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final NullPointerException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                Log.d(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final Exception e) {
                                        str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                        Log.d(TAG, e.getMessage());
                                        alert();
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FamousMMB_Activity.this);

                                    alertDialogBuilder.setTitle(getResources().getString(R.string.error));

                                    alertDialogBuilder
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent in = new Intent(getApplicationContext(), Home_Activity.class);
                                                    startActivity(in);
                                                    finish();
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FamousMMB_Activity.this);

        alertDialogBuilder.setTitle(str_mesg);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent in = new Intent(getApplicationContext(), Home_Activity.class);
                        startActivity(in);
                        finish();
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
