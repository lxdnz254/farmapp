package com.lxdnz.nz.myfarmapp.databases;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by alex on 28/07/15.
 */
public class FarmDbAO {

    protected SQLiteDatabase database;
    private FarmDbHandler dbHelper;
    private Context mContext;

    public FarmDbAO(Context context) {
        this.mContext = context;
        dbHelper = FarmDbHandler.getHelper(mContext);
        open();

    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = FarmDbHandler.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }
}
