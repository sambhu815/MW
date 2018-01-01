package com.example.swapnil.moneywisely.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swapnil on 29-07-2017.
 */

public class ContactAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> contactList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    Typeface typeface;

    public ContactAdapter(Activity activity, ArrayList<HashMap<String, String>> contactList) {
        this.activity = activity;
        this.contactList = contactList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactList.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_contact, viewGroup, false);
        typeface = Typeface.createFromAsset(activity.getAssets(), "fontawesome-webfont.ttf");

        TextView tv_icon = (TextView) view.findViewById(R.id.tv_icon);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_info = (TextView) view.findViewById(R.id.tv_info);

        resultp = contactList.get(i);

        tv_icon.setText(resultp.get(AppConstant.TAG_contact_class));
        tv_icon.setTypeface(typeface);
        tv_name.setText(resultp.get(AppConstant.TAG_contact_title));
        tv_info.setText(resultp.get(AppConstant.TAG_contact_desc));

        return view;
    }
}
