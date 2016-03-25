package com.lxdnz.nz.myfarmapp.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.FarmDbAO;
import com.lxdnz.nz.myfarmapp.databases.FarmDbHandler;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.SqlToXls;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by alex on 17/02/16.
 */
public class FarmWalkEmailDialogFragment extends DialogFragment{

    private EditText email;
    private String emailaddress;
  //  private Cursor cursor;
    private int sectionNumber;
    public static final String ARG_ITEM_ID = "farmwalk_dialog_fragment";
    public static final String TAG = "farmwalk dialog";

    SqlToXls sqlHelper;

    public FarmWalkEmailDialogFragment(){

    }

    public interface FarmWalkDialogFragmentListener {
        void onFinishDialog(int sectionNumber);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sqlHelper = new SqlToXls(getActivity());
        Log.i(TAG, "onCreateDialog");
        Bundle bundle = this.getArguments();
        sectionNumber = bundle.getInt(Constants.ARG_SECTION_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View customDialogView = inflater.inflate(R.layout.dialog_farmwalk,
                null);
        builder.setView(customDialogView);

        email = (EditText) customDialogView.findViewById(R.id.farmwalk_email);




        builder.setTitle(R.string.action_xls);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.createxls,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "enter create XLS");


                        sqlHelper.createXls();



                        MainActivity activity = (MainActivity) getActivity();
                        activity.onFinishDialog(sectionNumber);

                    }
                });
        builder.setNeutralButton(R.string.sendxls,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "enter send XLS to " + email);
                        sendXls(email.getText().toString());
                    }
                });

        AlertDialog alertDialog = builder.create();

        return alertDialog;


    }

    /**
     * Sends the XLS file by email
     * put in seperate thread to use in
     * other java calls.
     *
     * @param ??
     */

    public void sendXls(String emailaddress) {

        Log.i(TAG, "sending email to "+emailaddress);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailaddress});
        i.putExtra(Intent.EXTRA_SUBJECT, "Data from app");
        i.putExtra(Intent.EXTRA_TEXT   , "experience number x");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "stockfeeder.farmwalk/FarmWalk.xls"));
        i.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(i, "Send email..."));
    }


}
