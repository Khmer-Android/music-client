package com.sabaysongs.music.database.dao;

import android.content.Context;
import android.database.Cursor;

import com.sabaysongs.music.model.PlayList;

import java.util.ArrayList;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by phuon on 17-Jan-17.
 */
public class PlayListDAO extends BaseDAO {

    private  static PlayListDAO instance;

    private PlayListDAO(Context context) {
        super(context);
    }



    public  void createTest(){


        PlayList playList = new PlayList("New years songs",2,90);
        PlayList playList1 = new PlayList("new songs ",1,20);

            save(playList);
            save(playList1);

    }


    public static PlayListDAO getInstance(Context context){
        if(instance==null){
            instance = new PlayListDAO(context);
        }
        return instance;
    }


    public ArrayList<PlayList> lists(){

        ArrayList<PlayList> lists = new ArrayList<>();

        Cursor bunnies = cupboard().withDatabase(db).query(PlayList.class).getCursor();
        try {
            // Iterate Bunnys
            QueryResultIterable<PlayList> itr = cupboard().withCursor(bunnies).iterate(PlayList.class);
            for (PlayList playList : itr) {
                lists.add(playList);
            }
        } finally {
            // close the cursor
            bunnies.close();
        }
        return lists;
    }

    public long save(PlayList playList){
        return cupboard().withDatabase(db).put(playList);
    }



}
