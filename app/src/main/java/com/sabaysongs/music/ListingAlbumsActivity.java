package com.sabaysongs.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.sabaysongs.music.adapters.ListingSongRecyclerAdapter;
import com.bumptech.glide.Glide;

public class ListingAlbumsActivity extends AppCompatActivity {

    public static void launch(Context context, String type, String title, int id, String url) {
        Intent intent = new Intent(context, ListingAlbumsActivity.class);
        intent.putExtra("type", type)
                .putExtra("id", id)
                .putExtra("title", title)
                .putExtra("imageUrl", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_albums);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.singer_profile);
        Glide.with(this).load(getIntent().getStringExtra("imageUrl")).into(imageView);
        setTitle(getIntent().getStringExtra("title"));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListingSongRecyclerAdapter(getIntent().getStringExtra("type"), getIntent().getIntExtra("id", 0)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
