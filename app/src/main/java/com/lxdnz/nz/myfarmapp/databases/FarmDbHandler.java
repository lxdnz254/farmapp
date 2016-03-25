package com.lxdnz.nz.myfarmapp.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alex on 24/07/15.
 */
public class FarmDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "farmDB.db";

    public static final String TABLE_STOCK = "stock";
    public static final String TABLE_PADDOCKS = "paddock";

    // stock table
    public static final String COLUMN_SID = "sid";
    public static final String COLUMN_STOCKNAME = "stockname";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_DAILYFEED = "dailyfeed";
    public static final String COLUMN_SUPPLEMENT = "supplement";
    public static final String COLUMN_SUPKG = "supKg";
    public static final String COLUMN_GRASSKG = "grassKg";
    public static final String COLUMN_AREAUSING = "areausing";
    public static final String COLUMN_PADDOCK_IN = "paddockin";

    public static final String CREATE_STOCK_TABLE = "CREATE TABLE " +
            TABLE_STOCK + "("
            + COLUMN_SID + " INTEGER PRIMARY KEY," + COLUMN_STOCKNAME
            + " TEXT, " + COLUMN_QUANTITY + " INT, " +
            COLUMN_DAILYFEED + " DOUBLE, " + COLUMN_SUPPLEMENT + " INT, "
            + COLUMN_SUPKG + " DOUBLE, " + COLUMN_GRASSKG + " DOUBLE, " +
            COLUMN_AREAUSING + " DOUBLE, " +
            COLUMN_PADDOCK_IN + " INT, " + "FOREIGN KEY("+ COLUMN_PADDOCK_IN +
            ") REFERENCES " + TABLE_PADDOCKS + "(pid) " + ")";


    //paddock table
    public static final String COLUMN_PID = "pid";
    public static final String COLUMN_PADDOCKNAME = "paddockname";
    public static final String COLUMN_AREA = "area";
    public static final String COLUMN_CURRENTCOVERDATE = "currentcoverdate";
    public static final String COLUMN_CURRENTCOVER = "currentcover";
    public static final String COLUMN_PREVIOUSCOVERDATE = "previouscoverdate";
    public static final String COLUMN_PREVIOUSCOVER = "previouscover";
    public static final String COLUMN_LASTGRAZED = "lastgrazed";
    public static final String COLUMN_GRAZING = "grazing";
    public static final String COLUMN_MAPPED = "mapped";
    public static final String COLUMN_POLYPOINTS = "polypoints";


    public static final String CREATE_PADDOCKS_TABLE = "CREATE TABLE " +
            TABLE_PADDOCKS + "("
            + COLUMN_PID + " INTEGER PRIMARY KEY," + COLUMN_PADDOCKNAME
            + " TEXT," + COLUMN_AREA + " INTEGER," + COLUMN_CURRENTCOVERDATE
            + " DATE," + COLUMN_CURRENTCOVER + " INT," + COLUMN_PREVIOUSCOVERDATE
            + " DATE," + COLUMN_PREVIOUSCOVER + " INT," + COLUMN_LASTGRAZED
            + " DATE," + COLUMN_GRAZING + " INT," + COLUMN_MAPPED + " INT,"
            + COLUMN_POLYPOINTS + " TEXT" + ")";


    public static FarmDbHandler instance;

    public static synchronized FarmDbHandler getHelper(Context context) {
        if (instance == null)
            instance = new FarmDbHandler(context);
        return instance;
    }

    public FarmDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(CREATE_STOCK_TABLE);
        db.execSQL(CREATE_PADDOCKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
    }

}