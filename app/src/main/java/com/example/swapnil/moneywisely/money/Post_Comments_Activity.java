package com.example.swapnil.moneywisely.money;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.CommentsAdapter;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.JSONParser;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Post_Comments_Activity extends AppCompatActivity {
    public static final String TAG = Post_Comments_Activity.class.getSimpleName();

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref;
    Toolbar toolbar;
    Intent in;

    String str_post_id, str_mesg, str_success, str_cid, str_uid, str_comments, str_name;

    ArrayList<HashMap<String, String>> commentstList;
    CommentsAdapter commentsAdapter;
    ListView list_comments;

    EditText edt_comments;
    ImageView iv_send;

    OkHttpClient client;
    Request request;
    JSONParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__comments);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parser = new JSONParser();
        support = new SupportUtil(this);
        manager = new PrefManager(this);
        commentstList = new ArrayList<>();

        in = getIntent();
        str_post_id = in.getStringExtra("id");
        str_cid = in.getStringExtra("cid");
        str_name = in.getStringExtra("name");

        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_uid = pref.getString(manager.PM_userID, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Post_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        list_comments = (ListView) findViewById(R.id.list_comments);
        list_comments.setStackFromBottom(true);

        edt_comments = (EditText) findViewById(R.id.edt_comments);
        iv_send = (ImageView) findViewById(R.id.iv_send);

        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_comments = edt_comments.getText().toString().trim();

                if (str_comments.equals("") || str_comments.length() <= 0) {
                    edt_comments.setError("Enter Something...");
                    edt_comments.requestFocus();
                } else {
                    new Comment().execute();
                }
            }
        });

        if (support.checkInternetConnectivity()) {
            new GetDiscussPostComment().execute();
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

    private class GetDiscussPostComment extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Post_Comments_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_discussion_post_id, str_post_id);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.getDiscussPostComment).post(requestBody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);

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
                                        Log.e("Get Comments", "" + jsonObject);
                                        commentstList.clear();
                                        list_comments.invalidate();
                                        list_comments.refreshDrawableState();

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {

                                            JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                            for (int i = 0; i < jsondata.length(); i++) {
                                                JSONObject object = jsondata.getJSONObject(i);

                                                HashMap<String, String> market = new HashMap<String, String>();
                                                market.put(AppConstant.TAG_discussion_post_id, object.getString(AppConstant.TAG_discussion_post_id));
                                                market.put(AppConstant.TAG_discussion_post_comment_id, object.getString(AppConstant.TAG_discussion_post_comment_id));
                                                market.put(AppConstant.TAG_description, object.getString(AppConstant.TAG_description));
                                                market.put(AppConstant.TAG_user_id, object.getString(AppConstant.TAG_user_id));
                                                market.put(AppConstant.TAG_fullname, object.getString(AppConstant.TAG_fullname));

                                                commentstList.add(market);
                                            }
                                            commentsAdapter = new CommentsAdapter(Post_Comments_Activity.this, commentstList);
                                            list_comments.setAdapter(commentsAdapter);
                                            list_comments.setStackFromBottom(true);

                                        } else {
                                            str_mesg = jsonObject.getString("mesage");
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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Post_Comments_Activity.this);

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

    private class Comment extends AsyncTask<String, String, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Post_Comments_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_post_id, str_post_id));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_customer_id, str_cid));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_description, str_comments));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, str_uid));

                        JSONObject object = parser.makeHttpRequest(AppConstant.addDiscussionPostComment, "POST", pairs);
                        Log.e("Responce", object.toString());

                        try {
                            str_success = object.getString(AppConstant.TAG_success);

                            if (str_success.equals("1")) {
                                Log.e("entry", "mesage entered.......");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        edt_comments.setText("");
                                        str_comments = "";
                                        new GetDiscussPostComment().execute();
                                    }
                                });
                            } else {
                                str_mesg = object.getString("mesage");
                                alert();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Post_Comments_Activity.this);

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
        Intent intent = new Intent(getApplicationContext(), Post_Activity.class);
        startActivity(intent);
        finish();
    }
}
