package com.lxdnz.nz.myfarmapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;

import com.lxdnz.nz.myfarmapp.databases.Stock;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;

/**
 * Created by alex on 28/07/15.
 */
public class StockAO extends FarmDbAO {

    public static final String STOCK_ID_WITH_PREFIX = "stock.sid";
  //  public static final String STOCK_NAME_WITH_PREFIX = "stock.stockname";
  //  public static final String PADDOCK_NAME_WITH_PREFIX = "paddock.paddockname";

    public PaddockAO paddockAO;
    private String TAG = "StockAO";


    private static final String WHERE_ID_EQUALS = FarmDbHandler.COLUMN_SID
            + " =?";

    public StockAO(Context context) {
        super(context);
    }

    public long addStock(Stock stock) {

        ContentValues values = new ContentValues();
        values.put(FarmDbHandler.COLUMN_STOCKNAME, stock.getStockName());
        values.put(FarmDbHandler.COLUMN_QUANTITY, stock.getQuantity());
        values.put(FarmDbHandler.COLUMN_DAILYFEED, stock.getDailyfeed());
        values.put(FarmDbHandler.COLUMN_SUPPLEMENT, stock.getSupplement());
        values.put(FarmDbHandler.COLUMN_SUPKG, stock.getSupKg());
        values.put(FarmDbHandler.COLUMN_GRASSKG, stock.getGrassKg());
        values.put(FarmDbHandler.COLUMN_AREAUSING, stock.getAreaUsing());
        values.put(FarmDbHandler.COLUMN_PADDOCK_IN, stock.getPaddock().getID());

        return database.insert(FarmDbHandler.TABLE_STOCK, null, values);

    }

    public long updateStock(Stock stock) {

        ContentValues values = new ContentValues();
        values.put(FarmDbHandler.COLUMN_STOCKNAME, stock.getStockName());
        values.put(FarmDbHandler.COLUMN_QUANTITY, stock.getQuantity());
        values.put(FarmDbHandler.COLUMN_DAILYFEED, stock.getDailyfeed());
        values.put(FarmDbHandler.COLUMN_SUPPLEMENT, stock.getSupplement());
        values.put(FarmDbHandler.COLUMN_SUPKG, stock.getSupKg());
        values.put(FarmDbHandler.COLUMN_GRASSKG, stock.getGrassKg());
        values.put(FarmDbHandler.COLUMN_AREAUSING, stock.getAreaUsing());
        values.put(FarmDbHandler.COLUMN_PADDOCK_IN, stock.getPaddock().getID());

        long result = database.update(FarmDbHandler.TABLE_STOCK, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(stock.getID()) });
        Log.d("Update Result:", "=" + result);
        return result;
    }

    public int deleteStock(Stock stock) {

        return database.delete(FarmDbHandler.TABLE_STOCK, WHERE_ID_EQUALS,
                new String[] { stock.getID() + "" });
    }

    public ArrayList<Stock> getStock() {

        ArrayList<Stock> stocks = new ArrayList<Stock>();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder
                .setTables(FarmDbHandler.TABLE_STOCK
                        + " INNER JOIN "
                        + FarmDbHandler.TABLE_PADDOCKS
                        + " ON "
                        + FarmDbHandler.COLUMN_PADDOCK_IN
                        + " = "
                        + (FarmDbHandler.TABLE_PADDOCKS + "."
                                + FarmDbHandler.COLUMN_PID));

        // get cursor
        Cursor cursor = queryBuilder.query(database, new String[] {
                        STOCK_ID_WITH_PREFIX,
                        FarmDbHandler.TABLE_STOCK + "."
                                + FarmDbHandler.COLUMN_STOCKNAME,
                        FarmDbHandler.COLUMN_QUANTITY,
                        FarmDbHandler.COLUMN_DAILYFEED,
                        FarmDbHandler.COLUMN_SUPPLEMENT,
                        FarmDbHandler.COLUMN_SUPKG,
                        FarmDbHandler.COLUMN_AREAUSING,
                        FarmDbHandler.COLUMN_PADDOCK_IN,
                        FarmDbHandler.TABLE_PADDOCKS + "."
                                + FarmDbHandler.COLUMN_PADDOCKNAME}, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            Stock stock = new Stock();
            stock.setID(cursor.getInt(0));
            stock.setStockName(cursor.getString(1));
            stock.setQuantity(cursor.getInt(2));
            stock.setDailyfeed(cursor.getDouble(3));
            stock.setSupplement(cursor.getInt(4));
            stock.setSupKg(cursor.getDouble(5));
            stock.setGrassKg();
            stock.setAreaUsing(cursor.getDouble(6));

            Paddock paddock = new Paddock();
            paddock.setID(cursor.getInt(7));
            paddock.setPaddockName(cursor.getString(8));

            stock.setPaddock(paddock);

            stocks.add(stock);
        }
        return stocks;
    }

    public int TotalStock() {
        int totalStock = 0;
        Cursor cur = database.rawQuery("SELECT SUM(quantity) FROM stock", null);
        if(cur.moveToFirst())
        {
            totalStock = cur.getInt(0);
        }while (cur.moveToNext());
        return totalStock;
    }

    public double TotalStockArea() {
        double areaUsing = 0;
        Cursor cur = database.rawQuery("SELECT SUM(areausing) FROM stock", null);
        if(cur.moveToFirst())
        {
            areaUsing = cur.getDouble(0);
        }while (cur.moveToNext());
        return areaUsing;
    }

    public double StockDemand() {

        double stockEating = 0;
        Cursor cur = database.rawQuery("SELECT SUM(quantity * grassKg) FROM stock", null);
        if(cur.moveToFirst())
        {
            stockEating = cur.getDouble(0);
        }while (cur.moveToNext());
        Log.i(TAG, "stockeating: "+stockEating);
        return stockEating;
    }
}
