package com.example.swapnil.moneywisely.money;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.ProfileAdapter;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.NonScrollListView;
import com.example.swapnil.moneywisely.support.NonScrollRecyclerView;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Profile_Activity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = Profile_Activity.class.getSimpleName();
    Toolbar toolbar;

    SharedPreferences pref, pref_image;
    SharedPreferences.Editor editor;
    PrefManager manager;
    SupportUtil support;

    ImageView iv_profile, iv_back_pic;
    TextView tv_name, tv_bio;
    RelativeLayout rl_bio, rl_post, rl_like, rl_doc, rl_follow;
    NonScrollRecyclerView list_wall;

    String str_name, str_email, str_profile, str_dob, str_bio, str_uid, str_phone, str_success, str_mesg;
    String str_type = "all";
    Dialog dialog;

    OkHttpClient client;
    Request request;

    ProfileAdapter postAdapter;
    ArrayList<HashMap<String, String>> postList;

    ArrayList<Integer> dataLike, Likecount, markoff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        postList = new ArrayList<>();
        dataLike = new ArrayList<>();
        Likecount = new ArrayList<>();
        markoff = new ArrayList<>();

        pref_image = getSharedPreferences("test", 0);
        pref = getSharedPreferences(manager.PREF_NAME, 0);

        str_name = pref.getString(manager.PM_name, null);
        str_email = pref.getString(manager.PM_email, null);
        str_profile = pref.getString(manager.PM_profile, null);
        str_dob = pref.getString(manager.PM_dob, null);
        str_bio = pref.getString(manager.PM_bio, null);
        str_uid = pref.getString(manager.PM_userID, null);
        str_phone = pref.getString(manager.PM_phone, null);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        iv_back_pic = (ImageView) findViewById(R.id.iv_back_pic);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_bio = (TextView) findViewById(R.id.tv_bio);

        rl_bio = (RelativeLayout) findViewById(R.id.rl_bio);
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
                Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(intent);
                finish();
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
                            Picasso.with(Profile_Activity.this)
                                    .load(str_profile)
                                    .error(R.mipmap.ic_launcher)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(iv_profile);
                        }
                    });
        }

        tv_name.setText(str_name);
        if (str_bio.equals("null") || str_bio == null || str_bio.isEmpty()) {
            tv_bio.setText("(Describe your self...)");
        } else {
            tv_bio.setText(str_bio);
        }

        rl_follow.setOnClickListener(this);
        rl_bio.setOnClickListener(this);
        rl_post.setOnClickListener(this);
        rl_like.setOnClickListener(this);
        rl_doc.setOnClickListener(this);

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
                editor.putString("back", "profile");
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
            Intent in = new Intent(getApplicationContext(), FollowersList_Activity.class);
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
        } else if (view == rl_bio) {
            dialog = new Dialog(this);
            Window window = dialog.getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_bio);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final EditText edt_bio = (EditText) dialog.findViewById(R.id.edt_bio);
            ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
            Button btn_bio = (Button) dialog.findViewById(R.id.btn_bio);

            edt_bio.setText(str_bio);

            btn_bio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    str_bio = edt_bio.getText().toString().trim();

                    if (support.checkInternetConnectivity()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new EditBio().execute();
                            }
                        });
                        dialog.dismiss();
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
            });

            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        str_name = pref.getString(manager.PM_name, null);
        str_profile = pref.getString(manager.PM_profile, null);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent in = new Intent(getApplicationContext(), EditProfile_Activity.class);
            startActivity(in);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
        startActivity(intent);
        finish();
    }

    private class GetProfilePost extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_Activity.this);
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
                        .add(AppConstant.TAG_user_id, str_uid);

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
                                        dataLike.clear();
                                        Likecount.clear();
                                        markoff.clear();
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
                                            postAdapter = new ProfileAdapter(Profile_Activity.this, postList, dataLike, Likecount, markoff);
                                            list_wall.setAdapter(postAdapter);
                                            Log.e("postList", "" + Likecount);
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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Activity.this);

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

    private class EditBio extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Profile_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_bio, str_bio)
                        .add(AppConstant.TAG_user_id, str_uid);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.editBio).post(requestBody).build();

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
                                        Log.e("editBio ->", "" + jsonObject);

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {
                                            str_mesg = jsonObject.getString("message");
                                            alert();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv_bio.setText(str_bio);
                                                    manager.createLogin(str_name, str_email, str_phone, str_dob, str_bio, str_uid, str_profile);
                                                }
                                            });
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                Log.d(TAG, e.getMessage());
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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Activity.this);

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Activity.this);

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
}
