package com.example.swapnil.moneywisely.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> imageList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ImageAdapter(Activity activity, ArrayList<HashMap<String, String>> imageList) {
        this.activity = activity;
        this.imageList = imageList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(activity);
            grid = inflater.inflate(R.layout.row_images, null);

            TextView tv_id = (TextView) grid.findViewById(R.id.tv_id);
            TextView tv_title = (TextView) grid.findViewById(R.id.tv_title);
            TextView tv_img = (TextView) grid.findViewById(R.id.tv_img);

            final ImageView grid_image = (ImageView) grid.findViewById(R.id.grid_image);

            resultp = imageList.get(position);

            final String str_url = resultp.get(AppConstant.TAG_image_img);

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

            tv_title.setText(resultp.get(AppConstant.TAG_image_title));
            tv_id.setText(resultp.get(AppConstant.TAG_imagesid));
            tv_img.setText(resultp.get(AppConstant.TAG_image_img));
        } else {
            grid = (View) convertView;
        }

        return grid;
    }

}
