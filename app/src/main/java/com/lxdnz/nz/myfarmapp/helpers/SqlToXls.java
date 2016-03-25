package com.lxdnz.nz.myfarmapp.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.util.Log;

import com.lxdnz.nz.myfarmapp.databases.FarmDbAO;
import com.lxdnz.nz.myfarmapp.databases.FarmDbHandler;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;

import java.io.File;
import java.io.IOException;
import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jxl.CellFeatures;
import jxl.CellType;
import jxl.DateCell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.Pattern;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import jxl.write.DateFormat;
import jxl.write.Number;
import jxl.write.biff.DateRecord;
import jxl.write.biff.RowsExceededException;

/**
 * Created by alex on 18/02/16.
 */
public class SqlToXls extends FarmDbAO {

    DateUtils mDateUtils;

    private String TAG="SqlToXls";

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    // Defined once.
    public static final WritableCellFormat DATE_CELL_FRMT;
    static {
        DateFormat df = new DateFormat("dd/MM/yyyy");
        df.getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT"));
        DATE_CELL_FRMT = new WritableCellFormat(df);
    }



    public SqlToXls(Context context) {
        super(context);
    }

    /**
     * Exports the cursor value to an excel sheet.
     * Recommended to call this method in a separate thread,
     * especially if you have more number of threads.
     *
     * @param ??
     */

    public void createXls(){
        final String fileName = "FarmWalk.xls";

        /**
         * Build SQLite query and set up the cursor
         */

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder
                .setTables(FarmDbHandler.TABLE_PADDOCKS);

        Cursor cursor = queryBuilder.query(database, new String[]{
                FarmDbHandler.TABLE_PADDOCKS + "."
                        + FarmDbHandler.COLUMN_PADDOCKNAME,
                FarmDbHandler.COLUMN_AREA,
                FarmDbHandler.COLUMN_CURRENTCOVER,
                FarmDbHandler.COLUMN_PREVIOUSCOVER,
                FarmDbHandler.COLUMN_CURRENTCOVERDATE,
                FarmDbHandler.COLUMN_PREVIOUSCOVERDATE
        }, null,null,null,null,"cast("+FarmDbHandler.COLUMN_CURRENTCOVER+" as REAL) ASC");

        /**
         * Set up the saving directory
         */
        //Saving file in external Public storage (ie where the user has their downloads directory)
        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File directory = new File(sdCard.getAbsolutePath() + "/stockfeeder.farmwalk");

        //create directory if not exist
        if(!directory.isDirectory()){
            directory.mkdirs();
            Log.i(TAG, "created directory");
        }

        //file path
        File file = new File(directory, fileName);

        /**
         * Start up the workbook
         */

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        wbSettings.setUseTemporaryFileDuringWrite(true);
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("MyFarmWalk", 0);

            try {
                sheet.addCell(new Label(0, 0, "Paddock")); // column and row
                sheet.addCell(new Label(1, 0, "Area"));
                sheet.addCell(new Label(2, 0, "Cover"));
                sheet.addCell(new Label(3, 0, "Total Area:"));
                sheet.addCell(new Label(4, 0, "Target Residual:"));
                sheet.addCell(new Number(4, 1, Constants.getTargetresidual()));
                sheet.addCell(new Label(5, 0, "Average Cover:"));
                sheet.addCell(new Label(6, 0 , "Available"));
                sheet.addCell(new Label(7, 0 , "Previous Cover"));
                sheet.addCell(new Label(8, 0, "Growth"));
                sheet.addCell(new Label(9, 0, "Farm Growth:"));

                int j; // row counter
                double growthtotal = 0; //growth total
                int growthcount = 0; // number of cells with positive growth
                if (cursor.moveToFirst()) {
                    do {
                        String paddock = cursor.getString(0);
                        double area = cursor.getDouble(1);
                        int cover = cursor.getInt(2);
                        int prevcover = cursor.getInt(3);

                        int i = cursor.getPosition() + 1;
                        j=i+1;
                        sheet.addCell(new Label(0, i, paddock));
                        sheet.addCell(new Number(1, i, area));
                        sheet.addCell(new Number(2, i, cover));
                        sheet.addCell(new Number(6, i, (cover-Constants.getTargetresidual())*area));
                        sheet.addCell(new Number(7, i, prevcover));
                        if (prevcover >= cover) {
                            sheet.addCell(new Label(8, i, "NIL"));
                        }else{
                            int diff = cover-prevcover; // positive difference in covers

                            /**
                             * Get the cover dates, parse string to number,
                             */
                            Date currentdayparse = new Date();
                            Date previousdayparse = new Date();

                            String currentday = cursor.getString(4);
                            String previousday = cursor.getString(5);

                            try {currentdayparse = formatter.parse(currentday);
                            }catch (ParseException e){
                                Log.e(TAG, "parsing date failed", e);
                            }

                            try {previousdayparse = formatter.parse(previousday);
                            }catch (ParseException e){
                                Log.e(TAG, "parsing date failed", e);
                            }
                            /**
                             * Work out difference and growth
                             */
                            int daydiff = ((int)((currentdayparse.getTime()/(24*60*60*1000)) - (int
                                    )(previousdayparse.getTime()/(24*60*60*1000))));
                            double growth = ((double) diff / daydiff);
                            growthtotal = growthtotal + growth;
                            growthcount++;

                            /**
                             * Excel sheet add formula: ROUND(number,decimal places)
                             */
                            String growthFormString = "ROUND("+growth+" ,0)";
                            Formula growthFormula = new Formula(8,i, growthFormString);
                            sheet.addCell(growthFormula);
                            }
                    } while (cursor.moveToNext());

                    /**
                     * Excel sheet add formula: SUM(cell1:cell2)
                     */
                    String areaFormString = "SUM(B2:B"+j+")";
                    Formula areaFormula = new Formula(3,1, areaFormString);
                    sheet.addCell(areaFormula);
                    /**
                     * Excel sheet add fromula: ROUND(SUMPRODUCT(cell1:cell2*cell3:cell4)/cell, decimal places)
                     */
                    String avcoverFormString = "ROUND(SUMPRODUCT(B2:B"+j+"*C2:C"+j+")/D2,0)";
                    Formula avcoverFormula = new Formula(5,1,avcoverFormString);
                    sheet.addCell(avcoverFormula);
                    /**
                     * Excel sheet add formula: ROUND(number,decimal places)
                     */
                    double farmgrowth = growthtotal/growthcount; // get average farmgrowth
                    String farmFormString = "ROUND("+farmgrowth+" ,0)";
                    Formula farmFormula = new Formula(9, 1, farmFormString);
                    sheet.addCell(farmFormula);
                    /**
                     * Organise the two cells for the most recent date
                     */
                    sheet.addCell(new Label(10, 0, "Cover Date"));
                    // query database for the date Descending
                    Cursor date = queryBuilder.query(database, new String[]{FarmDbHandler.COLUMN_CURRENTCOVERDATE
                    }, null, null, null, null, FarmDbHandler.COLUMN_CURRENTCOVERDATE + " DESC");
                    date.moveToFirst();
                    final String coverdate = date.getString(0);
                    date.close();

                    // parse the string to date
                    Date coverdateparse = new Date();
                    try {

                        coverdateparse = formatter.parse(coverdate);
                    }catch (ParseException e){
                        Log.e(TAG, "parsing date failed", e);
                    }



                    WritableCell utilDate = mDateUtils.createDate(10,1,coverdateparse);

                    sheet.addCell(utilDate);
                }
                //closing cursor
                cursor.close();
            }
            /**
             * catch exceptions
             */
            catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
