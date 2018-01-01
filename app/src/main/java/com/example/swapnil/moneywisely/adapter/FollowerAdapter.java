package com.example.swapnil.moneywisely.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.swapnil.moneywisely.R;
import com.example.swapnil.moneywisely.money.Profile_viewer_Activity;
import com.example.swapnil.moneywisely.support.AppConstant;
import com.example.swapnil.moneywisely.support.PrefManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Swapnil.Patel on 07-09-2017.
 */

public class FollowerAdapter extends BaseAdapter {
    public static final String TAG = FollowerAdapter.class.getSimpleName();

    Activity activity;
    ArrayList<HashMap<String, String>> followerList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();

    SharedPreferences pref, pref_follow;
    SharedPreferences.Editor editor;
    PrefManager manager;

    public FollowerAdapter(Activity activity, ArrayList<HashMap<String, String>> followerList) {
        this.activity = activity;
        this.followerList = followerList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return followerList.size();
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
        view = inflater.inflate(R.layout.row_followers, viewGroup, false);

        manager = new PrefManager(activity);
        pref = activity.getSharedPreferences(manager.PREF_NAME, 0);
        pref_follow = activity.getSharedPreferences("view", 0);

        final ImageView iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
        ImageView iv_done = (ImageView) view.findViewById(R.id.iv_done);

        TextView tv_uid = (TextView) view.findViewById(R.id.tv_uid);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_follow_count = (TextView) view.findViewById(R.id.tv_follow_count);
        TextView tv_bio = (TextView) view.findViewById(R.id.tv_bio);

        RelativeLayout rl_follow = (RelativeLayout) view.findViewById(R.id.rl_follow);

        resultp = followerList.get(i);

        final String str_img = resultp.get(AppConstant.TAG_profile_image);
        if (str_img.equals("null") || str_img == null || str_img.isEmpty()) {
            iv_profile.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(activity)
                    .load(str_img)
                    .error(R.mipmap.ic_launcher)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.progress_animation)
                    .into(iv_profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(str_img)
                                    .error(R.mipmap.ic_launcher)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(iv_profile);
                        }
                    });
        }

        String str_follow = resultp.get(AppConstant.TAG_follow);
        if (str_follow.equals("1") || str_follow.equals(1)) {
            iv_done.setVisibility(View.VISIBLE);
        } else {
            iv_done.setVisibility(View.GONE);
        }

        tv_uid.setText(resultp.get(AppConstant.TAG_user_id));
        tv_name.setText(resultp.get(AppConstant.TAG_fullname));
        tv_follow_count.setText("(" + resultp.get(AppConstant.TAG_followers_count) + " Followers)");
        tv_bio.setText(resultp.get(AppConstant.TAG_bio));

        rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = followerList.get(i);

                editor = pref_follow.edit();
                editor.putString("name", resultp.get(AppConstant.TAG_fullname));
                editor.putString("image", resultp.get(AppConstant.TAG_profile_image));
                editor.putString("bio", resultp.get(AppConstant.TAG_bio));
                editor.putString("follow", resultp.get(AppConstant.TAG_follow));
                editor.putString("fuid", resultp.get(AppConstant.TAG_user_id));
                editor.putString("post", "follow");
                editor.commit();

                Intent in = new Intent(activity, Profile_viewer_Activity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(in);
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = followerList.get(i);

                editor = pref_follow.edit();
                editor.putString("name", resultp.get(AppConstant.TAG_fullname));
                editor.putString("image", resultp.get(AppConstant.TAG_profile_image));
                editor.putString("bio", resultp.get(AppConstant.TAG_bio));
                editor.putString("follow", resultp.get(AppConstant.TAG_follow));
                editor.putString("fuid", resultp.get(AppConstant.TAG_user_id));
                editor.putString("post", "follow");
                editor.commit();

                Intent in = new Intent(activity, Profile_viewer_Activity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(in);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.card_animation);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.animate();
        animation.start();
        return view;
    }
}
