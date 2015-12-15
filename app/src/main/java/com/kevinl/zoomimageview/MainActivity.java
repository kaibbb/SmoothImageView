package com.kevinl.zoomimageview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kevinl.zoomimagevieww.R;

/**
 * Author: liuk
 * Created at: 15/12/15
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter recyclerAdapter;

    private String[] urls = {"http://img.1985t.com/uploads/attaches/2013/03/10471-GAAGE6.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/8435e5dde71190efa7aa1231ca1b9d16fcfa608f.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/6a600c338744ebf84821a4edddf9d72a6159a794.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/6c224f4a20a44623c458fa889c22720e0df3d74e.jpg"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(this, urls);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
