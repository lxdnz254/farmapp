package com.lxdnz.nz.myfarmapp.databases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alex on 25/07/15.
 */
public class PaddockAO extends FarmDbAO {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    private static final String WHERE_ID_EQUALS = FarmDbHandler.COLUMN_PID
            + " =?";
    public static final String PADDOCK_ID_WITH_PREFIX = "paddock.pid";

    public String TAG = "PaddockAO";

    public PaddockAO(Context context) {
        super(context);
    }

    public long addPaddock(Paddock paddock) {

        ContentValues values = new ContentValues();
        values.put(FarmDbHandler.COLUMN_PADDOCKNAME, paddock.getPaddockName());
        values.put(FarmDbHandler.COLUMN_AREA, paddock.getArea());
        values.put(FarmDbHandler.COLUMN_CURRENTCOVERDATE, formatter.format(paddock.getCurrentCoverDate()));
        values.put(FarmDbHandler.COLUMN_CURRENTCOVER, paddock.getCurrentCover());
        values.put(FarmDbHandler.COLUMN_PREVIOUSCOVERDATE, formatter.format(paddock.getPreviousCoverDate()));
        values.put(FarmDbHandler.COLUMN_PREVIOUSCOVER, paddock.getPreviousCover());
        values.put(FarmDbHandler.COLUMN_LASTGRAZED, formatter.format(paddock.getLastGrazed()));
        values.put(FarmDbHandler.COLUMN_GRAZING, paddock.getGrazing());
        values.put(FarmDbHandler.COLUMN_MAPPED, paddock.getMapped());
        values.put(FarmDbHandler.COLUMN_POLYPOINTS, paddock.getPolyPoints());


        return database.insert(FarmDbHandler.TABLE_PADDOCKS, null, values);

    }

