package com.lxdnz.nz.myfarmapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.fragments.PaddockAddFragment;
import com.lxdnz.nz.myfarmapp.helpers.Constants;


import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by alex on 27/02/16.
 */
public class MapDialog extends DialogFragment implements OnClickListener{

    // UI references
    private Spinner mapPaddock;
    private LinearLayout submitLayout;
    private TextView mappedArea;
    private EditText mapNameETxt;
    private EditText mapCurrentCoverDateETxt;
    private EditText mapCurrentCoverEtxt;
   // private TextView mapPreviousCoverDateEtxt;

    public static final String ARG_ITEM_ID = "map_dialog";

    private double area;
    private String points;
    private AlertDialog.Builder newPaddockBuilder;
    private AlertDialog.Builder existingPaddockBuilder;
    private GetPaddockTask paddockTask;
    private Paddock Updatepaddock;
    private int sectionNumber;
    private int defaultcover = Constants.getDefaultcover();
    private String TAG = "MapDialog";

    PaddockAO paddockAO;
    Paddock paddock = null;
    private MapPadTask mapPadTask;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;
    Calendar prevCalendar;
    private int prevmonth;
    private int prevyear;

    public MapDialog(){

    }

    public interface MapDialogListener {
        void onFinishDialog(int sectionNumber);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Bundle bundle=this.getArguments();
        area = bundle.getDouble("mappedArea");
        points = bundle.getString("mappedPoints");
        sectionNumber = bundle.getInt(Constants.ARG_SECTION_NUMBER);
        Log.i(TAG, " OnCreate MapDialog sectionNumber:"+sectionNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customDialogView = inflater.inflate(R.layout.dialog_map, null);
        builder.setView(customDialogView);
        mappedArea = (TextView) customDialogView.findViewById(R.id.get_map_area);
        mappedArea.append(": " + area + "ha mapped");

        //mapNewPaddock();
        mapExistingPaddock();

        builder.setTitle("Add Map to Paddock");
        builder.setCancelable(false);
        builder.setPositiveButton("New Paddock",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        mapNewPaddock();
                        //newPaddockBuilder.show();

                    }
                });
        builder.setNeutralButton("Existing Paddock",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        existingPaddockBuilder.show();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();

    return alertDialog;
    }

    private void mapNewPaddock() {
    // TODO implement new paddock like PaddockAddFragment
        paddockAO = new PaddockAO(getActivity());
        final int mapped = 1;
        newPaddockBuilder = new AlertDialog.Builder(getActivity());
        newPaddockBuilder.setTitle(R.string.add_paddock);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customDialogView = inflater.inflate(R.layout.dialog_map_new, null);
        newPaddockBuilder.setView(customDialogView);
        findViewsById(customDialogView);
        setListeners();

        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit_new_map);
        submitLayout.setVisibility(View.GONE);

        paddockTask = new GetPaddockTask(getActivity());

        newPaddockBuilder.setCancelable(false);
        newPaddockBuilder.setPositiveButton(R.string.add_paddock,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setPaddock();
                        mapPadTask = new MapPadTask(getActivity());
                        mapPadTask.execute((Void) null);
                        MainActivity activity = (MainActivity) paddockTask.activityWeakRef.get();
                        activity.onFinishDialog(sectionNumber);
                    }
                });

        newPaddockBuilder.setNeutralButton(R.string.reset, null);
              /*  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetAllFields();

                    }
                }); */

        newPaddockBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        MainActivity activity = (MainActivity) paddockTask.activityWeakRef.get();
                        activity.onFinishDialog(sectionNumber);
                    }
                });
        final AlertDialog dialog = newPaddockBuilder.create();
        dialog.show();
        // Get the button from the view.
        Button dialogButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

