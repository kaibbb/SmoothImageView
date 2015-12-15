package com.kevinl.smoothimageview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Author: liuk
 * Created at: 15/12/15
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    private Activity activity;
    private String[] urls;

    public RecyclerAdapter(Activity activity, String[] urls) {
        this.activity = activity;
        this.urls = urls;
    }

    @Override
    public RecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.recycler_item, parent, false);
        Holder viewHolder = new Holder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.Holder holder, final int position) {
        Glide.with(activity)
                .load(urls[position])
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Rect frame = new Rect();
                    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    int statusBarHeight = frame.top;
                    v.getLocationOnScreen(location);
                    location[1] += statusBarHeight;
                } else {
                    v.getLocationOnScreen(location);
                }
                v.invalidate();
                int width = v.getWidth();
                int height = v.getHeight();

                Intent intent = new Intent(activity, GalleryActivity.class);
                Bundle b = new Bundle();
                b.putStringArray(GalleryActivity.PHOTO_SOURCE_ID, urls);
                intent.putExtras(b);
                intent.putExtra(GalleryActivity.PHOTO_SELECT_POSITION, position);
                intent.putExtra(GalleryActivity.PHOTO_SELECT_X_TAG, location[0]);
                intent.putExtra(GalleryActivity.PHOTO_SELECT_Y_TAG, location[1]);
                intent.putExtra(GalleryActivity.PHOTO_SELECT_W_TAG, width);
                intent.putExtra(GalleryActivity.PHOTO_SELECT_H_TAG, height);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.length;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public Holder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv);
        }
    }
}
