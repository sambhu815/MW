package com.example.swapnil.moneywisely.money;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.adapter.CropingOptionAdapter;
import com.example.swapnil.moneywisely.support.AndroidMultiPartEntity;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.CropingOption;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SignUp_Activity extends AppCompatActivity {
    public static final String TAG = SignUp_Activity.class.getSimpleName();

    EditText edt_username, edt_email, edt_phone, edt_password, edt_confirm_password, edt_day, edt_month, edt_year;
    ImageView iv_profile;
    Button btn_signup;
    Toolbar toolbar;
    SupportUtil support;
    PrefManager manager;
    JSONParser parser;

    int status;
    String str_name, str_email, str_phone, str_success, str_pass, str_cpass, str_day, str_month, str_year, str_dob, str_mesg;
    String str_path, str_check;

    OkHttpClient client;
    Request request;
    long totalSize = 0;

    String reslut;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Uri mImageCaptureUri;
    private File outPutFile = null;
    File dir;
    String str_filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        parser = new JSONParser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(in);
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

        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_confirm_password = (EditText) findViewById(R.id.edt_confirm_password);
        edt_day = (EditText) findViewById(R.id.edt_day);
        edt_month = (EditText) findViewById(R.id.edt_month);
        edt_year = (EditText) findViewById(R.id.edt_year);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);

        btn_signup = (Button) findViewById(R.id.btn_signup);

        edt_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp_Activity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String str_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            Date sdfdate = sdf.parse(str_date);
                            str_dob = sdf.format(sdfdate);
                            String[] splite = str_dob.split("/");
                            edt_day.setText(splite[0]);
                            edt_month.setText(splite[1]);
                            edt_year.setText(splite[2]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        edt_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp_Activity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String str_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            Date sdfdate = sdf.parse(str_date);
                            str_dob = sdf.format(sdfdate);
                            String[] splite = str_dob.split("/");
                            edt_day.setText(splite[0]);
                            edt_month.setText(splite[1]);
                            edt_year.setText(splite[2]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        edt_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp_Activity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String str_date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            Date sdfdate = sdf.parse(str_date);
                            str_dob = sdf.format(sdfdate);
                            String[] splite = str_dob.split("/");
                            edt_day.setText(splite[0]);
                            edt_month.setText(splite[1]);
                            edt_year.setText(splite[2]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        str_check = "0";
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_check = "1";
                if (checkpermission()) {
                    selectImageOption();
                } else {
                    requestPermission();
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_name = edt_username.getText().toString().trim();
                str_email = edt_email.getText().toString().trim();
                str_phone = edt_phone.getText().toString().trim();
                str_pass = edt_password.getText().toString().trim();
                str_cpass = edt_confirm_password.getText().toString().trim();
                str_day = edt_day.getText().toString().trim();
                str_month = edt_month.getText().toString().trim();
                str_year = edt_year.getText().toString().trim();

                if (str_name.equals("") || str_name.length() <= 0) {
                    edt_username.setError("Enter Name");
                    edt_username.requestFocus();
                } else if (str_pass.equals("") || str_pass.length() == 0) {
                    edt_password.setError("Enter Password");
                    edt_password.requestFocus();
                } else if (str_pass.length() < 6) {
                    edt_password.setError("Password must be more than 6 character");
                    edt_password.requestFocus();
                } else if (str_day.equals("") || str_day.length() <= 0) {
                    edt_day.setError("Enter Day");
                    edt_day.requestFocus();
                } else if (str_month.equals("") || str_month.length() <= 0) {
                    edt_month.setError("Enter Momth");
                    edt_month.requestFocus();
                } else if (str_year.equals("") || str_year.length() <= 0) {
                    edt_year.setError("Enter Year");
                    edt_year.requestFocus();
                } else if (str_phone.equals("") || str_phone.length() < 10) {
                    edt_phone.setError("Enter Valid Mobile Number.");
                    edt_phone.requestFocus();
                } else {
                    if (str_pass.equals(str_cpass)) {
                        if (support.isValidEmail(str_email) == true) {
                            if (support.checkInternetConnectivity()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        str_dob = str_day + "-" + str_month + "-" + str_year;
//                                        Log.e("Image Path", str_path);
                                        if (str_check.equals("1")) {
                                            new SignUPDP().execute();
                                        } else {
                                            new SignUP().execute();
                                        }
                                    }
                                });
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
                        } else {
                            edt_email.setError("Enter valid E-Mail.");
                            edt_email.requestFocus();
                        }
                    } else {
                        edt_password.setError("Password not match.");
                        edt_password.requestFocus();
                    }
                }
            }
        });
    }

    private class SignUPDP extends AsyncTask<Void, Integer, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(SignUp_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(AppConstant.signup);

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
                entity.addPart(AppConstant.TAG_password, new StringBody(str_pass));
                entity.addPart(AppConstant.TAG_dob, new StringBody(str_dob));

                post.setEntity(entity);
                HttpResponse response = client.execute(post);

                HttpEntity httpEntity = response.getEntity();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    reslut = EntityUtils.toString(httpEntity);

                    try {
                        JSONObject jsonObject = new JSONObject(reslut);

                        str_success = jsonObject.getString(AppConstant.TAG_success);
                        if (str_success.equals("0")) {
                            str_mesg = jsonObject.getString("mesage");
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

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp_Activity.this);
                alertDialogBuilder.setTitle("SignUp Successfully Completed...!");
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent in = new Intent(getApplicationContext(), Login_Activity.class);
                                startActivity(in);
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

    }

    private class SignUP extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(SignUp_Activity.this);
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
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_email, str_email));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_password, str_pass));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_fullname, str_name));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_phone_number, str_phone));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_dob, str_dob));
                        pairs.add(new BasicNameValuePair(AppConstant.TAG_profile_image, ""));

                        JSONObject object = parser.makeHttpRequest(AppConstant.signup, "POST", pairs);
                        Log.e("SignUp", object.toString());

                        try {
                            str_success = object.getString(AppConstant.TAG_success);
                            if (str_success.equals("0")) {
                                str_mesg = object.getString("message");
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

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp_Activity.this);
                alertDialogBuilder.setTitle("SignUp Successfully Completed...!");
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent in = new Intent(getApplicationContext(), Login_Activity.class);
                                startActivity(in);
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp_Activity.this);

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

    private void selectImageOption() {
        if (checkpstorage()) {
            final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignUp_Activity.this);
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
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } else {
            requeststorage();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageOption();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Unable to get Permission", Snackbar.LENGTH_LONG).show();
                }
                break;
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
        Intent in = new Intent(getApplicationContext(), Login_Activity.class);
        startActivity(in);
        finish();
    }
}
