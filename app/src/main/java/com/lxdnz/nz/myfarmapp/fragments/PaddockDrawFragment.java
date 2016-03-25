package com.lxdnz.nz.myfarmapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.maps.android.SphericalUtil;
import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.FarmDbAO;
import com.lxdnz.nz.myfarmapp.databases.FarmDbHandler;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.dialogs.MapDialog;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.GPSTracker;


import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 20/02/16.
 */
public class PaddockDrawFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener{

    MapView mMapView;
    private GoogleMap googleMap;
    PaddockAO paddockAO;
    MapDbAO mapDbAO;
    GPSTracker gps;
    Context mContext;

    SupportMapFragment sMapFragment;

    private ArrayList<LatLng> arrayPoints = null;

    public ArrayList<Polygon> polygonList=new ArrayList<>();
    public ArrayList<String> polygonName=new ArrayList<>();
    private boolean checkClick = false;
    private boolean polygonExist = false;
    private boolean polygonClickable = false;
    private int spread;
    private int red=0;
    private int green=55;
    private int blue=0;
    private int alpha=100;
    private double redInc;
    private double greenInc;

    private GetPaddockTask task;
    Activity activity;

    ArrayList<Paddock> getpaddocks;
    private Paddock paddock;
    private Intent intent;
    private Context sharedContext;

    public final String TAG = "PaddockDraw";
    private String enablePaddocks="Enable Paddocks";
    private String viewCover = "view covers";
    private String farmwalkActive = "Start Farmwalk";
    private boolean farmwalkOn;
    public boolean enabled = false;
    public boolean coverView = false;







    public static final String ARG_ITEM_ID = "pad_draw_fragment";

    public int sectionNumber = 7;


    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        intent =  new Intent(activity,GPSTracker.class);
        sharedContext = this.getContext();
        farmwalkOn = GPSTracker.onFarmwalk;

