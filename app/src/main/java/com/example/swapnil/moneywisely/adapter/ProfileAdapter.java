package com.example.swapnil.moneywisely.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.money.FullScreenImage_Activity;
import com.example.swapnil.moneywisely.money.Post_Activity;
import com.example.swapnil.moneywisely.money.Post_Comments_Activity;
import com.example.swapnil.moneywisely.money.Profile_Activity;
import com.example.swapnil.moneywisely.money.Profile_viewer_Activity;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.JSONParser;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
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

/**
 * Created by swapnil on 31-07-2017.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.RecyclerViewHolder> {
    public static final String TAG = ProfileAdapter.class.getSimpleName();

    private Context context;
    PrefManager manager;
    SharedPreferences pref, pref_view, pref_image;
    SharedPreferences.Editor editor;
    ArrayList<HashMap<String, String>> postList;
    ArrayList<Integer> dataLike, likecount, markoff;

    public ProfileAdapter(Context context, ArrayList<HashMap<String, String>> postList, ArrayList<Integer> dataLike, ArrayList<Integer> likecount, ArrayList<Integer> markoff) {
        this.context = context;
        this.postList = postList;
        this.dataLike = dataLike;
        this.likecount = likecount;
        this.markoff = markoff;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        // View holder for gridview recycler view as we used in listview

        HashMap<String, String> resultp = new HashMap<String, String>();

        String str_type, str_img, str_postImg, str_post_id, str_cid, str_uid, str_dis_id, str_success, str_mesg, str_name, str_like;
        int position;
        TextView tv_like, tv_mesg;
        ShineButton iv_like;
        ImageView iv_profile, iv_delete, iv_mesg, iv_post, iv_share;
        TextView tv_name, tv_follow_count, tv_post_id, tv_dis_id, tv_type, tv_file, tv_mark;
        ExpandableTextView expandable;
        JSONParser parser;
        LinearLayout lin_mark;

        String str_mine_type, str_docname, extention;
        private ProgressDialog pDialog;

        int like_conut, int_mark;

        public RecyclerViewHolder(View view) {
            super(view);
            // Find all views ids
            parser = new JSONParser();

            iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            iv_mesg = (ImageView) view.findViewById(R.id.iv_mesg);
            iv_share = (ImageView) view.findViewById(R.id.iv_share);

            iv_like = (ShineButton) view.findViewById(R.id.iv_like);
            lin_mark = (LinearLayout) view.findViewById(R.id.lin_mark);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_follow_count = (TextView) view.findViewById(R.id.tv_follow_count);
            tv_post_id = (TextView) view.findViewById(R.id.tv_post_id);
            tv_dis_id = (TextView) view.findViewById(R.id.tv_dis_id);
            tv_type = (TextView) view.findViewById(R.id.tv_type);

            iv_post = (ImageView) view.findViewById(R.id.iv_post);
            tv_file = (TextView) view.findViewById(R.id.tv_file);
            expandable = (ExpandableTextView) view.findViewById(R.id.expandable);

            tv_like = (TextView) view.findViewById(R.id.tv_like);
            tv_mesg = (TextView) view.findViewById(R.id.tv_mesg);
            tv_mark = (TextView) view.findViewById(R.id.tv_mark);
        }
    }

    @Override
    public ProfileAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        ProfileAdapter.RecyclerViewHolder holder = new ProfileAdapter.RecyclerViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        manager = new PrefManager(context);
        pref_image = context.getSharedPreferences("test", 0);
        pref = context.getSharedPreferences(manager.PREF_NAME, 0);
        holder.str_uid = pref.getString(manager.PM_userID, null);

        pref_view = context.getSharedPreferences("view", 0);

        holder.resultp = postList.get(position);

        holder.str_type = holder.resultp.get(AppConstant.TAG_type);
        holder.tv_type.setText(holder.resultp.get(AppConstant.TAG_type));

        holder.str_img = holder.resultp.get(AppConstant.TAG_profile_image);
        if (holder.str_img.equals("null") || holder.str_img == null || holder.str_img.isEmpty()) {
            holder.iv_profile.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context)
                    .load(holder.str_img)
                    .error(R.mipmap.ic_launcher)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.iv_profile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context)
                                    .load(holder.str_img)
                                    .error(R.mipmap.ic_launcher)
                                    .into(holder.iv_profile);
                        }
                    });
        }

        holder.tv_post_id.setText(holder.resultp.get(AppConstant.TAG_discussion_post_id));
        holder.tv_dis_id.setText(holder.resultp.get(AppConstant.TAG_discussion_id));
        holder.tv_name.setText(holder.resultp.get(AppConstant.TAG_fullname));
        holder.tv_follow_count.setText("(" + holder.resultp.get(AppConstant.TAG_follower_count) + " followers)");

        if (holder.str_type.equals("text")) {
            holder.iv_post.setVisibility(View.GONE);
            holder.iv_share.setVisibility(View.VISIBLE);
        } else if (holder.str_type.equals("image")) {
            holder.iv_post.setVisibility(View.VISIBLE);
            holder.iv_share.setVisibility(View.GONE);

            holder.str_postImg = holder.resultp.get(AppConstant.TAG_image_document_path);
            if (holder.str_postImg.equals("null") || holder.str_postImg == null || holder.str_postImg.isEmpty()) {
                holder.iv_post.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(context)
                        .load(holder.str_postImg)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.iv_post, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context)
                                        .load(holder.str_postImg)
                                        .error(R.mipmap.ic_launcher)
                                        .into(holder.iv_post);
                            }
                        });
            }
        } else {
            holder.iv_post.setVisibility(View.VISIBLE);
            holder.iv_share.setVisibility(View.GONE);
            holder.str_postImg = holder.resultp.get(AppConstant.TAG_image_document_path);
            holder.tv_file.setText(holder.resultp.get(AppConstant.TAG_file_name));

            String[] temp_ex = holder.tv_file.getText().toString().split("\\.");
            String ext = temp_ex[temp_ex.length - 1];

            if (ext.equalsIgnoreCase("txt")) {
                holder.iv_post.setImageResource(R.drawable.ic_text);
            } else if (ext.equalsIgnoreCase("pdf")) {
                holder.iv_post.setImageResource(R.drawable.ic_pdf);
            } else if (ext.equalsIgnoreCase("doc")) {
                holder.iv_post.setImageResource(R.drawable.ic_doc);
            } else if (ext.equalsIgnoreCase("png")) {
                holder.iv_post.setImageResource(R.drawable.ic_png);
            } else if (ext.equalsIgnoreCase("jpg")) {
                holder.iv_post.setImageResource(R.drawable.ic_jpg);
            } else {
                holder.iv_post.setImageResource(R.drawable.ic_text);
            }
        }

        holder.expandable.setText(holder.resultp.get(AppConstant.TAG_description));
        holder.tv_mesg.setText(holder.resultp.get(AppConstant.TAG_discussion_post_count));

        holder.tv_like.setText("" + likecount.get(position));

        final int like = dataLike.get(position);
        if (like == 1) {
            holder.iv_like.setChecked(true);
        } else {
            holder.iv_like.setChecked(false);
        }

        holder.int_mark = markoff.get(position);

        if (holder.int_mark == 1) {
            holder.tv_mark.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.lin_mark.setClickable(false);
        } else {
            holder.tv_mark.setTextColor(context.getResources().getColor(R.color.bg_color));
            holder.lin_mark.setClickable(true);
        }

        if (holder.str_type.equals("image")) {
            holder.tv_file.setVisibility(View.GONE);
        } else if (holder.str_type.equals("document")) {
            holder.tv_file.setVisibility(View.VISIBLE);
        } else {
            holder.tv_file.setVisibility(View.GONE);
            holder.iv_post.setVisibility(View.GONE);
        }

        holder.str_cid = holder.resultp.get(AppConstant.TAG_user_id);
        if (holder.str_cid.equals(holder.str_uid)) {
            holder.iv_delete.setVisibility(View.VISIBLE);
        } else {
            holder.iv_delete.setVisibility(View.GONE);
        }

        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.resultp = postList.get(position);

                String str_info = holder.resultp.get(AppConstant.TAG_description);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/html");
                share.putExtra(android.content.Intent.EXTRA_TEXT, "MoneyWisely Sharing \n" + str_info);
                context.startActivity(Intent.createChooser(share, "Share Post"));
            }
        });

        holder.iv_delete.setTag(position);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.resultp = postList.get(position);
                notifyDataSetChanged();
                notifyItemChanged(position);

                holder.str_post_id = holder.resultp.get(AppConstant.TAG_discussion_post_id);
                holder.str_dis_id = holder.resultp.get(AppConstant.TAG_discussion_id);

                holder.position = (int) view.getTag();
                postList.remove(holder.position);
                notifyDataSetChanged();

                new DeleteDiscussionPostMaster().execute();
            }

            class DeleteDiscussionPostMaster extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(final String... strings) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            try {
                                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_post_id, holder.str_post_id));
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_id, holder.str_dis_id));
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, holder.str_uid));

                                JSONObject object = holder.parser.makeHttpRequest(AppConstant.deleteDiscussionPostMaster, "POST", pairs);
                                Log.e("delete Post", object.toString());

                                holder.str_success = object.getString(AppConstant.TAG_success);

                                if (holder.str_success.equals("1")) {
                                    notifyDataSetChanged();
                                    notifyItemChanged(position);
                                    ((Post_Activity) context).refreshlist();
                                } else {
                                    holder.str_mesg = object.getString("mesage");
                                    alert();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return null;
                }
            }

            private void alert() {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle(holder.str_mesg);

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

        holder.iv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.resultp = postList.get(position);

                holder.str_type = holder.resultp.get(AppConstant.TAG_type);
                holder.str_postImg = holder.resultp.get(AppConstant.TAG_image_document_path);
                holder.str_name = holder.resultp.get(AppConstant.TAG_fullname);

                if (holder.str_type.equals("image")) {
                    holder.str_postImg = holder.resultp.get(AppConstant.TAG_image_document_path);

                    if (holder.str_uid.equals(holder.resultp.get(AppConstant.TAG_user_id))) {
                        editor = pref_image.edit();
                        editor.putString("name", holder.str_name + "'s Post");
                        editor.putString("img", holder.str_postImg);
                        editor.putString("back", "profile");
                        editor.commit();

                        Intent i = new Intent(context, FullScreenImage_Activity.class);
                        context.startActivity(i);
                    } else {
                        editor = pref_image.edit();
                        editor.putString("name", holder.str_name + "'s Post");
                        editor.putString("img", holder.str_postImg);
                        editor.putString("back", "profile_view");
                        editor.commit();

                        Intent i = new Intent(context, FullScreenImage_Activity.class);
                        context.startActivity(i);
                    }

                } else if (holder.str_type.equals("document")) {
                    holder.str_docname = holder.tv_file.getText().toString();

                    String[] temp_ex = holder.str_docname.split("\\.");
                    holder.extention = temp_ex[temp_ex.length - 1];

                    holder.str_mine_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(holder.extention);

                    File file = Environment.getExternalStorageDirectory();
                    File dir = new File(file.getAbsolutePath() + "/MoneyWisely/Docs/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File doc = new File(dir, holder.str_docname);

                    if (!doc.exists()) {
                        new DownloadFileFromURL().execute(holder.str_postImg);
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/MoneyWisely/Docs/" + holder.str_docname);
                                Uri uri = Uri.fromFile(file);

                                Intent in = new Intent(Intent.ACTION_VIEW);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                in.setDataAndType(uri, holder.str_mine_type);
                                try {
                                    context.startActivity(in);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(context, "Please install application for open this file document.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }

            class DownloadFileFromURL extends AsyncTask<String, String, String> {
                InputStream input;
                OutputStream output;
                URLConnection connection;

                @Override
                protected void onPreExecute() {
                    holder.pDialog = new ProgressDialog(context);
                    holder.pDialog.setMessage("Downloading file. Please wait...");
                    holder.pDialog.setIndeterminate(true);
                    holder.pDialog.setMax(100);
                    holder.pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    holder.pDialog.setCancelable(true);
                    holder.pDialog.show();
                }

                @Override
                protected String doInBackground(String... strings) {
                    int count;
                    try {
                        URL url = new URL(strings[0]);
                        connection = url.openConnection();
                        connection.connect();

                        int lenght = connection.getContentLength();

                        input = connection.getInputStream();

                        File file = Environment.getExternalStorageDirectory();
                        File dir = new File(file.getAbsolutePath() + "/MoneyWisely/Docs/");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File doc = new File(dir, holder.str_docname);
                        output = new FileOutputStream(doc);

                        byte data[] = new byte[1024];
                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            publishProgress("" + (int) (total * 100) / lenght);
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onProgressUpdate(String... progress) {
                    // setting progress percentage
                    holder.pDialog.setProgress(Integer.parseInt(progress[0]));
                }

                @Override
                protected void onPostExecute(String s) {
                    if (holder.pDialog.isShowing()) {
                        holder.pDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/MoneyWisely/Docs/" + holder.str_docname);
                                Uri uri = Uri.fromFile(file);

                                Intent in = new Intent(Intent.ACTION_VIEW);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                in.setDataAndType(uri, holder.str_mine_type);
                                try {
                                    context.startActivity(in);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(context, "Please install application for open this file document.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.resultp = postList.get(position);

                if (holder.str_uid.equals(holder.resultp.get(AppConstant.TAG_user_id))) {
                    Intent in = new Intent(context, Profile_Activity.class);
                    in.putExtra("name", holder.resultp.get(AppConstant.TAG_fullname));
                    in.putExtra("image", holder.resultp.get(AppConstant.TAG_profile_image));
                    in.putExtra("bio", holder.resultp.get(AppConstant.TAG_bio));
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(in);
                } else {
                    editor = pref_view.edit();
                    editor.putString("name", holder.resultp.get(AppConstant.TAG_fullname));
                    editor.putString("image", holder.resultp.get(AppConstant.TAG_profile_image));
                    editor.putString("bio", holder.resultp.get(AppConstant.TAG_bio));
                    editor.putString("follow", holder.resultp.get(AppConstant.TAG_follow));
                    editor.putString("fuid", holder.resultp.get(AppConstant.TAG_user_id));
                    editor.commit();

                    Intent in = new Intent(context, Profile_viewer_Activity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(in);
                }
            }
        });

        holder.iv_mesg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.resultp = postList.get(position);
                holder.str_post_id = holder.resultp.get(AppConstant.TAG_discussion_post_id);
                holder.str_cid = holder.resultp.get(AppConstant.TAG_user_id);
                holder.str_name = holder.resultp.get(AppConstant.TAG_fullname);

                Intent in = new Intent(context, Post_Comments_Activity.class);
                in.putExtra("id", holder.str_post_id);
                in.putExtra("cid", holder.str_cid);
                in.putExtra("name", holder.str_name);
                context.startActivity(in);
            }
        });

        holder.iv_like.setTag(position);
        holder.iv_like.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                Log.e(TAG, "click " + checked);
                holder.resultp = postList.get(position);
                holder.like_conut = likecount.get(position);

                holder.str_post_id = holder.resultp.get(AppConstant.TAG_discussion_post_id);

                if (checked == true) {
                    holder.iv_like.setChecked(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new addPostLike().execute();
                        }
                    }).start();
                } else {
                    holder.iv_like.setChecked(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new deletePostLike().execute();
                        }
                    }).start();
                }
            }

            class addPostLike extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(final String... strings) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_post_id, holder.str_post_id));
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, holder.str_uid));

                                JSONObject object = holder.parser.makeHttpRequest(AppConstant.addPostLike, "POST", pairs);
                                Log.e("Responce", object.toString());

                                try {
                                    holder.str_success = object.getString(AppConstant.TAG_success);

                                    if (holder.str_success.equals("1")) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                dataLike.remove(position);
                                                dataLike.add(position, 1);

                                                holder.like_conut = holder.like_conut + 1;
                                                likecount.remove(position);
                                                likecount.add(position, holder.like_conut);
                                                holder.tv_like.setText("" + holder.like_conut);
                                            }
                                        });
                                    } else {
                                        holder.str_mesg = object.getString("mesage");
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
            }

            class deletePostLike extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(final String... strings) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_post_id, holder.str_post_id));
                                pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, holder.str_uid));

                                JSONObject object = holder.parser.makeHttpRequest(AppConstant.deletePostLike, "POST", pairs);
                                Log.e("Responce", object.toString());

                                try {
                                    holder.str_success = object.getString(AppConstant.TAG_success);

                                    if (holder.str_success.equals("1")) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                dataLike.remove(position);
                                                dataLike.add(position, 0);

                                                holder.like_conut = holder.like_conut - 1;
                                                likecount.remove(position);
                                                likecount.add(position, holder.like_conut);
                                                holder.tv_like.setText("" + holder.like_conut);
                                            }
                                        });
                                    } else {
                                        holder.str_mesg = object.getString("mesage");
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
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    notifyDataSetChanged();
                    notifyItemChanged(position);
                }
            }

            private void alert() {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle(holder.str_mesg);

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

        if (holder.int_mark == 0) {
            holder.lin_mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.resultp = postList.get(position);
                    holder.int_mark = markoff.get(position);

                    holder.str_post_id = holder.resultp.get(AppConstant.TAG_discussion_post_id);
                    holder.tv_mark.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new addMarkOffensive().execute();
                        }
                    }).start();

                }

                class addMarkOffensive extends AsyncTask<String, String, String> {

                    @Override
                    protected String doInBackground(final String... strings) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                try {
                                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                                    pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_post_id, holder.str_post_id));

                                    JSONObject object = holder.parser.makeHttpRequest(AppConstant.addMarkOffensive, "POST", pairs);
                                    Log.e("Responce", object.toString());

                                    try {
                                        holder.str_success = object.getString(AppConstant.TAG_success);

                                        if (holder.str_success.equals("1")) {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    markoff.remove(position);
                                                    markoff.add(position, 1);
                                                    holder.lin_mark.setClickable(false);
                                                }
                                            });
                                        } else {
                                            holder.str_mesg = object.getString("message");
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
                }

                private void alert() {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle(holder.str_mesg);

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

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
