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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.lxdnz.nz.myfarmapp.helpers.Constants;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by alex on 28/08/15.
 */
public class CoverUpdateDialogFragment extends DialogFragment implements OnClickListener{

    // UI references
    private EditText coverUpdateEtxt;
    private TextView currentCoverTxt;
    private EditText coverUpdateDateEtxt;
    private TextView currentCoverDateTxt;
    private LinearLayout submitLayout;
    private Spinner coverPaddock;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    private GetPaddockTask paddockTask;

    private Paddock Updatepaddock;
    private int sectionNumber;
    private int currentcover;
    private Date currentcoverdate;
    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    PaddockAO paddockAO;

    public static final String ARG_ITEM_ID = "cover_dialog_fragment";

    public interface CoverDialogFragmentListener {
        void onFinishDialog(int sectionNumber);
    }

    public CoverUpdateDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        paddockAO = new PaddockAO(getActivity());

        Bundle bundle = this.getArguments();

        sectionNumber = bundle.getInt(Constants.ARG_SECTION_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customDialogView = inflater.inflate(R.layout.dialog_cover,
                null);
        builder.setView(customDialogView);

        coverPaddock = (Spinner) customDialogView.findViewById(R.id.spinner_paddock);

        coverUpdateEtxt = (EditText) customDialogView.findViewById(R.id.set_cover);
        currentCoverTxt = (TextView) customDialogView
                .findViewById(R.id.get_cover);
        coverUpdateDateEtxt = (EditText) customDialogView.findViewById(R.id.set_date);
        coverUpdateDateEtxt.setInputType(InputType.TYPE_NULL);
        currentCoverDateTxt = (TextView) customDialogView.findViewById(R.id.get_date);
        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit_cover);
        submitLayout.setVisibility(View.GONE);

        paddockTask = new GetPaddockTask(getActivity());
        paddockTask.execute((Void) null);

        spinlistener();
        datepicker();

        builder.setTitle(R.string.update_cover);
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
                            MainActivity activity = (MainActivity) getActivity();
                            activity.onFinishDialog(sectionNumber);
                        } else {
                            Toast.makeText(getActivity(),
                                    "Unable to update paddock",
                                    Toast.LENGTH_SHORT).show();
                        }
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
                coverPaddock.setAdapter(adapter);

            }
        }
    }

    private void spinlistener(){

        coverPaddock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                Paddock paddock = (Paddock) adapterView.getItemAtPosition(i);
                setValue(paddock);
                Updatepaddock = paddock;
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }

    private void datepicker(){

        coverUpdateDateEtxt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year, monthOfYear, dayOfMonth);


                        coverUpdateDateEtxt.setText(formatter.format(dateCalendar
                                .getTime()));



                    }

                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));


    }

    @Override
    public void onClick(View view) {
        if (view == coverUpdateDateEtxt) {
            datePickerDialog.show();
        }
    }




}
