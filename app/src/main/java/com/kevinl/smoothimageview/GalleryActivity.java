package com.kevinl.smoothimageview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kevinl.zoomimagevieww.R;

/**
 * Author: liuk
 * Created at: 15/12/15
 */
public class GalleryActivity extends AppCompatActivity {

    public static final String PHOTO_SOURCE_ID = "PHOTO_SOURCE_ID";
    public static final String PHOTO_SELECT_POSITION = "PHOTO_SELECT_POSITION";
    public static final String PHOTO_SELECT_X_TAG = "PHOTO_SELECT_X_TAG";
    public static final String PHOTO_SELECT_Y_TAG = "PHOTO_SELECT_Y_TAG";
    public static final String PHOTO_SELECT_W_TAG = "PHOTO_SELECT_W_TAG";
    public static final String PHOTO_SELECT_H_TAG = "PHOTO_SELECT_H_TAG";

    private int locationX;
    private int locationY;
    private int locationW;
    private int locationH;
    private int position;
    private String[] urls;

    private ViewPager viewPager;
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle b = intent.getExtras();
            urls = b.getStringArray(PHOTO_SOURCE_ID);

            position = intent.getIntExtra(PHOTO_SELECT_POSITION, 0);
            locationX = intent.getIntExtra(PHOTO_SELECT_X_TAG, 0);
            locationY = intent.getIntExtra(PHOTO_SELECT_Y_TAG, 0);
            locationW = intent.getIntExtra(PHOTO_SELECT_W_TAG, 0);
            locationH = intent.getIntExtra(PHOTO_SELECT_H_TAG, 0);
        }

        viewPager = (ViewPager) findViewById(R.id.vp);
        galleryAdapter = new GalleryAdapter(this, urls, locationW, locationH, locationX, locationY, position);
        viewPager.setAdapter(galleryAdapter);
        viewPager.setCurrentItem(position);
    }
}
