package com.example.swapnil.moneywisely.support;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.swapnil.moneywisely.money.Login_Activity;

/**
 * Created by Swapnil.Patel on 8/31/2016.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "money";

    public final String IS_LOGIN = "IsLoggedIn";

    public final String PM_userID = "user_id";
    public final String PM_profile = "profile_image";
    public final String PM_dob = "dob";
    public final String PM_bio = "bio";
    public final String PM_phone = "phone_number";
    public final String PM_email = "email";
    public final String PM_name = "fullname";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLogin(String name, String email, String phone, String dob, String bio, String userID, String profile) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(PM_name, name);
        editor.putString(PM_email, email);
        editor.putString(PM_phone, phone);
        editor.putString(PM_dob, dob);
        editor.putString(PM_bio, bio);
        editor.putString(PM_userID, userID);
        editor.putString(PM_profile, profile);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void logOutUser() {
        editor.clear();
        editor.commit();

        Intent welcome_intent = new Intent(_context, Login_Activity.class);
        welcome_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(welcome_intent);
    }
}
