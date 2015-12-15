package com.kevinl.zoomimageview;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kevinl.zoomimagevieww.R;

/**
 * Author: liuk
 * Created at: 15/12/15
 */
public class GalleryAdapter extends PagerAdapter {

    private Activity activity;
    private String[] urls;
    private int locationW, locationH, locationX, locationY;
    private int pos;

    public GalleryAdapter(Activity activity, String[] urls, int w, int h, int x, int y, int pos) {
        this.activity = activity;
        this.urls = urls;
        this.locationH = h;
        this.locationW = w;
        this.locationX = x;
        this.locationY = y;
        this.pos = pos;
    }

    @Override
    public int getCount() {
        return urls.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        SmoothImageView smoothImageView = (SmoothImageView) LayoutInflater.from(activity).inflate(R.layout.pager_item, null);
        container.addView(smoothImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        smoothImageView.setOriginalInfo(locationW, locationH, locationX, locationY);
        smoothImageView.transformIn();

        Glide.with(activity)
                .load(urls[position])
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(smoothImageView);

        smoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                }
            }
        });

        smoothImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == pos) {
                    ((SmoothImageView) v).transformOut();
                } else {
                    activity.finish();
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        return smoothImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
