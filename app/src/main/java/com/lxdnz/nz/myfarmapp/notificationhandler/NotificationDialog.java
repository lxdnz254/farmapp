package com.lxdnz.nz.myfarmapp.notificationhandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.FarmDbHandler;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.GPSTracker;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by alex on 14/03/16.
 */
public class NotificationDialog extends DialogFragment  {

    private EditText coverUpdateEtxt;
    private TextView currentCoverTxt;
    private TextView coverUpdateDateEtxt;
    private TextView currentCoverDateTxt;
    private LinearLayout submitLayout;


    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    private GetPaddockTask paddockTask;
    private Context mContext;
    Paddock paddock;
    private NotificationActivity notificationActivity;
    private NotificationActivityClose mCloseActivity;

    private Paddock Updatepaddock;
    private String pad;
    //private int sectionNumber;
    private int currentcover;
    private Date currentcoverdate;
    //DatePickerDialog datePickerDialog;
    //Calendar dateCalendar;

    PaddockAO paddockAO;
    ArrayList<Paddock> getpaddocks;

    String TAG = "NotifactionDialog";




    public static NotificationDialog newInstance() {
        NotificationDialog f = new NotificationDialog( );
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        notificationActivity = (NotificationActivity)getActivity();
        sharedPref();
        paddockAO = new PaddockAO(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customDialogView = inflater.inflate(R.layout.dialog_notification_cover,
                null);
        builder.setView(customDialogView);



        coverUpdateEtxt = (EditText) customDialogView.findViewById(R.id.set_cover);
        currentCoverTxt = (TextView) customDialogView
                .findViewById(R.id.get_cover);
        coverUpdateDateEtxt = (TextView) customDialogView.findViewById(R.id.set_date);
        //coverUpdateDateEtxt.setInputType(InputType.TYPE_NULL);
        currentCoverDateTxt = (TextView) customDialogView.findViewById(R.id.get_date);
        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit_cover);
        submitLayout.setVisibility(View.GONE);

        paddockTask = new GetPaddockTask(getActivity());
        paddockTask.execute((Void) null);


        dateSet();

        builder.setTitle("Update Cover for paddock " + pad);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.update,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            Updatepaddock.setCurrentCoverDate(formatter.parse(coverUpdateDateEtxt.getText().toString()));
                        } catch (ParseException e) {
                            Toast.makeText(getActivity(),
                                    "Invalid date format!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Updatepaddock.setCurrentCover(Integer.parseInt(coverUpdateEtxt.getText().toString()));

                        Updatepaddock.setPreviousCover(currentcover);

                        Updatepaddock.setPreviousCoverDate(currentcoverdate);

                        long result = paddockAO.updatePaddock(Updatepaddock);
                        if (result > 0) {
                            Toast.makeText(getActivity(), "Updated Cover", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(),
                                    "Unable to update paddock",
                                    Toast.LENGTH_SHORT).show();
                        }
                        exitActivity();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        exitActivity();

                    }
                });

        builder.setNeutralButton(R.string.stopfarmwalk,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notificationActivity.stopService(new Intent(notificationActivity,GPSTracker.class));
                        GPSTracker.onFarmwalk = false;
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("FarmApp", 0);
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        prefEditor.putString("Farmwalk", "off");
                        prefEditor.commit();
                        exitActivity();

                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.setOwnerActivity(notificationActivity);

        return alertDialog;

    }

    public void exitActivity() {
        notificationActivity.close();
    }

    private void userPaddock(){

        sharedPref();
        String getPaddock = pad;
        int pos=0;

        MapDbAO mapDbAO = new MapDbAO(getActivity());
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FarmDbHandler.TABLE_PADDOCKS);

        Cursor cursor = queryBuilder.query(mapDbAO.mapdatabase, new String[]{

                FarmDbHandler.COLUMN_PID,
                FarmDbHandler.COLUMN_PADDOCKNAME
        }, "paddockname = '" + getPaddock+"'", null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                pos = cursor.getInt(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i(TAG, "pos is equal to:" + (pos-1));

        Paddock currentPaddock = getpaddocks.get(pos-1);


        setValue(currentPaddock);
        Updatepaddock = currentPaddock;
    }

    private void setValue(Paddock paddock) {
        if (paddock != null) {

            currentCoverTxt.setText(R.string.coveris);
            currentCoverTxt.append(paddock.getCurrentCover() + "");
            currentCoverDateTxt.setText(R.string.coverdateis);
            currentCoverDateTxt.append(formatter.format(paddock.getCurrentCoverDate()));
            currentcover = paddock.getCurrentCover();
            currentcoverdate = paddock.getCurrentCoverDate();

        }
    }

    protected void sharedPref(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FarmApp", Context.MODE_PRIVATE);
        pad = sharedPref.getString("userInPaddock", "");

        }

    private void dateSet(){
        Calendar newCalendar = Calendar.getInstance();
        coverUpdateDateEtxt.setText(formatter.format(newCalendar
                .getTime()));
    }

    public class GetPaddockTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Activity> activityWeakRef;
        private ArrayList<Paddock> paddocks;

        public GetPaddockTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            paddocks = paddockAO.getPaddocks();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {

                getpaddocks = new ArrayList<Paddock>(paddocks);
                userPaddock();

            }
        }
    }

    private class MapDbAO {

        protected SQLiteDatabase mapdatabase;
        private FarmDbHandler dbHelper;
        private Context mContext;

        public MapDbAO(Context context) {
            this.mContext = context;
            dbHelper = FarmDbHandler.getHelper(mContext);
            open();

        }

        public void open() throws SQLException {
            if(dbHelper == null)
                dbHelper = FarmDbHandler.getHelper(mContext);
            mapdatabase = dbHelper.getWritableDatabase();
        }
    }



}
