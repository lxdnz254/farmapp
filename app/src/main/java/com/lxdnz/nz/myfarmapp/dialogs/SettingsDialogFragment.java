package com.lxdnz.nz.myfarmapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.helpers.Constants;

/**
 * Created by alex on 26/08/15.
 */
public class SettingsDialogFragment extends DialogFragment {

    private int targetresidual = Constants.getTargetresidual();
    private int defaultcover = Constants.getDefaultcover();
    private int sectionNumber;
    private TextView target;
    private TextView cover;
    private EditText newtarget;
    private EditText newcover;
    private LinearLayout submitLayout;

    public static final String ARG_ITEM_ID = "settings_dialog_fragment";
    public static final String TAG = "settings dialog";



    public SettingsDialogFragment(){

    }

    public interface SetDialogFragmentListener {
        void onFinishDialog(int sectionNumber);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");
        Bundle bundle = this.getArguments();
        sectionNumber = bundle.getInt(Constants.ARG_SECTION_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View customDialogView = inflater.inflate(R.layout.dialog_settings,
                null);
        builder.setView(customDialogView);

        target = (TextView) customDialogView.findViewById(R.id.get_target_residual);
        cover = (TextView) customDialogView.findViewById(R.id.get_default_cover);
        target.append(targetresidual+"");
        cover.append(defaultcover+"");

        newtarget = (EditText) customDialogView.findViewById(R.id.set_target_residual);
        newcover = (EditText) customDialogView
                .findViewById(R.id.set_default_cover);
        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit_settings);
        submitLayout.setVisibility(View.GONE);


        builder.setTitle(R.string.update_settings);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.update,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String targetString = newtarget.getText().toString();
                        String coverString = newcover.getText().toString();

                        if (targetString.length() > 0) {
                            Constants.setTargetresidual(Integer.parseInt(targetString));
                        }else{
                            Constants.setTargetresidual(targetresidual);

                        }
                        if (coverString.length() > 0) {
                            Constants.setDefaultcover(Integer.parseInt(coverString));
                        }else{
                            Constants.setDefaultcover(defaultcover);

                        }


                            MainActivity activity = (MainActivity) getActivity();
                            activity.onFinishDialog(sectionNumber);

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

}
