package com.example.swapnil.moneywisely.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.money.Browser_Activity;
import com.example.swapnil.moneywisely.money.Post_Activity;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swapnil on 25-07-2017.
 */

public class MarketAdapter extends BaseAdapter {
    Activity activity;
    String market;
    ArrayList<HashMap<String, String>> marketList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    String videoId, str_mine_type, str_docname, extention, str_disId, str_companyname;
    private ProgressDialog pDialog;

    public MarketAdapter(Activity activity, ArrayList<HashMap<String, String>> marketList, String market) {
        this.activity = activity;
        this.marketList = marketList;
        this.market = market;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return marketList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_market, viewGroup, false);

        final ImageView grid_image = (ImageView) view.findViewById(R.id.grid_image);
        ImageView iv_play = (ImageView) view.findViewById(R.id.iv_play);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_link = (TextView) view.findViewById(R.id.tv_link);

        ExpandableTextView expandable = (ExpandableTextView) view.findViewById(R.id.expandable);

        resultp = marketList.get(i);

        if (market.equals("equity")) {
            final String str_url = resultp.get(AppConstant.TAG_equitymarket_img);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .fit()
                                        .centerCrop()
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_equitymarket_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_equitymarket_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_equitymarket_title);
                    String str_link = resultp.get(AppConstant.TAG_equitymarket_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "equity");
                    activity.startActivity(in);
                    activity.finish();
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_equitymarket_title);
                    String str_link = resultp.get(AppConstant.TAG_equitymarket_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "equity");
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        } else if (market.equals("calender")) {
            final String str_url = resultp.get(AppConstant.TAG_calendar_img);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_calendar_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_calendar_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_calendar_title);
                    String str_link = resultp.get(AppConstant.TAG_calendar_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "calender");
                    activity.startActivity(in);
                    activity.finish();
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_calendar_title);
                    String str_link = resultp.get(AppConstant.TAG_calendar_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "calender");
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        } else if (market.equals("stock")) {
            final String str_url = resultp.get(AppConstant.TAG_discussion_img);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_discussion_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_discussion_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    str_disId = resultp.get(AppConstant.TAG_discussion_id);
                    str_companyname = resultp.get(AppConstant.TAG_discussion_title);

                    SharedPreferences pref = activity.getSharedPreferences("post", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("DisID", str_disId);
                    editor.putString("company", str_companyname);
                    editor.commit();

                    Intent in = new Intent(activity, Post_Activity.class);
                    activity.startActivity(in);
                    activity.finish();
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    str_disId = resultp.get(AppConstant.TAG_discussion_id);
                    str_companyname = resultp.get(AppConstant.TAG_discussion_title);

                    SharedPreferences pref = activity.getSharedPreferences("post", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("DisID", str_disId);
                    editor.putString("company", str_companyname);
                    editor.commit();

                    Intent in = new Intent(activity, Post_Activity.class);
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        } else if (market.equals("famous")) {
            final String str_url = resultp.get(AppConstant.TAG_famousmmb_img);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_famousmmb_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_famousmmb_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_famousmmb_title);
                    String str_link = resultp.get(AppConstant.TAG_famousmmb_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "famous");
                    activity.startActivity(in);
                    activity.finish();
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_famousmmb_title);
                    String str_link = resultp.get(AppConstant.TAG_famousmmb_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "famous");
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        } else if (market.equals("personal")) {
            final String str_url = resultp.get(AppConstant.TAG_otherlinks_image);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_otherlinks_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_otherlinks_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_otherlinks_title);
                    String str_link = resultp.get(AppConstant.TAG_otherlinks_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "personal");
                    activity.startActivity(in);
                    activity.finish();
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_otherlinks_title);
                    String str_link = resultp.get(AppConstant.TAG_otherlinks_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "personal");
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        } else if (market.equals("basic")) {
            final String str_url = resultp.get(AppConstant.TAG_basicinfo_class);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_basicinfo_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_basicinfo_title));
        } else if (market.equals("docs")) {
            final String str_url = resultp.get(AppConstant.TAG_document_image);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_document_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_document_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    final String filename = resultp.get(AppConstant.TAG_documentlink);

                    String[] temp_ex = filename.split("\\.");
                    extention = temp_ex[temp_ex.length - 1];

                    str_mine_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
                    str_docname = resultp.get(AppConstant.TAG_document_title);

                    File file = Environment.getExternalStorageDirectory();
                    File dir = new File(file.getAbsolutePath() + "/MoneyWisely/Docs/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String name = str_docname + "." + extention;
                    File doc = new File(dir, name);

                    if (!doc.exists()) {
                        new DownloadFileFromURL().execute(filename);
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/MoneyWisely/Docs/" + str_docname + "." + extention);
                                Uri uri = Uri.fromFile(file);

                                Intent in = new Intent(Intent.ACTION_VIEW);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                in.setDataAndType(uri, str_mine_type);
                                try {
                                    activity.startActivity(in);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(activity, "Please install application for open this file document.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    final String filename = resultp.get(AppConstant.TAG_documentlink);

                    String[] temp_ex = filename.split("\\.");
                    extention = temp_ex[temp_ex.length - 1];

                    str_mine_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
                    str_docname = resultp.get(AppConstant.TAG_document_title);

                    File file = Environment.getExternalStorageDirectory();
                    File dir = new File(file.getAbsolutePath() + "/MoneyWisely/Docs/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String name = str_docname + "." + extention;
                    File doc = new File(dir, name);

                    if (!doc.exists()) {
                        new DownloadFileFromURL().execute(filename);
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/MoneyWisely/Docs/" + str_docname + "." + extention);
                                Uri uri = Uri.fromFile(file);

                                Intent in = new Intent(Intent.ACTION_VIEW);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                in.setDataAndType(uri, str_mine_type);
                                try {
                                    activity.startActivity(in);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(activity, "Please install application for open this file document.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
        } else if (market.equals("videos")) {
            tv_title.setText(resultp.get(AppConstant.TAG_video_title));
            tv_link.setText(resultp.get(AppConstant.TAG_videoid));
            String str_vid = resultp.get(AppConstant.TAG_videoid);

            try {
                videoId = extractYoutubeId("http://www.youtube.com/watch?v=" + str_vid);
                final String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";

                Picasso.with(activity)
                        .load(img_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(img_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });

                iv_play.setVisibility(View.VISIBLE);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            final String str_url = resultp.get(AppConstant.TAG_commoditymarket_img);

            if (str_url.isEmpty()) {
                grid_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(activity)
                        .load(str_url)
                        .error(R.mipmap.ic_launcher)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.progress_animation)
                        .into(grid_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(activity)
                                        .load(str_url)
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.drawable.progress_animation)
                                        .into(grid_image);
                            }
                        });
            }
            iv_play.setVisibility(View.GONE);
            expandable.setVisibility(View.VISIBLE);

            expandable.setText(resultp.get(AppConstant.TAG_commoditymarket_desc));
            tv_title.setText(resultp.get(AppConstant.TAG_commoditymarket_title));

            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_commoditymarket_title);
                    String str_link = resultp.get(AppConstant.TAG_commoditymarket_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "commoditymarket");
                    activity.startActivity(in);
                    activity.finish();
                }
            });

            grid_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resultp = marketList.get(i);

                    String str_title = resultp.get(AppConstant.TAG_commoditymarket_title);
                    String str_link = resultp.get(AppConstant.TAG_commoditymarket_link);

                    Intent in = new Intent(activity, Browser_Activity.class);
                    in.putExtra("title", str_title);
                    in.putExtra("link", str_link);
                    in.putExtra("back", "commoditymarket");
                    activity.startActivity(in);
                    activity.finish();
                }
            });
        }
        return view;
    }

    public String extractYoutubeId(String url) throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }

    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        InputStream input;
        OutputStream output;
        URLConnection connection;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
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
                String name = str_docname + "." + extention;
                File doc = new File(dir, name);
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
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/MoneyWisely/Docs/" + str_docname + "." + extention);
                        Uri uri = Uri.fromFile(file);

                        Intent in = new Intent(Intent.ACTION_VIEW);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        in.setDataAndType(uri, str_mine_type);
                        try {
                            activity.startActivity(in);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(activity, "Please install application for open this file document.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}
