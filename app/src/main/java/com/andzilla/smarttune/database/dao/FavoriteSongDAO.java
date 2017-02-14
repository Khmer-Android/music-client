package com.andzilla.smarttune.database.dao;

import android.content.Context;
import android.database.Cursor;

import com.andzilla.smarttune.model.FavoriteSongs;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by phuon on 17-Jan-17.
 */
public class FavoriteSongDAO extends  BaseDAO {

    private static FavoriteSongDAO instance;
    private Context context;

    private FavoriteSongDAO(Context context) {
        super(context);
    }

    public static FavoriteSongDAO  getInstance(Context context){

        if(instance==null){
            instance = new FavoriteSongDAO(context);
        }
        return instance;
    }


    public long saveFavorite(FavoriteSongs favoriteSongs) {
        return cupboard().withDatabase(db).put(favoriteSongs);
    }

    public ArrayList<FavoriteSongs> lists(){

        ArrayList<FavoriteSongs> lists = new ArrayList<>();

        Cursor bunnies = cupboard().withDatabase(db).query(FavoriteSongs.class).getCursor();
        try {
            // Iterate Bunnys
            QueryResultIterable<FavoriteSongs> itr = cupboard().withCursor(bunnies).iterate(FavoriteSongs.class);
            for (FavoriteSongs favoriteSong : itr) {
                lists.add(favoriteSong);
            }
        } finally {
            // close the cursor
            bunnies.close();
        }
        return lists;
    }




}
