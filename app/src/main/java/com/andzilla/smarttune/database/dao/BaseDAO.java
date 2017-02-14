package com.andzilla.smarttune.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.andzilla.smarttune.database.PracticeDatabaseHelper;

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
