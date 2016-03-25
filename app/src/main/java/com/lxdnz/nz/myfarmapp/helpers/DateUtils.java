package com.lxdnz.nz.myfarmapp.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;

/**
 * Created by alex on 19/02/16.
 */
public class DateUtils {

    private static final String TAG = "DateUtils";

    // formatter to convert from current timezone
    private static final SimpleDateFormat DATE_FORMATTER_FROM_CURRENT = new SimpleDateFormat("yyyy-MM-dd");

    // formatter to convert to GMT timezone
    private static final SimpleDateFormat DATE_FORMATTER_TO_GMT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        // initialize the GMT formatter
        final Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        DATE_FORMATTER_TO_GMT.setCalendar(cal);
    }

    public static Date toGMT(final Date base) {
        try {
            // convert to string and after that convert it back
            final String date = DATE_FORMATTER_FROM_CURRENT.format(base);
            return DATE_FORMATTER_TO_GMT.parse(date);

        } catch (ParseException e) {
            Log.e(TAG, "Date parsing failed. Conversion to GMT wasn't performed.", e);
        }
        return base;
    }


    /**
     * builds date cell for header
     */
    public static WritableCell createDate(final int column, final int row, final Date value) {
        final DateFormat valueFormatDate = new DateFormat("dd/MM/yyyy");
        valueFormatDate.getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT"));
        final WritableCellFormat formatDate = new WritableCellFormat(valueFormatDate);

        // create cell
        return new DateTime(column, row, toGMT(value), formatDate, DateTime.GMT);
    }
}