// Set the onClickListener here, in the view.
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetAllFields();
                // Dialog will not get dismissed until you call dismiss() explicitly.

            }

        });


    }

    private void mapExistingPaddock() {
        // TODO implement update paddock like CoverUpdateDialog
        paddockAO = new PaddockAO(getActivity());
        final int mapped = 1;
        existingPaddockBuilder = new AlertDialog.Builder(getActivity());
        existingPaddockBuilder.setTitle(R.string.update_pad);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customDialogView = inflater.inflate(R.layout.dialog_map_existing,
                null);
        existingPaddockBuilder.setView(customDialogView);

        mapPaddock = (Spinner) customDialogView.findViewById(R.id.spinner_paddock);
        mappedArea = (TextView) customDialogView.findViewById(R.id.get_map_area);

        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit_existingmap);
        submitLayout.setVisibility(View.GONE);

        paddockTask = new GetPaddockTask(getActivity());
        paddockTask.execute((Void) null);

        spinlistener();


        existingPaddockBuilder.setCancelable(false);
        existingPaddockBuilder.setPositiveButton(R.string.update,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Updatepaddock.setMapped(mapped);
                        Updatepaddock.setArea(area);
                        Updatepaddock.setPolyPoints(points);
                        Log.i(TAG, "Update paddock section Number:"+sectionNumber);

                        long result = paddockAO.updatePaddock(Updatepaddock);
                        if (result > 0) {
                            Toast.makeText(paddockTask.activityWeakRef.get(),
                                    "Paddock Boundary added to map",
                                    Toast.LENGTH_SHORT).show();
                            MainActivity activity = (MainActivity) paddockTask.activityWeakRef.get();
                            activity.onFinishDialog(sectionNumber);
                        } else {
                            Toast.makeText(paddockTask.activityWeakRef.get(),
                                    "Unable to update paddock",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        existingPaddockBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        existingPaddockBuilder.create();

    }




    private void setValue(Paddock paddock) {
        if (paddock != null) {
            mappedArea.setText(R.string.area);
            mappedArea.append(": "+area+"ha mapped");
        }
    }

    public class GetPaddockTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Activity> activityWeakRef;
        private List<Paddock> paddocks;

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

                ArrayAdapter<Paddock> adapter = new ArrayAdapter<Paddock>(
                        activityWeakRef.get(),
                        android.R.layout.simple_spinner_item, paddocks);
                mapPaddock.setAdapter(adapter);

            }
        }
    }

    private void spinlistener(){

        mapPaddock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Paddock paddock = (Paddock) adapterView.getItemAtPosition(i);
                setValue(paddock);
                Updatepaddock = paddock;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }

    private void setListeners() {

        mapCurrentCoverDateETxt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);
                        prevCalendar = Calendar.getInstance();
                        if (monthOfYear == 1){
                            prevmonth = 12;
                            prevyear = year - 1;
                        }else{
                            prevmonth = monthOfYear - 1;
                            prevyear = year;
                        }
                        prevCalendar.set(prevyear, prevmonth, dayOfMonth);

                        mapCurrentCoverDateETxt.setText(formatter.format(dateCalendar
                                .getTime()));
                      //  mapPreviousCoverDateEtxt.setText(formatter.format(prevCalendar.getTime()));


                    }

                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {
        if (view == mapCurrentCoverDateETxt) {
            datePickerDialog.show();
        }
    }

    protected void resetAllFields() {
        mapNameETxt.setText("");
        mapCurrentCoverDateETxt.setText("");
        mapCurrentCoverEtxt.setText("");

    }

    private void setPaddock() {

        paddock = new Paddock();
        if (mapNameETxt.getText().toString().isEmpty()){
            paddock.setPaddockName("New Paddock");
        }else{
            paddock.setPaddockName(mapNameETxt.getText().toString());}

            paddock.setArea(area);

        if (mapCurrentCoverEtxt.getText().toString().isEmpty()){
            paddock.setCurrentCover(defaultcover);
        }else{
            paddock.setCurrentCover(Integer.parseInt(mapCurrentCoverEtxt.getText().toString()));}
        if (mapCurrentCoverDateETxt.getText().toString().isEmpty()){
            //  Log.i(TAG, "currentcover date is empty ");
            if (dateCalendar != null) {
                //   Log.i(TAG, "empty string dateC not null");
                paddock.setCurrentCoverDate(dateCalendar.getTime());
            }else if (dateCalendar == null) {
                //  Log.i(TAG, "dateC null");
                dateCalendar = Calendar.getInstance();
                paddock.setCurrentCoverDate(dateCalendar.getTime());
                prevCalendar = Calendar.getInstance();
                prevCalendar.add(Calendar.MONTH, -1);
              //  mapPreviousCoverDateEtxt.setText(formatter.format(prevCalendar.getTime()));

            }
        }else if (dateCalendar != null){
            //  Log.i(TAG, "date Calendar not null");
            paddock.setCurrentCoverDate(dateCalendar.getTime());
        }
        /*
        if (mapPreviousCoverDateEtxt.getText().toString().isEmpty()){
            //  Log.i(TAG, "prev cover date string empty");
            if (prevCalendar != null) {
                // Log.i(TAG, "prevCalendar not null");
                paddock.setPreviousCoverDate(prevCalendar.getTime());
                paddock.setLastGrazed(prevCalendar.getTime());
            }
        }else */
        if (prevCalendar != null) {
            paddock.setPreviousCoverDate(prevCalendar.getTime());
            paddock.setLastGrazed(prevCalendar.getTime());
        }
        paddock.setPreviousCover(defaultcover);
        paddock.setGrazing(0);
        paddock.setMapped(1);
        paddock.setPolyPoints(points);
    }

    private void findViewsById(View rootView) {
        mapNameETxt = (EditText) rootView.findViewById(R.id.map_txt_paddock_name);
        mappedArea = (TextView) rootView.findViewById(R.id.map_txt_area);
        mappedArea.append(": "+area+"ha mapped");
        mapCurrentCoverDateETxt = (EditText) rootView.findViewById(R.id.map_txt_currentcoverdate);
        mapCurrentCoverDateETxt.setInputType(InputType.TYPE_NULL);
        mapCurrentCoverEtxt = (EditText) rootView.findViewById(R.id.map_txt_currentcover);

    }

    public class MapPadTask extends AsyncTask<Void, Void, Long> {

        private final WeakReference<Activity> activityWeakRef;

        public MapPadTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected Long doInBackground(Void... arg0) {
            long result = paddockAO.addPaddock(paddock);
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {
                if (result != -1)
                    Toast.makeText(activityWeakRef.get(), "Paddock Saved",
                            Toast.LENGTH_LONG).show();
            }
        }
    }

}
