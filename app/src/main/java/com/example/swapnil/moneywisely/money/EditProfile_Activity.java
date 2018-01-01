package com.example.swapnil.moneywisely.money;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.CropingOptionAdapter;
import com.example.swapnil.moneywisely.support.AndroidMultiPartEntity;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.CropingOption;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfile_Activity extends AppCompatActivity {
    public static final String TAG = EditProfile_Activity.class.getSimpleName();
    Typeface typeface;

    Toolbar toolbar;

    SharedPreferences pref;
    PrefManager manager;
    SupportUtil support;

    OkHttpClient client;
    Request request;

    CircleImageView iv_profile;
    EditText edt_name, edt_mail, edt_phone, edt_dob, edt_change_email, edt_password, edt_new_password;
    Button btn_update, btn_change;
    TextView tv_icon;

    String str_name, str_email, str_profile, str_dob, str_bio, str_uid, str_phone, str_cpass, str_newpass;
    String str_success, str_mesg;

    long totalSize = 0;

    String reslut;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Uri mImageCaptureUri;
    private File outPutFile = null;
    File dir;
    String str_filename, str_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);

        str_name = pref.getString(manager.PM_name, null);
        str_email = pref.getString(manager.PM_email, null);
        str_profile = pref.getString(manager.PM_profile, null);
        str_dob = pref.getString(manager.PM_dob, null);
        str_bio = pref.getString(manager.PM_bio, null);
        str_uid = pref.getString(manager.PM_userID, null);
        str_phone = pref.getString(manager.PM_phone, null);

        dir = android.os.Environment.getExternalStorageDirectory();
        dir = new File(dir.getAbsolutePath() + "/MoneyWisely/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        str_filename = "Image-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        outPutFile = new File(dir, str_filename);

        iv_profile = (CircleImageView) findViewById(R.id.iv_profile);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_mail = (EditText) findViewById(R.id.edt_email);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_dob = (EditText) findViewById(R.id.edt_dob);
        edt_change_email = (EditText) findViewById(R.id.edt_change_email);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_new_password = (EditText) findViewById(R.id.edt_new_password);

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_change = (Button) findViewById(R.id.btn_change);

        tv_icon = (TextView) findViewById(R.id.tv_icon);
        tv_icon.setTypeface(typeface);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    .into(iv_profile);
        }

        edt_name.setText(str_name);
        edt_mail.setText(str_email);
        edt_change_email.setText(str_email);
        edt_phone.setText(str_phone);
        edt_dob.setText(str_dob);

        edt_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile_Activity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String str_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                        try {
                            Date sdfdate = sdf.parse(str_date);
                            str_dob = sdf.format(sdfdate);
                            edt_dob.setText(str_dob);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkpermission()) {
                    selectImageOption();
                } else {
                    requestPermission();
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_name = edt_name.getText().toString();
                str_phone = edt_phone.getText().toString();
                str_dob = edt_dob.getText().toString();

                if (str_name.equals("") || str_name.length() == 0) {
                    edt_name.setError("Enter Name");
                    edt_name.requestFocus();
                } else if (str_phone.equals("") || str_phone.length() == 0) {
                    edt_phone.setError("Enter Phone Number");
                    edt_phone.requestFocus();
                } else if (str_dob.equals("") || str_dob.length() == 0) {
                    edt_dob.setError("Enter Date of Birth");
                    edt_dob.requestFocus();
                } else {
                    if (support.isValidMobile(str_phone) == true) {
                        if (support.checkInternetConnectivity()) {
                            new UpdateProfile().execute();
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
                    } else {
                        edt_phone.setError("Enter Valid  Phone Number");
                        edt_phone.requestFocus();
                    }
                }
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_email = edt_change_email.getText().toString();
                str_cpass = edt_password.getText().toString();
                str_newpass = edt_new_password.getText().toString();

                if (str_cpass.equals("") || str_cpass.length() == 0) {
                    edt_password.setError("Enter Password");
                    edt_password.requestFocus();
                } else if (str_newpass.equals("") || str_newpass.length() == 0) {
                    edt_new_password.setError("Enter Password");
                    edt_new_password.requestFocus();
                } else if (str_cpass.length() < 6) {
                    edt_password.setError("Password must be more than 6 character");
                    edt_password.requestFocus();
                } else if (str_newpass.length() < 6) {
                    edt_new_password.setError("Password must be more than 6 character");
                    edt_new_password.requestFocus();
                } else {
                    if (support.checkInternetConnectivity()) {
                        new changePassword().execute();
                    } else {
                        Snackbar.make(view, "Please Check your Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Setting", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(
                                                new Intent(Settings.ACTION_SETTINGS));
                                    }
                                }).show();
                    }

                }
            }
        });
    }

    private class changePassword extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(EditProfile_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();

                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_phone_number_Email, str_email)
                        .add(AppConstant.TAG_password, str_cpass)
                        .add(AppConstant.TAG_new_passwrod, str_newpass);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.changePassword).post(requestBody).build();

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
                                        Log.e("changePassword ->", "" + jsonObject);

                                        str_success = jsonObject.getString(AppConstant.TAG_success);

                                        if (str_success.equals("1")) {
                                            str_mesg = jsonObject.getString("message");
                                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfile_Activity.this);

                                            alertDialogBuilder.setTitle(str_mesg);

                                            alertDialogBuilder
                                                    .setCancelable(true)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            manager.logOutUser();
                                                            finish();
                                                        }
                                                    });
                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
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
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfile_Activity.this);

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

    private class UpdateProfile extends AsyncTask<Void, Integer, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(EditProfile_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(AppConstant.editUser);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(str_path);
                entity.addPart(AppConstant.TAG_profile_image, new FileBody(sourceFile));
                entity.addPart(AppConstant.TAG_fullname, new StringBody(str_name));
                entity.addPart(AppConstant.TAG_email, new StringBody(str_email));
                entity.addPart(AppConstant.TAG_phone_number, new StringBody(str_phone));
                entity.addPart(AppConstant.TAG_dob, new StringBody(str_dob));
                entity.addPart(AppConstant.TAG_user_id, new StringBody(str_uid));

                post.setEntity(entity);
                HttpResponse response = client.execute(post);

                HttpEntity httpEntity = response.getEntity();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    reslut = EntityUtils.toString(httpEntity);

                    try {
                        final JSONObject jsonObject = new JSONObject(reslut);

                        str_success = jsonObject.getString(AppConstant.TAG_success);
                        str_mesg = jsonObject.getString("message");
                        if (str_success.equals("1")) {

                            JSONObject object = jsonObject.getJSONObject(AppConstant.TAG_data);
                            Log.e("DAta", "" + object.toString());
                            str_name = object.getString(AppConstant.TAG_fullname);
                            str_dob = object.getString(AppConstant.TAG_dob);
                            str_path = object.getString(AppConstant.TAG_profile_image);
                            str_phone = object.getString(AppConstant.TAG_phone_number);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfile_Activity.this);
                                    alertDialogBuilder.setTitle(str_mesg);
                                    alertDialogBuilder
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    manager.createLogin(str_name, str_email, str_phone, str_dob, str_bio, str_uid, str_path);
                                                    Intent in = new Intent(getApplicationContext(), Profile_Activity.class);
                                                    startActivity(in);
                                                    finish();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        str_mesg = jsonObject.getString("mesage");
                                        alert();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfile_Activity.this);

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

    private boolean checkpstorage() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requeststorage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkpermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void selectImageOption() {
        if (checkpstorage()) {
            final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditProfile_Activity.this);
            builder.setTitle("Change Photo!");
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
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } else {
            requeststorage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

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
                    iv_profile.setImageBitmap(photo);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
