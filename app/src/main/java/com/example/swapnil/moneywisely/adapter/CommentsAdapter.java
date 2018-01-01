package com.example.swapnil.moneywisely.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.PrefManager;

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

/**
 * Created by Swapnil.Patel on 09-09-2017.
 */

public class CommentsAdapter extends BaseAdapter {
    public static final String TAG = CommentsAdapter.class.getSimpleName();

    Activity activity;
    ArrayList<HashMap<String, String>> commentstList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    PrefManager manager;
    SharedPreferences pref;
    String str_uid, str_cid;

    OkHttpClient client;
    Request request;
    int position;
    String str_post_id, str_dis_id, str_success, str_mesg;

    public CommentsAdapter(Activity activity, ArrayList<HashMap<String, String>> commentstList) {
        this.activity = activity;
        this.commentstList = commentstList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return commentstList.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_comments, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        str_uid = pref.getString(manager.PM_userID, null);

        ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

        TextView tv_commetns = (TextView) view.findViewById(R.id.tv_comments);
        TextView tv_dis_id = (TextView) view.findViewById(R.id.tv_dis_id);
        TextView tv_uid = (TextView) view.findViewById(R.id.tv_uid);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

        resultp = commentstList.get(i);

        tv_commetns.setText(resultp.get(AppConstant.TAG_description));
        tv_dis_id.setText(resultp.get(AppConstant.TAG_discussion_post_id));
        tv_uid.setText(resultp.get(AppConstant.TAG_user_id));
        tv_name.setText(resultp.get(AppConstant.TAG_fullname));

        str_cid = resultp.get(AppConstant.TAG_user_id);

        if (str_uid.equals(str_cid)) {
            iv_delete.setVisibility(View.VISIBLE);
        } else {
            iv_delete.setVisibility(View.GONE);
        }

        iv_delete.setTag(i);
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resultp = commentstList.get(i);
                str_dis_id = resultp.get(AppConstant.TAG_discussion_post_id);
                str_cid = resultp.get(AppConstant.TAG_user_id);
                str_post_id = resultp.get(AppConstant.TAG_discussion_post_comment_id);

                position = (int) view.getTag();
                commentstList.remove(position);
                notifyDataSetChanged();

                new DeleteComment().execute();

            }
        });

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.card_animation);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate();
        animation.start();
        return view;
    }

    private class DeleteComment extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(activity);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_discussion_post_comment_id, str_post_id)
                        .add(AppConstant.TAG_discussion_post_id, str_dis_id)
                        .add(AppConstant.TAG_user_id, str_cid);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.deleteDiscussionPostComment).post(requestBody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);

                if (response.code() == 400) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String json = response.body().string();
                                        JSONObject jsonObject = new JSONObject(json);
                                        Log.e("Delete Comments", "" + jsonObject);

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {
                                            Log.e("Delete", "Successs");
                                        } else {
                                        }
                                    } catch (final JSONException e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final SocketTimeoutException e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                e.printStackTrace();
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final IOException e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                e.printStackTrace();
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final NullPointerException e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, e.getMessage());
                                                alert();
                                            }
                                        });
                                    } catch (final Exception e) {
                                        activity.runOnUiThread(new Runnable() {
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                                    alertDialogBuilder.setTitle(activity.getResources().getString(R.string.error));

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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
