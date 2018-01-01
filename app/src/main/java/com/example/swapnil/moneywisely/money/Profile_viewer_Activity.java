package com.example.swapnil.moneywisely.money;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.ProfileAdapter;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.JSONParser;
import com.example.swapnil.moneywisely.support.NonScrollRecyclerView;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class Profile_viewer_Activity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = Profile_viewer_Activity.class.getSimpleName();
    Toolbar toolbar;

    SharedPreferences pref, pref_view, pref_image;
    SharedPreferences.Editor editor;
    PrefManager manager;
    SupportUtil support;

    ImageView iv_profile, iv_back_pic;
    TextView tv_name, tv_bio, tv_follow;
    RelativeLayout rl_post, rl_like, rl_doc, rl_follow;

    NonScrollRecyclerView list_wall;

    String str_name, str_bio, str_profile, str_success, str_mesg, str_uid, str_follow, str_fid;
    String str_type = "all";
    Intent in;

    OkHttpClient client;
    Request request;
    JSONParser parser;

    ProfileAdapter postAdapter;
    ArrayList<HashMap<String, String>> postList;

    ArrayList<Integer> dataLike, Likecount, markoff;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        in = getIntent();
        support = new SupportUtil(this);
        manager = new PrefManager(this);
        parser = new JSONParser();
        postList = new ArrayList<>();
        dataLike = new ArrayList<>();
        Likecount = new ArrayList<>();
        markoff = new ArrayList<>();

        pref_image = getSharedPreferences("test", 0);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_uid = pref.getString(manager.PM_userID, null);

        pref_view = getSharedPreferences("view", 0);

        str_name = pref_view.getString("name", null);
        str_profile = pref_view.getString("image", null);
        str_bio = pref_view.getString("bio", null);
        str_follow = pref_view.getString("follow", null);
        str_fid = pref_view.getString("fuid", null);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        iv_back_pic = (ImageView) findViewById(R.id.iv_back_pic);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_bio = (TextView) findViewById(R.id.tv_bio);
        tv_follow = (TextView) findViewById(R.id.tv_follow);

        rl_post = (RelativeLayout) findViewById(R.id.rl_post);
        rl_like = (RelativeLayout) findViewById(R.id.rl_like);
        rl_doc = (RelativeLayout) findViewById(R.id.rl_doc);
        rl_follow = (RelativeLayout) findViewById(R.id.rl_follow);

        list_wall = (NonScrollRecyclerView) findViewById(R.id.list_wall);
        list_wall.setHasFixedSize(true);
        list_wall.setLayoutManager(new LinearLayoutManager(this));
        list_wall.setItemAnimator(new DefaultItemAnimator());
        list_wall.setNestedScrollingEnabled(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pref_view.getString("post", null).equals("follow")) {
                    Intent intent = new Intent(getApplicationContext(), FollowersList_Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Post_Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        if (str_profile.isEmpty()) {
            iv_profile.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(this)
                    .load(str_profile)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.drawable.progress_animation)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(iv_profile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Profile_viewer_Activity.this)
                                    .load(str_profile)
                                    .error(R.mipmap.ic_launcher)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(iv_profile);
                        }
                    });
        }

        tv_name.setText(str_name);
        tv_bio.setText(str_bio);

        if (str_follow.equals("1") || str_follow.equals(1)) {
            tv_follow.setText("Following");
        } else {
            tv_follow.setText("Follow");
        }

        rl_follow.setOnClickListener(this);
        rl_post.setOnClickListener(this);
        rl_like.setOnClickListener(this);
        rl_doc.setOnClickListener(this);
        tv_follow.setOnClickListener(this);

        if (support.checkInternetConnectivity()) {
            str_type = "all";
            new GetProfilePost().execute();
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

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = pref_image.edit();
                editor.putString("name", str_name + "'s Profile Image");
                editor.putString("img", str_profile);
                editor.putString("back", "profile_view");
                editor.commit();
                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullScreenImage_Activity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == rl_follow) {
            Intent in = new Intent(getApplicationContext(), FollowersListViewer_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_post) {
            str_type = "all";
            new GetProfilePost().execute();
        } else if (view == rl_like) {
            str_type = "like";
            new GetProfilePost().execute();
        } else if (view == rl_doc) {
            str_type = "document";
            new GetProfilePost().execute();
        } else if (view == tv_follow) {
            if (tv_follow.getText().equals("Follow")) {
                dialog = new Dialog(this);
                Window window = dialog.getWindow();
                window.requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_follow);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);

                TextView tv_label = (TextView) dialog.findViewById(R.id.tv_lable);
                TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                TextView tv_follow = (TextView) dialog.findViewById(R.id.tv_follow);

                tv_label.setText("Do You want to follow " + str_name + "?");
                tv_follow.setText("Follow");

                tv_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AddFollow().execute();
                    }
                });

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else {
                dialog = new Dialog(this);
                Window window = dialog.getWindow();
                window.requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_follow);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);

                TextView tv_label = (TextView) dialog.findViewById(R.id.tv_lable);
                TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                TextView tv_follow = (TextView) dialog.findViewById(R.id.tv_follow);

                tv_label.setText("Do You want to unfollow " + str_name + "?");
                tv_follow.setText("Unfollow");

                tv_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DeleteFollow().execute();
                    }
                });

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    }

    private class AddFollow extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_viewer_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, str_uid));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_follower_user_id, str_fid));

                        JSONObject object = parser.makeHttpRequest(AppConstant.addFollow, "POST", pairs);
                        Log.e("AddFollow", object.toString());

                        try {
                            str_success = object.getString(AppConstant.TAG_success);
                            if (str_success.equals("1")) {
                                tv_follow.setText("Following");
                                str_mesg = object.getString("message");
                                dialog.dismiss();
                                alert();
                            } else {
                                str_mesg = object.getString("message");
                                dialog.dismiss();
                                alert();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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

    private class DeleteFollow extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_viewer_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, str_uid));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_follower_user_id, str_fid));

                        JSONObject object = parser.makeHttpRequest(AppConstant.deleteFollow, "POST", pairs);
                        Log.e("DeleteFollow", object.toString() + "UID : " + str_uid + "FID : " + str_fid);

                        try {
                            str_success = object.getString(AppConstant.TAG_success);
                            if (str_success.equals("1")) {
                                tv_follow.setText("Follow");
                                str_mesg = object.getString("mesage");
                                dialog.dismiss();
                                alert();
                            } else {
                                str_mesg = object.getString("mesage");
                                dialog.dismiss();
                                alert();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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

    private class GetProfilePost extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_viewer_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_type, str_type)
                        .add(AppConstant.TAG_user_id, str_fid);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.getDiscussionProfile).post(requestBody).build();

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
                                        Log.e("getDiscussionProfile", "" + jsonObject);

                                        postList.clear();
                                        list_wall.invalidate();
                                        list_wall.refreshDrawableState();

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {

                                            JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                            for (int i = 0; i < jsondata.length(); i++) {
                                                JSONObject object = jsondata.getJSONObject(i);

                                                HashMap<String, String> market = new HashMap<String, String>();
                                                market.put(AppConstant.TAG_discussion_post_id, object.getString(AppConstant.TAG_discussion_post_id));
                                                market.put(AppConstant.TAG_discussion_id, object.getString(AppConstant.TAG_discussion_id));
                                                market.put(AppConstant.TAG_description, object.getString(AppConstant.TAG_description));
                                                market.put(AppConstant.TAG_mark_offensive, object.getString(AppConstant.TAG_mark_offensive));
                                                market.put(AppConstant.TAG_post_like_count, object.getString(AppConstant.TAG_post_like_count));
                                                market.put(AppConstant.TAG_image_document_path, object.getString(AppConstant.TAG_image_document_path));
                                                market.put(AppConstant.TAG_type, object.getString(AppConstant.TAG_type));
                                                market.put(AppConstant.TAG_follower_count, object.getString(AppConstant.TAG_follower_count));
                                                market.put(AppConstant.TAG_discussion_post_count, object.getString(AppConstant.TAG_discussion_post_count));
                                                market.put(AppConstant.TAG_type, object.getString(AppConstant.TAG_type));
                                                market.put(AppConstant.TAG_like, object.getString(AppConstant.TAG_like));
                                                market.put(AppConstant.TAG_follow, object.getString(AppConstant.TAG_follow));
                                                market.put(AppConstant.TAG_file_name, object.getString(AppConstant.TAG_file_name));

                                                JSONObject juser = object.getJSONObject("user_data");
                                                market.put(AppConstant.TAG_user_id, juser.getString(AppConstant.TAG_user_id));
                                                market.put(AppConstant.TAG_fullname, juser.getString(AppConstant.TAG_fullname));
                                                market.put(AppConstant.TAG_email, juser.getString(AppConstant.TAG_email));
                                                market.put(AppConstant.TAG_phone_number, juser.getString(AppConstant.TAG_phone_number));
                                                market.put(AppConstant.TAG_dob, juser.getString(AppConstant.TAG_dob));
                                                market.put(AppConstant.TAG_bio, juser.getString(AppConstant.TAG_bio));
                                                market.put(AppConstant.TAG_profile_image, juser.getString(AppConstant.TAG_profile_image));

                                                postList.add(market);

                                                dataLike.add(Integer.parseInt(object.getString(AppConstant.TAG_like)));
                                                Likecount.add(Integer.parseInt(object.getString(AppConstant.TAG_post_like_count)));
                                                markoff.add(Integer.parseInt(object.getString(AppConstant.TAG_mark_offensive)));
                                            }
                                            postAdapter = new ProfileAdapter(Profile_viewer_Activity.this, postList, dataLike, Likecount, markoff);
                                            list_wall.setAdapter(postAdapter);

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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_viewer_Activity.this);

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_viewer_Activity.this);

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
        if (pref_view.getString("post", null).equals("follow")) {
            Intent intent = new Intent(getApplicationContext(), FollowersList_Activity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), Post_Activity.class);
            startActivity(intent);
            finish();
        }
    }
}
