package com.lxdnz.nz.myfarmapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
public class PaddockAddFragment extends Fragment implements OnClickListener{

    // UI references
    private EditText padNameEtxt;
    private EditText padAreaEtxt;
    private EditText padCurrentCoverDateEtxt;
    private EditText padCurrentCoverEtxt;
    private TextView padPreviousCoverDateEtxt;
    private TextView padPreviousCoverTxt;
    private int defaultcover = Constants.getDefaultcover();


    private Button addButton;
    private Button resetButton;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;
    Calendar prevCalendar;
    private int prevmonth;
    private int prevyear;



    Paddock paddock = null;
    private PaddockAO paddockAO;
    private AddPadTask task;

    public static final String ARG_ITEM_ID = "pad_add_fragment";
  //  public String TAG = "Paddock Add Frag";
    public int sectionNumber = 2;

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        paddockAO = new PaddockAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paddock_add, container,
                false);

        findViewsById(rootView);

        setListeners();

        // Used for orientation change
        /*
         * After entering the fields, change the orientation.
         * NullPointerException occurs for date. This piece of code avoids it.
         */
        if (savedInstanceState != null) {
            dateCalendar = Calendar.getInstance();
            if (savedInstanceState.getLong("dateCalendar") != 0)
                dateCalendar.setTime(new Date(savedInstanceState
                        .getLong("dateCalendar")));
            prevCalendar = Calendar.getInstance();
            if (savedInstanceState.getLong("prevCalendar") != 0)
                prevCalendar.setTime(new Date(savedInstanceState.
                        getLong("prevCalendar")));
        }


        return rootView;
    }

    public static PaddockAddFragment newInstance(int sectionNumber) {
        PaddockAddFragment fragment = new PaddockAddFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void setListeners() {

        padCurrentCoverDateEtxt.setOnClickListener(this);
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

                        padCurrentCoverDateEtxt.setText(formatter.format(dateCalendar
                                .getTime()));
                        padPreviousCoverDateEtxt.setText(formatter.format(prevCalendar.getTime()));


                    }

                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));


        addButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    protected void resetAllFields() {
        padNameEtxt.setText("");
        padAreaEtxt.setText("");
        padCurrentCoverDateEtxt.setText("");
        padCurrentCoverEtxt.setText("");

    }

    private void setPaddock() {


        paddock = new Paddock();
        if (padNameEtxt.getText().toString().isEmpty()){
            paddock.setPaddockName("New Paddock");
        }else{
        paddock.setPaddockName(padNameEtxt.getText().toString());}
        if (padAreaEtxt.getText().toString().isEmpty()){
            paddock.setArea(0);
        }else{
        paddock.setArea(Double.parseDouble(padAreaEtxt.getText()
                .toString()));}
        if (padCurrentCoverEtxt.getText().toString().isEmpty()){
            paddock.setCurrentCover(defaultcover);
        }else{
        paddock.setCurrentCover(Integer.parseInt(padCurrentCoverEtxt.getText().toString()));}
        if (padCurrentCoverDateEtxt.getText().toString().isEmpty()){
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
                padPreviousCoverDateEtxt.setText(formatter.format(prevCalendar.getTime()));

            }
        }else if (dateCalendar != null){
          //  Log.i(TAG, "date Calendar not null");
            paddock.setCurrentCoverDate(dateCalendar.getTime());
        }


        if (padPreviousCoverDateEtxt.getText().toString().isEmpty()){
          //  Log.i(TAG, "prev cover date string empty");
           if (prevCalendar != null) {
              // Log.i(TAG, "prevCalendar not null");
               paddock.setPreviousCoverDate(prevCalendar.getTime());
               paddock.setLastGrazed(prevCalendar.getTime());
           }
        }else if (prevCalendar != null) {
            paddock.setPreviousCoverDate(prevCalendar.getTime());
            paddock.setLastGrazed(prevCalendar.getTime());
        }
        if (padPreviousCoverTxt.getText().toString().isEmpty()){
            paddock.setPreviousCover(defaultcover);
        }
        paddock.setGrazing(0);
        paddock.setMapped(0);
        paddock.setPolyPoints("");
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.add_paddock);
        getActionBar().setTitle(R.string.add_paddock);
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
        if (dateCalendar != null)
            outState.putLong("dateCalendar", dateCalendar.getTime().getTime());
        if (prevCalendar != null)
            outState.putLong("prevCalendar", prevCalendar.getTime().getTime());
    }


    private void findViewsById(View rootView) {
        padNameEtxt = (EditText) rootView.findViewById(R.id.etxt_paddock_name);
        padAreaEtxt = (EditText) rootView.findViewById(R.id.etxt_area);
        padCurrentCoverDateEtxt = (EditText) rootView.findViewById(R.id.etxt_currentcoverdate);
        padCurrentCoverDateEtxt.setInputType(InputType.TYPE_NULL);
        padCurrentCoverEtxt = (EditText) rootView.findViewById(R.id.etxt_currentcover);
        padPreviousCoverDateEtxt = (TextView) rootView.findViewById(R.id.txt_previouscoverdate);
        padPreviousCoverTxt = (TextView) rootView.findViewById(R.id.txt_previouscover);


        addButton = (Button) rootView.findViewById(R.id.button_add);
        resetButton = (Button) rootView.findViewById(R.id.button_reset);
    }

    @Override
    public void onClick(View view) {
        if (view == padCurrentCoverDateEtxt) {
            datePickerDialog.show();
        } else if (view == addButton) {
            setPaddock();

            task = new AddPadTask(getActivity());
            task.execute((Void) null);
        } else if (view == resetButton) {
            resetAllFields();
        }
    }

    public class AddPadTask extends AsyncTask<Void, Void, Long> {

        private final WeakReference<Activity> activityWeakRef;

        public AddPadTask(Activity context) {
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