    public long updatePaddock(Paddock paddock) {

        ContentValues values = new ContentValues();
        values.put(FarmDbHandler.COLUMN_PADDOCKNAME, paddock.getPaddockName());
        values.put(FarmDbHandler.COLUMN_AREA, paddock.getArea());
        values.put(FarmDbHandler.COLUMN_CURRENTCOVERDATE, formatter.format(paddock.getCurrentCoverDate()));
        values.put(FarmDbHandler.COLUMN_CURRENTCOVER, paddock.getCurrentCover());
        values.put(FarmDbHandler.COLUMN_PREVIOUSCOVERDATE, formatter.format(paddock.getPreviousCoverDate()));
        values.put(FarmDbHandler.COLUMN_PREVIOUSCOVER, paddock.getPreviousCover());
        values.put(FarmDbHandler.COLUMN_LASTGRAZED, formatter.format(paddock.getLastGrazed()));
        values.put(FarmDbHandler.COLUMN_GRAZING, paddock.getGrazing());
        values.put(FarmDbHandler.COLUMN_MAPPED, paddock.getMapped());
        values.put(FarmDbHandler.COLUMN_POLYPOINTS, paddock.getPolyPoints());


        long result = database.update(FarmDbHandler.TABLE_PADDOCKS, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(paddock.getID()) });
        Log.d("Update Result:", "=" + result);
        return result;
    }

    public int deletePaddock(Paddock paddock) {

        return database.delete(FarmDbHandler.TABLE_PADDOCKS, WHERE_ID_EQUALS,
                    new String[] { paddock.getID() + "" });
    }

    public ArrayList<Paddock> getPaddocks() {

        ArrayList<Paddock> paddocks = new ArrayList<Paddock>();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder
                .setTables(FarmDbHandler.TABLE_PADDOCKS

                             );


        Cursor cursor = queryBuilder.query(database, new String[]{
                        PADDOCK_ID_WITH_PREFIX,
                        FarmDbHandler.TABLE_PADDOCKS + "."
                            + FarmDbHandler.COLUMN_PADDOCKNAME,
                        FarmDbHandler.COLUMN_AREA,
                        FarmDbHandler.COLUMN_CURRENTCOVERDATE,
                        FarmDbHandler.COLUMN_CURRENTCOVER,
                        FarmDbHandler.COLUMN_PREVIOUSCOVERDATE,
                        FarmDbHandler.COLUMN_PREVIOUSCOVER,
                        FarmDbHandler.COLUMN_LASTGRAZED,
                        FarmDbHandler.COLUMN_GRAZING,
                        FarmDbHandler.COLUMN_MAPPED,
                        FarmDbHandler.COLUMN_POLYPOINTS
                     }, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Paddock paddock = new Paddock();
            paddock.setID(cursor.getInt(0));
            paddock.setPaddockName(cursor.getString(1));
            paddock.setArea(cursor.getDouble(2));
            try {
                paddock.setCurrentCoverDate(formatter.parse(cursor.getString(3)));
            } catch (ParseException e){
                paddock.setCurrentCoverDate(null);
            }
            paddock.setCurrentCover(cursor.getInt(4));
            try {
                paddock.setPreviousCoverDate(formatter.parse(cursor.getString(5)));
            } catch (ParseException e){
                paddock.setPreviousCoverDate(null);
            }
            paddock.setPreviousCover(cursor.getInt(6));
            try {
                paddock.setLastGrazed(formatter.parse(cursor.getString(7)));
            } catch (ParseException e){
                paddock.setLastGrazed(null);
            }
            paddock.setGrazing(cursor.getInt(8));
            paddock.setMapped(cursor.getInt(9));
            paddock.setPolyPoints(cursor.getString(10));


            paddocks.add(paddock);
        }
        return paddocks;
    }

    public double TotalArea() {
        double areaTotal = 0;
        Cursor cur = database.rawQuery("SELECT SUM(area) FROM paddock", null);
        if(cur.moveToFirst())
        {
            areaTotal = cur.getDouble(0);
        }while (cur.moveToNext());
        return areaTotal;

    }

    public double AverageCover() {
        int coverTotal = 0;
        double averageCover = 0;
        Cursor cur = database.rawQuery("SELECT SUM(currentcover * area) FROM paddock", null);
        if(cur.moveToFirst())
        {coverTotal = cur.getInt(0);}
        while (cur.moveToNext());
        averageCover = (double) coverTotal/TotalArea();
        return averageCover;
    }

    public double FarmGrowth() {
        double farmgrowth;
        int count = 0;
        int diff;
        String currentday;
        String previousday;
        Date currentdayparse = new Date();
        Date previousdayparse = new Date();
        int daydiff;
        double growth;
        double growthtot =0;

        Cursor cur = database.rawQuery("SELECT (currentcover - previouscover) FROM paddock", null);
        Cursor cud = database.rawQuery("SELECT (currentcoverdate) FROM paddock", null);
        Cursor cup = database.rawQuery("SELECT (previouscoverdate) FROM paddock", null);
        cud.moveToFirst();
        cup.moveToFirst();

        if (cur.moveToFirst()) {
            do {
                diff = cur.getInt(0);
             //   Log.i(TAG, String.valueOf(pos) + " paddock row");
             //   Log.i(TAG, String.valueOf(diff) + " cover difference");
                currentday = cud.getString(0)  ;
                previousday = cup.getString(0) ;

                try {currentdayparse = formatter.parse(currentday);
                }catch (ParseException e){
                    Log.e(TAG, "parsing date failed", e);
                                                    }
                try {previousdayparse = formatter.parse(previousday);
                }catch (ParseException e){
                    Log.e(TAG, "parsing date failed", e);
                }

             //   Log.i(TAG, String.valueOf(currentdayparse) + " current day");
             //   Log.i(TAG, String.valueOf(previousdayparse)+ " prev day");
                if (diff > 0) {
                    daydiff = ((int)((currentdayparse.getTime()/(24*60*60*1000)) - (int)(previousdayparse.getTime()/(24*60*60*1000))));
                  //  Log.i(TAG, String.valueOf(daydiff)+ " day difference");
                    growth = ((double) diff / daydiff);
                    count++;
                    growthtot = growthtot + growth;

                 //   Log.i(TAG, String.valueOf(growth) + " growth");
                }
            cud.moveToNext();
                cup.moveToNext();
            } while (cur.moveToNext());
        }
        farmgrowth = growthtot/count;
        return farmgrowth;
    }

}
