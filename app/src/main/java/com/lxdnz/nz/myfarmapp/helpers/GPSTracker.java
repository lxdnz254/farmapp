package com.lxdnz.nz.myfarmapp.helpers;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.FarmDbHandler;
import com.lxdnz.nz.myfarmapp.notificationhandler.NotificationActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.app.AlertDialog.*;

/**
 * Created by alex on 25/02/16.
 */
public class GPSTracker extends Service implements LocationListener {

    private final String TAG = "GPSTracker";
    MapDbAO mapDbAO;
    private String inPaddock;
    private String currentPaddock;

    private Context mContext;

    // flag for service running
    public static Boolean isServiceRunning = false;
    // flag for farmwalk in action
    public static Boolean onFarmwalk = false;
    // the service handler objects
    private Looper looper;
    private MyServiceHandler myServiceHandler;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3; // 3 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(){

    }

    public GPSTracker(Context context) {

        this.mContext = context;

        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location != null) { // needs to be not null for GPS location to be passed to app
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){

            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        Builder alertDialog = new Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged activated");
        sharedPrefs();
        Log.i(TAG, "preference get current paddock:" + currentPaddock);
        inPaddock = null;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        checkPaddock(latLng);
        Log.i(TAG, "checked paddock in:" + inPaddock);
        if (inPaddock != null && !inPaddock.matches(currentPaddock)) {
            Log.i(TAG, "new notification for paddock:"+inPaddock);
            notifyPaddock(inPaddock);
        }
        if(!onFarmwalk) {
            stopUsingGPS();
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        HandlerThread handlerthread = new HandlerThread("MyThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerthread.start();
        looper = handlerthread.getLooper();
        myServiceHandler = new MyServiceHandler(looper);
        isServiceRunning = true;

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "service destroyed");
        Toast.makeText(this, "GPSTracker Stopped.", Toast.LENGTH_SHORT).show();
        isServiceRunning = false;
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Message msg = myServiceHandler.obtainMessage();
        msg.arg1 = startId;
        myServiceHandler.sendMessage(msg);
        checkContext();
        getLocation();
        sharedPrefs();

        if (onFarmwalk) {
            Log.d(TAG, "OnStart STICKY");
            Toast.makeText(this, "GPSTracker Started STICKY.", Toast.LENGTH_SHORT).show();
            //setPreferences("Farmwalk", "on");
            return START_STICKY;
        }else{
            Log.d(TAG, "OnStart NOT_STICKY");
            Toast.makeText(this, "GPSTracker Started NOT STICKY.", Toast.LENGTH_SHORT).show();
            //setPreferences("Farmwalk", "off");
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public Context checkContext() {

        if (mContext == null){
            mContext = this.getBaseContext();
        }

        return mContext;
    }

    public void checkPaddock(LatLng latLng){



        /**
         * Build SQLite query and set up the cursor
         */
        MapDbAO mapDbAO = new MapDbAO(getApplication());
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FarmDbHandler.TABLE_PADDOCKS);

        Cursor cursor = queryBuilder.query(mapDbAO.mapDatabase, new String[]{

                FarmDbHandler.COLUMN_MAPPED,
                FarmDbHandler.COLUMN_POLYPOINTS,
                FarmDbHandler.COLUMN_PADDOCKNAME
        }, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                int mapped = cursor.getInt(0);
                if (mapped > 0) {

                    String DbPolyString = cursor.getString(1);
                    String DbPolyName = cursor.getString(2);

                    //Parse Gson to ArrayList<LatLng>
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<LatLng>>() {
                    }.getType();
                    ArrayList<LatLng> polyPoints = gson.fromJson(DbPolyString, type);
                    if(PolyUtil.containsLocation(latLng, polyPoints, false)){
                        inPaddock = DbPolyName;

                    }

                }
            }while (cursor.moveToNext());
        }
        cursor.close();



    }

    public void notifyPaddock(String inPaddock){

        Toast.makeText(mContext,"Currently in paddock "+inPaddock, Toast.LENGTH_SHORT).show();
        setPreferences("userInPaddock", inPaddock);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1357;
        // Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(mContext, NotificationActivity.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setTicker("You are in paddock "+inPaddock)
                .setContentTitle("New Paddock")
                .setContentText("You have moved into paddock "+inPaddock)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(notifyPendingIntent);


        // Because the ID remains unchanged, the existing notification is updated.
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());

    }

    private class MapDbAO {

        protected SQLiteDatabase mapDatabase;
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
            mapDatabase = dbHelper.getWritableDatabase();
        }
    }

    private void setPreferences(String string, String i){

        SharedPreferences sharedPref = mContext.getSharedPreferences("FarmApp", 0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString(string, i);

        prefEditor.commit();

    }

    private void sharedPrefs() {

        SharedPreferences sharedPref = mContext.getSharedPreferences("FarmApp", Context.MODE_PRIVATE);
        String getPaddock = sharedPref.getString("userInPaddock", "");
        if (getPaddock != null){
            currentPaddock = getPaddock;
            Log.i(TAG, "getPaddock not null");
        }else{
            Log.i(TAG, "getPaddock null");
        }
        String getFarmWalk = sharedPref.getString("Farmwalk", "");
        Log.i(TAG, "GPS Farmwalk :"+getFarmWalk);
        if (getFarmWalk != null) {
            if (getFarmWalk.contentEquals("on")) {
                onFarmwalk = true;
                Log.i(TAG, "Farmwalk on");
            } else {
                onFarmwalk = false;
                Log.i(TAG,"Farmwalk off");
            }
        }else{
                onFarmwalk=false;
                Log.i(TAG, "Farmwalk not exist");
            }

    }

    private final class MyServiceHandler extends Handler {
        public MyServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                for (int i = 0; i < 10; i++) {
                    try {
                        Log.i(TAG, "GPSTracker running...");
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                    if(!isServiceRunning){
                        Log.i(TAG, "GPSTracker not running...");
                        break;
                    }
                }
            }
            //stops the service for the start id.
            if(!onFarmwalk) {
                Log.i(TAG, "GPSTracker farmwalk false...");
                stopSelfResult(msg.arg1);
            }
        }
    }

}
