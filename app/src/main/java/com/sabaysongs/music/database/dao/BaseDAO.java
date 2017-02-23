package com.sabaysongs.music.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sabaysongs.music.database.PracticeDatabaseHelper;

/**
 * Created by phuon on 17-Jan-17.
 */
public class BaseDAO {

    protected  SQLiteDatabase db = null;

    public BaseDAO(Context context) {
        PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(context);
      if(db==null) {
          db = dbHelper.getWritableDatabase();
      }

    }

}
