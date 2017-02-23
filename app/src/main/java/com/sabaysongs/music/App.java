package com.sabaysongs.music;

import android.app.Application;

import com.sabaysongs.music.model.FavoriteSongs;
import com.sabaysongs.music.model.PlayList;
import com.bumptech.glide.Glide;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by phuon on 17-Jan-17.
 */
public class App extends Application {
    static {
        cupboard().register(FavoriteSongs.class);
        cupboard().register(PlayList.class);


    }

    private static App sInstance;

    public App() {
        sInstance = this;
    }

    public static synchronized App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Glide.get(this).trimMemory(level);
    }




}
