package com.example.swapnil.moneywisely.money;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.Galleyadapter;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Home_Activity extends AppCompatActivity implements View.OnClickListener {

    Typeface typeface;
    TextView tv_notify, tv_search, tv_rate, tv_share, tv_profile, tv_logout, tv_more;
    TextView tv_person, tv_world, tv_commo, tv_rs, tv_receipt, tv_people, tv_video, tv_calender, tv_contact;

    RelativeLayout rl_menu;
    RelativeLayout rl_basic, rl_equity, rl_commodity, rl_famous, rl_personal, rl_social, rl_image, rl_economics, rl_contact;
    LinearLayout lin_profile, lin_logout;
    int status;
    String str_success, str_sliderImage, str_mesg;

    OkHttpClient client;
    Request request;
    ArrayList<String> list;

    SupportUtil support;
    PrefManager manager;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    Timer swipetime;
    Handler handler;
    Runnable update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        list = new ArrayList<String>();
        typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        swipetime = new Timer();

        tv_notify = (TextView) findViewById(R.id.tv_notify);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_rate = (TextView) findViewById(R.id.tv_rate);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_profile = (TextView) findViewById(R.id.tv_profile);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_more = (TextView) findViewById(R.id.tv_more);

        tv_person = (TextView) findViewById(R.id.tv_person);
        tv_world = (TextView) findViewById(R.id.tv_world);
        tv_commo = (TextView) findViewById(R.id.tv_commo);
        tv_rs = (TextView) findViewById(R.id.tv_rs);
        tv_receipt = (TextView) findViewById(R.id.tv_receipt);
        tv_people = (TextView) findViewById(R.id.tv_people);
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_calender = (TextView) findViewById(R.id.tv_calender);
        tv_contact = (TextView) findViewById(R.id.tv_contact);

        rl_basic = (RelativeLayout) findViewById(R.id.rl_basic);
        rl_equity = (RelativeLayout) findViewById(R.id.rl_equity);
        rl_commodity = (RelativeLayout) findViewById(R.id.rl_commodity);
        rl_famous = (RelativeLayout) findViewById(R.id.rl_famous);
        rl_personal = (RelativeLayout) findViewById(R.id.rl_personal);
        rl_social = (RelativeLayout) findViewById(R.id.rl_social);
        rl_image = (RelativeLayout) findViewById(R.id.rl_image);
        rl_economics = (RelativeLayout) findViewById(R.id.rl_economics);
        rl_contact = (RelativeLayout) findViewById(R.id.rl_contact);

        lin_profile = (LinearLayout) findViewById(R.id.lin_profile);
        lin_logout = (LinearLayout) findViewById(R.id.lin_logout);

        tv_notify.setTypeface(typeface);
        tv_search.setTypeface(typeface);
        tv_rate.setTypeface(typeface);
        tv_share.setTypeface(typeface);
        tv_profile.setTypeface(typeface);
        tv_logout.setTypeface(typeface);
        tv_more.setTypeface(typeface);

        tv_person.setTypeface(typeface);
        tv_world.setTypeface(typeface);
        tv_commo.setTypeface(typeface);
        tv_rs.setTypeface(typeface);
        tv_receipt.setTypeface(typeface);
        tv_people.setTypeface(typeface);
        tv_video.setTypeface(typeface);
        tv_calender.setTypeface(typeface);
        tv_contact.setTypeface(typeface);

        rl_basic.setOnClickListener(this);
        rl_equity.setOnClickListener(this);
        rl_commodity.setOnClickListener(this);
        rl_famous.setOnClickListener(this);
        rl_personal.setOnClickListener(this);
        rl_social.setOnClickListener(this);
        rl_image.setOnClickListener(this);
        rl_economics.setOnClickListener(this);
        rl_contact.setOnClickListener(this);

        lin_profile.setOnClickListener(this);
        lin_logout.setOnClickListener(this);

        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        tv_more = (TextView) findViewById(R.id.tv_more);
        tv_more.setVisibility(View.VISIBLE);

        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rl_menu.getVisibility() == View.GONE) {
                    rl_menu.setVisibility(View.VISIBLE);
                    rl_menu.bringToFront();
                } else {
                    rl_menu.setVisibility(View.GONE);
                }
            }
        });

        if (support.checkInternetConnectivity()) {
            new GetSlider().execute();
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

    @Override
    public void onClick(View view) {
        if (view == rl_basic) {
            Intent in = new Intent(getApplicationContext(), BasicInfo_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_equity) {
            Intent in = new Intent(getApplicationContext(), EquityMarket_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_commodity) {
            Intent in = new Intent(getApplicationContext(), CommodityMarket_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_famous) {
            Intent in = new Intent(getApplicationContext(), FamousMMB_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_personal) {
            Intent in = new Intent(getApplicationContext(), PersonalandOther_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_social) {
            Intent in = new Intent(getApplicationContext(), StockMarket_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_image) {
            Intent in = new Intent(getApplicationContext(), Tab_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_economics) {
            Intent in = new Intent(getApplicationContext(), Calender_Activity.class);
            startActivity(in);
            finish();
        } else if (view == rl_contact) {
            Intent in = new Intent(getApplicationContext(), Contact_Activity.class);
            startActivity(in);
            finish();
        } else if (view == lin_profile) {
            Intent in = new Intent(getApplicationContext(), Profile_Activity.class);
            startActivity(in);
            finish();
        } else if (view == lin_logout) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Activity.this);

            alertDialogBuilder.setTitle("Are you sure want to Logout?");

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            manager.logOutUser();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private class GetSlider extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Home_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                RequestBody reqbody = RequestBody.create(null, new byte[0]);

                request = new Request.Builder().url(AppConstant.getSlider).post(reqbody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);
                status = response.code();

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
                                        Log.e("Slider ->", "" + jsonObject);

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {
                                            JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                            for (int i = 0; i < jsondata.length(); i++) {
                                                JSONObject object = jsondata.getJSONObject(i);

                                                str_sliderImage = object.getString(AppConstant.TAG_slider_img);
                                                list.add(str_sliderImage);
                                            }
                                            Gallery();
                                        } else {
                                            str_mesg = jsonObject.getString("mesage");
                                            alert();
                                        }

                                    } catch (final JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                alert();
                                            }
                                        });
                                    } catch (final IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                e.printStackTrace();
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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Activity.this);

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Activity.this);

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

    private void Gallery() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new Galleyadapter(Home_Activity.this, list));

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

        NUM_PAGES = list.size();

        //For Auto Start
        handler = new Handler();
        update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        swipetime.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);

        //pagechanger on indicater

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onPause() {
        swipetime.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        swipetime = new Timer();
        swipetime.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_right);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Activity.this);

        alertDialogBuilder.setTitle("Are You sure want to Exit?");

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent in = new Intent(Intent.ACTION_MAIN);
                        in.addCategory(Intent.CATEGORY_HOME);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
