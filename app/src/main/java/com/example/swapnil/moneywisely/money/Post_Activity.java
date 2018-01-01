package com.example.swapnil.moneywisely.money;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.CropingOptionAdapter;
import com.example.swapnil.moneywisely.adapter.PostAdapter;
import com.example.swapnil.moneywisely.support.AndroidMultiPartEntity;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.CropingOption;
import com.example.swapnil.moneywisely.support.FileBrowserActivity;
import com.example.swapnil.moneywisely.support.JSONParser;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class Post_Activity extends AppCompatActivity {
    public static final String TAG = Post_Activity.class.getSimpleName();

    Typeface typeface;
    Toolbar toolbar;
    TextView tv_photo, tv_video, tv_doc, tv_filename;

    RecyclerView list_post;
    LinearLayoutManager linearLayoutManager;
    public static int index = -1;
    public static int top = -1;

    EditText edt_post;
    ImageView iv_post;
    RelativeLayout rl_post;

    SupportUtil support;
    PrefManager manager;
    SharedPreferences post, pref;

    OkHttpClient client;
    Request request;

    ArrayList<HashMap<String, String>> postList;
    PostAdapter postAdapter;

    String str_disID, str_companyname;
    String str_success, str_title, str_link, str_mesg, str_path, str_post, str_docname, str_uid, reslut;
    String str_type = "text";

    private final int REQUEST_CODE_PICK_FILE = 2;

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Uri mImageCaptureUri;
    private File outPutFile = null;
    long totalSize = 0;
    ProgressDialog pd;

    File dir;
    String str_filename;
    JSONParser parser;

    ArrayList<Integer> dataLike, Likecount, markoff;
    Parcelable parce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        parser = new JSONParser();
        manager = new PrefManager(this);
        support = new SupportUtil(this);

        postList = new ArrayList<>();
        dataLike = new ArrayList<>();
        Likecount = new ArrayList<>();
        markoff = new ArrayList<>();

        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_uid = pref.getString(manager.PM_userID, null);

        post = getSharedPreferences("post", 0);
        str_disID = post.getString("DisID", null);
        str_companyname = post.getString("company", null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(str_companyname + "' post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StockMarket_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        dir = android.os.Environment.getExternalStorageDirectory();
        dir = new File(dir.getAbsolutePath() + "/MoneyWisely/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        str_filename = "Image-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        outPutFile = new File(dir, str_filename);

        edt_post = (EditText) findViewById(R.id.edt_post);
        iv_post = (ImageView) findViewById(R.id.iv_post);
        rl_post = (RelativeLayout) findViewById(R.id.rl_post);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        list_post = (RecyclerView) findViewById(R.id.list_post);
        list_post.setHasFixedSize(true);
        list_post.setLayoutManager(linearLayoutManager);


        tv_photo = (TextView) findViewById(R.id.tv_photo);
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_doc = (TextView) findViewById(R.id.tv_doc);
        tv_filename = (TextView) findViewById(R.id.tv_filename);

        tv_photo.setTypeface(typeface);
        tv_video.setTypeface(typeface);
        tv_doc.setTypeface(typeface);

        if (support.checkInternetConnectivity()) {
            new GetPost().execute();
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

        tv_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_post.setVisibility(View.VISIBLE);
                tv_filename.setVisibility(View.VISIBLE);
                str_type = "document";

                Intent file = new Intent(FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                        null, getApplicationContext(), FileBrowserActivity.class);
                file.putExtra(FileBrowserActivity.showCannotReadParameter, false);
                startActivityForResult(file, REQUEST_CODE_PICK_FILE);
            }
        });

        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_type = "image";
                str_docname = "";
                iv_post.setVisibility(View.VISIBLE);
                tv_filename.setVisibility(View.GONE);
                selectImageOption();
            }
        });

        rl_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                str_post = edt_post.getText().toString().trim();

                if (support.checkInternetConnectivity()) {
                    if (str_type.equals("text")) {
                        if (str_post.equals("") || str_post.length() <= 0) {
                            edt_post.setError("Enter Something...");
                            edt_post.requestFocus();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AddDiscussionPostMasterText().execute();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AddDiscussionPostMaster().execute();
                            }
                        });
                    }
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        parce = linearLayoutManager.onSaveInstanceState();
        outState.putParcelable("state", parce);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            parce = savedInstanceState.getParcelable("state");
    }

    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Post_Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = android.os.Environment.getExternalStorageDirectory();
                    f = new File(f.getAbsolutePath() + "/MoneyWisely/");
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    str_filename = "Imgae-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    f = new File(f, str_filename);

                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    iv_post.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {
                str_path = data.getStringExtra(FileBrowserActivity.returnFileParameter);
                iv_post.setImageResource(R.drawable.ic_text);
                str_docname = str_path.substring(str_path.lastIndexOf("/") + 1);
                tv_filename.setText(str_docname);
                // Toast.makeText(this, "SElected File Path:\n" + newFile, Toast.LENGTH_SHORT).show();
            } else {
                iv_post.setVisibility(View.GONE);
                Toast.makeText(
                        this,
                        "Received NO result from file browser",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();
            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(mImageCaptureUri, filepath, null, null, null);
            cursor.moveToNext();
            int index = cursor.getColumnIndex(filepath[0]);
            str_path = cursor.getString(index);
            System.out.println("Gallery Image URI : " + str_path);
            CropingIMG();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            str_path = outPutFile.getAbsolutePath();
            System.out.println("Camera Image URI : " + str_path);
            CropingIMG();
        } else if (requestCode == CROPING_CODE) {

            try {
                if (outPutFile.exists()) {
                    Bitmap photo = decodeFile(outPutFile);
                    iv_post.setImageBitmap(photo);

                    str_path = outPutFile.getAbsolutePath();
                    System.out.println("Final Image URI : " + str_path);

                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public void refreshlist() {
        list_post.invalidate();
        list_post.refreshDrawableState();
        postAdapter.notifyDataSetChanged();
    }

    private class GetPost extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Post_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_discussion_id, str_disID)
                        .add(AppConstant.TAG_user_id, str_uid);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.getDiscussionPostMaster).post(requestBody).build();

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
                                        Log.e("getDiscussionPostMaster", "" + jsonObject);

                                        postList.clear();
                                        dataLike.clear();
                                        Likecount.clear();
                                        markoff.clear();
                                        list_post.invalidate();
                                        list_post.refreshDrawableState();

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

                                            postAdapter = new PostAdapter(Post_Activity.this, postList, dataLike, Likecount, markoff);
                                            list_post.setAdapter(postAdapter);
                                            Log.e("dataLike", "" + Likecount);

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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Post_Activity.this);

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Post_Activity.this);

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
        Intent intent = new Intent(getApplicationContext(), StockMarket_Activity.class);
        startActivity(intent);
        finish();
    }

    private class AddDiscussionPostMaster extends AsyncTask<Void, Integer, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Post_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(AppConstant.addDiscussionPostMaster);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(str_path);
                entity.addPart(AppConstant.TAG_image_document_path, new FileBody(sourceFile));
                entity.addPart(AppConstant.TAG_file_name, new StringBody(str_docname));
                entity.addPart(AppConstant.TAG_user_id, new StringBody(str_uid));
                entity.addPart(AppConstant.TAG_type, new StringBody(str_type));
                entity.addPart(AppConstant.TAG_description, new StringBody(str_post));
                entity.addPart(AppConstant.TAG_discussion_id, new StringBody(str_disID));

                post.setEntity(entity);
                HttpResponse response = client.execute(post);

                HttpEntity httpEntity = response.getEntity();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    reslut = EntityUtils.toString(httpEntity);

                    try {
                        JSONObject jsonObject = new JSONObject(reslut);

                        str_success = jsonObject.getString(AppConstant.TAG_success);
                        if (str_success.equals("1")) {
                            str_mesg = jsonObject.getString("message");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    edt_post.setText("");
                                    str_post = "";
                                    tv_filename.setText("");
                                    iv_post.setVisibility(View.GONE);
                                    refresh();
                                }
                            });
                        } else {
                            str_mesg = jsonObject.getString("message");
                            alert();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alert();
                        }
                    });
                }
            } catch (final ClientProtocolException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reslut = e.toString();
                        alert();
                    }
                });
            } catch (final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reslut = e.toString();
                        alert();
                    }
                });
            }
            return reslut;
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

    private void refresh() {
        Intent in = getIntent();
        in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(in);
        new GetPost().execute();
    }

    private class AddDiscussionPostMasterText extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Post_Activity.this);
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
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_user_id, str_uid));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_type, str_type));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_file_name, ""));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_description, str_post));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_discussion_id, str_disID));

                        final JSONObject object = parser.makeHttpRequest(AppConstant.addDiscussionPostMaster, "POST", pairs);
                        Log.e("Responce", object.toString());

                        try {
                            str_success = object.getString(AppConstant.TAG_success);

                            if (str_success.equals("1")) {
                                Log.e("entry", "post entered.......");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            str_mesg = object.getString("message");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                edt_post.setText("");
                                                str_post = "";
                                                dataLike.clear();
                                                Likecount.clear();
                                                markoff.clear();
                                                new GetPost().execute();
                                            }
                                        });
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

    @Override
    protected void onPause() {
        super.onPause();
        index = linearLayoutManager.findFirstVisibleItemPosition();
        View v = list_post.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - list_post.getPaddingTop());
    }

    @Override
    protected void onResume() {
        new GetPost().execute();
        super.onResume();
        if (index != -1) {
            linearLayoutManager.scrollToPositionWithOffset(index, top);
        }
    }
}