        paddockAO = new PaddockAO(activity);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_paddock_draw, container,
                false);

        task = new GetPaddockTask(activity);
        task.execute((Void) null);


        sMapFragment = SupportMapFragment.newInstance();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        // Perform any camera updates here
        setUpMap();
        arrayPoints = new ArrayList<LatLng>();
        if(polygonExist) {
            Button button = (Button) rootView.findViewById(R.id.enablePaddockButton);
            button.setText(enablePaddocks);
            Log.i(TAG, "settng text button:" + enablePaddocks);
            button.setAlpha((float) 0.4);

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!enabled) {
                        polygonClickable = true;
                        enablePaddocks = "Disable Paddocks";
                    } else {
                        polygonClickable = false;
                        enablePaddocks = "Enable Paddocks";
                    }
                    if (polygonClickable) {
                        enabled = true;

                    } else {
                        enabled = false;
                    }
                    Button button1 = (Button) rootView.findViewById(R.id.enablePaddockButton);
                    button1.setText(enablePaddocks);

                    setClick();

                    clickPolygon();
                }
            });

            Button button2 = (Button) rootView.findViewById(R.id.viewCoverButton);
            button2.setText(viewCover);
            button2.setAlpha((float) 0.4);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button2a = (Button) rootView.findViewById(R.id.viewCoverButton);
                    CharSequence buttonText = button2a.getText();
                    if (buttonText == "view covers") {
                        coverView = true;
                        viewCovers();
                        button2a.setText("hide covers");
                    } else {
                        coverView = false;
                        button2a.setText("view covers");
                        googleMap.clear();
                        getPaddockPoints();

                    }

                    setClick();
                }
            });

            Button button3 = (Button) rootView.findViewById(R.id.farmwalkButton);
            if(farmwalkOn){
                farmwalkActive = "Stop Farmwalk";
            }
            button3.setText(farmwalkActive);
            button3.setAlpha((float) 0.4);
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button3a = (Button) rootView.findViewById(R.id.farmwalkButton);
                    CharSequence buttonText = button3a.getText();
                    if (buttonText == "Start Farmwalk") {
                        GPSTracker.onFarmwalk = true;
                        farmwalkOn = true;
                        setPreferences("Farmwalk", "on");
                        activity.startService(intent);
                        button3a.setText(R.string.stopfarmwalk);
                    }else{
                        GPSTracker.onFarmwalk = false;
                        farmwalkOn = false;
                        setPreferences("Farmwalk", "off");
                        activity.stopService(intent);
                        button3a.setText(R.string.startfarmwalk);
                    }
                }
            });

        }



        sMapFragment.getMapAsync(this);
        return rootView;
    }

    public static PaddockDrawFragment newInstance(int sectionNumber) {
        PaddockDrawFragment fragment = new PaddockDrawFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void setClick() {
        if(polygonExist) {

            for (int i = 0; i < polygonList.size(); i++) {
                Polygon polySet = polygonList.get(i);
                if(polySet != null) {
                    polySet.setClickable(enabled);
                }
            }
        }
    }

    private void setUpMap() {

        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Intent i = new Intent(activity,GPSTracker.class);


        if (!GPSTracker.isServiceRunning) {
            activity.startService(intent);
            gps = new GPSTracker(getActivity());


        }else{

            gps = new GPSTracker(getActivity());
        }


        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f));
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();

        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        getPaddockPoints();
        if (!GPSTracker.onFarmwalk){
            activity.stopService(intent);
        }

    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.draw_paddock);
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMapClick(LatLng point) {
        if (checkClick == false) {

            googleMap.addMarker(new MarkerOptions().position(point).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.measle)));
            arrayPoints.add(point);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        googleMap.clear();
        arrayPoints.clear();
        checkClick = false;
        getPaddockPoints();
        if(coverView) {
            viewCovers();
        }

        setClick();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        if (arrayPoints.get(0).equals(marker.getPosition())) {
            countPolygonPoints();
            mapPaddock();
        }
        return false;
    }

    public void countPolygonPoints() {
        if (arrayPoints.size() >= 3) {
            checkClick = true;
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(arrayPoints);

            polygonOptions.strokeColor(Color.RED);
            polygonOptions.strokeWidth(3);
            polygonOptions.fillColor(Color.argb(70, 15, 100, 150));

            Polygon polygon = googleMap.addPolygon(polygonOptions);
            int k = Integer.parseInt(polygon.getId().replaceAll("[\\D]", ""));
            polygonList.add(k, polygon);
            polygonName.add(k, "new polygon");
        }
    }

    public void mapPaddock(){
        double polyArea = SphericalUtil.computeArea(arrayPoints);
        BigDecimal roundArea = new BigDecimal(polyArea/10000).setScale(2, RoundingMode.HALF_EVEN);
        double finalArea =  roundArea.doubleValue();
        Gson gson = new Gson();
        String polyString = gson.toJson(arrayPoints);
        mapBundle(finalArea, polyString);
    }

    public void mapBundle(double area, String points){
        Bundle arguments = new Bundle();
        arguments.putDouble("mappedArea", area);
        arguments.putString("mappedPoints", points);
        arguments.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        Log.i(TAG, "Bundling Map section Number:" + sectionNumber);
        MapDialog mapDialog = new MapDialog();
        mapDialog.setArguments(arguments);
        mapDialog.show(getFragmentManager(), MapDialog.ARG_ITEM_ID);
    }

    public void getPaddockPoints () {

        /**
         * Build SQLite query and set up the cursor
         */
        MapDbAO mapDbAO = new MapDbAO(getActivity());
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FarmDbHandler.TABLE_PADDOCKS);

        Cursor cursor = queryBuilder.query(mapDbAO.mapdatabase, new String[]{

                FarmDbHandler.COLUMN_MAPPED,
                FarmDbHandler.COLUMN_POLYPOINTS,
                FarmDbHandler.COLUMN_PID
        }, null, null, null, null, null);

             if(cursor.moveToFirst()) {
                 do {
                     int mapped = cursor.getInt(0);
                     if (mapped>0) {
                         polygonExist = true;
                         String DbPolyString = cursor.getString(1);
                         String DbPolyId = cursor.getString(2);

                         //Parse Gson to ArrayList<LatLng>
                         Gson gson = new Gson();
                         Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
                         ArrayList<LatLng> polyPoints = gson.fromJson(DbPolyString, type);
                         // new polygon
                         PolygonOptions polygonOptions = new PolygonOptions();
                         polygonOptions.addAll(polyPoints);
                         polygonOptions.isClickable();
                         polygonOptions.strokeColor(Color.BLUE);
                         polygonOptions.strokeWidth(2);
                         polygonOptions.fillColor(Color.argb(70, 0, 100, 200));
                         Polygon polygon = googleMap.addPolygon(polygonOptions);
                         // add polygon to List and Name according to PID
                         int k = Integer.parseInt(polygon.getId().replaceAll("[\\D]", ""));
                         if (polygonList.size() != 0) {
                             polygonList.add(k, polygon);
                             polygonName.add(k, DbPolyId);
                         }else if(polygonList.size() == 0 && k == 0) {
                             polygonList.add(k, polygon);
                             polygonName.add(k, DbPolyId);
                         }else{
                                 for (int j=0;j<=k;j++){
                                     polygonList.add(j,null);
                                     polygonName.add(j,null);
                                 }
                         }
                     }
                 } while (cursor.moveToNext());
             }
                cursor.close();
    }

    public void viewCovers() {


        googleMap.clear();

        MapDbAO mapDbAO = new MapDbAO(getActivity());
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FarmDbHandler.TABLE_PADDOCKS);

        Cursor cursor = queryBuilder.query(mapDbAO.mapdatabase, new String[]{
                FarmDbHandler.COLUMN_CURRENTCOVER,
                FarmDbHandler.COLUMN_MAPPED
        }, "mapped=1",null,null,null,"currentcover DESC");
        if (cursor.moveToFirst()){
           spread = cursor.getInt(0)-Constants.getTargetresidual();
        }
        cursor.close();

        redInc = (double)255/spread;
        greenInc = (double)200/spread;

        Cursor cursor1 = queryBuilder.query(mapDbAO.mapdatabase, new String[]{
                FarmDbHandler.COLUMN_MAPPED,
                FarmDbHandler.COLUMN_PID,
                FarmDbHandler.COLUMN_POLYPOINTS,
                FarmDbHandler.COLUMN_CURRENTCOVER
        }, null,null,null,null,null);

        if(cursor1.moveToFirst()) {
            do {
                int mapped = cursor1.getInt(0);
                if (mapped>0) {
                    polygonExist = true;
                    String DbPolyString = cursor1.getString(2);
                    String DbPolyId = cursor1.getString(1);
                    // set cover colors
                    int dbcover = cursor1.getInt(3);
                    double redShift = 255-((dbcover-Constants.getTargetresidual())*redInc);
                    double greenShift = 255-((dbcover-Constants.getTargetresidual())*greenInc);
                    BigDecimal redRound = new BigDecimal(redShift).setScale(0,RoundingMode.HALF_EVEN);
                    BigDecimal greenRound = new BigDecimal(greenShift).setScale(0,RoundingMode.HALF_EVEN);
                    BigDecimal blueRound = new BigDecimal((double)256/Constants.getTargetresidual())
                            .setScale(0, RoundingMode.HALF_EVEN);
                    int blueMultiplier = blueRound.intValue();
                    red = redRound.intValue();
                    green = greenRound.intValue();
                    // check if larger than 255 and adjust blue if so
                    if (red > 255 & green < 255){
                        blue = (red-255)*blueMultiplier;
                        red = 255;
                    }else if (green > 255 & red < 255){
                        blue = (green-255)*blueMultiplier;
                        green = 255;
                    }else if (green > 255 & red > 255){
                        blue = ((green-255)+(red-255))*blueMultiplier;
                        red = 255;
                        green = 255;
                    }else{
                        blue = 0;
                    }
                    if (green<255) {
                        BigDecimal alphaRound = new BigDecimal((255-green)/2).setScale(0,BigDecimal.ROUND_DOWN);
                         alpha = alphaRound.intValue()+100;
                    }else{
                         alpha=100;
                    }

                    //define fencelines
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<LatLng>>() {
                    }.getType();
                    ArrayList<LatLng> polyPoints = gson.fromJson(DbPolyString, type);

                    //create polygon
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.addAll(polyPoints);
                    polygonOptions.isClickable();
                    polygonOptions.strokeColor(Color.BLUE);
                    polygonOptions.strokeWidth(2);
                    polygonOptions.fillColor(Color.argb(alpha, red, green, blue));
                    Polygon polygon = googleMap.addPolygon(polygonOptions);

                    // add polygon to List and Name with Id identifier
                    int k = Integer.parseInt(polygon.getId().replaceAll("[\\D]", ""));
                    if (polygonList.size() != 0) {
                        polygonList.add(k, polygon);
                        polygonName.add(k, DbPolyId);
                    }else if(polygonList.size() == 0 && k == 0) {
                        polygonList.add(k, polygon);
                        polygonName.add(k, DbPolyId);
                    }else{
                        for (int j=0;j<=k;j++){
                            polygonList.add(j,null);
                            polygonName.add(j,null);
                        }
                        Log.i(TAG, "created new polygon arrays with nulls");
                    }
                }
            } while (cursor1.moveToNext());
        }
        cursor1.close();
    }

    public void clickPolygon() {
        if (polygonClickable) {
            Log.i(TAG, "setting onPolygonClickableListener");
            googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(Polygon polygon) {
                    int i = Integer.parseInt(polygon.getId().replaceAll("[\\D]", ""));
                    int pos = 0;
                    String pid = polygonName.get(i);

                    MapDbAO mapDbAO = new MapDbAO(getActivity());
                    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                    queryBuilder.setTables(FarmDbHandler.TABLE_PADDOCKS);

                    Cursor cursor = queryBuilder.query(mapDbAO.mapdatabase, new String[]{

                            FarmDbHandler.COLUMN_PID
                    }, "pid = " + pid, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            pos = cursor.getInt(0);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    Paddock paddock = getpaddocks.get(pos-1);
                    Toast.makeText(getActivity(), "Clicked on Paddock:" + paddock.getPaddockName()+", Cover:"+
                            paddock.getCurrentCover()+", Area:"+paddock.getArea()+"ha", Toast.LENGTH_SHORT).show();
                    // Handle click ...pass the paddock to zoom to..
                    zoomToPaddock(paddock);

                }
            });
        }else{
            Log.i(TAG, "disable polygonClickListener");
            googleMap.setOnPolygonClickListener(null);
        }
    }

    private void zoomToPaddock(Paddock zoomPaddock){

        // get the fencelines of paddock
        String DbPolyString = zoomPaddock.getPolyPoints();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        ArrayList<LatLng> polyPoints = gson.fromJson(DbPolyString, type);

        // build the new zoom level
        LatLngBounds.Builder zoomTo = new LatLngBounds.Builder();

        for (LatLng item:polyPoints){
            zoomTo.include(item);
        }
        // move the camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(zoomTo.build(), 50));

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
            Log.i(TAG, "getting paddocks");
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {
                        getpaddocks = new ArrayList<Paddock>(paddocks);
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

    private void setPreferences(String string, String i){

        SharedPreferences sharedPref = sharedContext.getSharedPreferences("FarmApp", 0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString(string, i);

        prefEditor.commit();

    }





}

