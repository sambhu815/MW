package com.example.swapnil.moneywisely.money;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.example.swapnil.moneywisely.support.SupportUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login_Activity extends AppCompatActivity {
    public static final String TAG = Login_Activity.class.getSimpleName();

    EditText edt_username, edt_password;
    Button btn_login;
    TextView tv_change_pass;
    RelativeLayout rl_signup;
    Switch switch_login;

    SupportUtil support;
    PrefManager manager;
    SharedPreferences pref, login_pref;
    SharedPreferences.Editor editor;

    OkHttpClient client;
    Request request;

    String str_name, str_pass, str_success, str_email, str_phone, str_dob, str_bio, str_path, str_userID, str_mesg;
    int status;
    boolean bool_check;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    String[] permissionsRequired = new String[]{Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        manager = new PrefManager(this);
        pref = getSharedPreferences(manager.PREF_NAME, 0);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        CheckPermission();

        login_pref = getSharedPreferences("save", 0);
        editor = login_pref.edit();

        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_username = (EditText) findViewById(R.id.edt_username);

        btn_login = (Button) findViewById(R.id.btn_login);
        switch_login = (Switch) findViewById(R.id.switch_login);

        tv_change_pass = (TextView) findViewById(R.id.tv_change_pass);

        rl_signup = (RelativeLayout) findViewById(R.id.rl_signup);

        str_name = login_pref.getString("name", null);
        str_pass = login_pref.getString("pass", null);
        bool_check = login_pref.getBoolean("check", false);

        edt_username.setText(str_name);
        edt_password.setText(str_pass);

        if (bool_check == true) {
            switch_login.setChecked(true);
        } else {
            switch_login.setChecked(false);
        }

        rl_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), SignUp_Activity.class);
                startActivity(in);
                finish();
            }
        });

        tv_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                str_name = edt_username.getText().toString().trim();
                str_pass = edt_password.getText().toString().trim();

                if (switch_login.isChecked()) {
                    editor.putString("name", str_name);
                    editor.putString("pass", str_pass);
                    editor.putBoolean("check", true);
                    editor.commit();
                } else {
                    editor.putString("name", "");
                    editor.putString("pass", "");
                    editor.putBoolean("check", false);
                    editor.commit();
                }
                if (str_pass.equals("") || str_pass.length() == 0) {
                    edt_password.setError("Enter Password");
                    edt_password.requestFocus();
                } else if (str_pass.length() < 6) {
                    edt_password.setError("Password must be more than 6 character");
                    edt_password.requestFocus();
                } else {
                    if (support.isValidEmail(str_name) == true) {
                        if (support.checkInternetConnectivity()) {
                            new callLogin().execute();
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
                        edt_username.setError("Enter valid E-Mail.");
                        edt_username.requestFocus();
                    }
                }
            }
        });
    }

    private class callLogin extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(Login_Activity.this);
            pd.setMessage("Loading, Please Wait.....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();

                FormBody.Builder builder = new FormBody.Builder()
                        .add(AppConstant.TAG_email, str_name)
                        .add(AppConstant.TAG_password, str_pass);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.login).post(requestBody).build();

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
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null) {
                            try {
                                String json = response.body().string();
                                JSONObject jsonObject = new JSONObject(json);
                                Log.e("Login User ->", "" + jsonObject);

                                str_success = jsonObject.getString(AppConstant.TAG_success);

                                if (str_success.equals("1")) {

                                    JSONObject jsondata = jsonObject.getJSONObject(AppConstant.TAG_data);

                                    str_userID = jsondata.getString(AppConstant.TAG_user_id);
                                    str_name = jsondata.getString(AppConstant.TAG_fullname);
                                    str_email = jsondata.getString(AppConstant.TAG_email);
                                    str_phone = jsondata.getString(AppConstant.TAG_phone_number);
                                    str_dob = jsondata.getString(AppConstant.TAG_dob);
                                    str_bio = jsondata.getString(AppConstant.TAG_bio);
                                    str_path = jsondata.getString(AppConstant.TAG_profile_image);

                                    manager.createLogin(str_name, str_email, str_phone, str_dob, str_bio, str_userID, str_path);

                                    Intent in = new Intent(getApplicationContext(), Home_Activity.class);
                                    startActivity(in);
                                    finish();
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
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_Activity.this);

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_Activity.this);

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

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[4])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                builder.setTitle("Need Multiple Permissions.");
                // builder.setMessage(str_per_mesg);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Login_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                builder.setTitle("Need Multiple Permissions.");
                //  builder.setMessage(str_per_mesg);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        //  Toast.makeText(getBaseContext(), "Go to Permissions to Grant Phone and Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(Login_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            proceedAfterPermission();
        }

    }

    private void proceedAfterPermission() {
        // Toast.makeText(getBaseContext(), "Permissions Granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login_Activity.this, permissionsRequired[4])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                builder.setTitle("Need Multiple Permissions.");
                // builder.setMessage(str_per_mesg);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Login_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(Login_Activity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }
}
