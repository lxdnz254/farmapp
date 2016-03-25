package com.lxdnz.nz.myfarmapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.PaddockActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.Enums;

/**
 * Created by alex on 25/07/15.
 */
public class CustomPadDialogFragment extends DialogFragment{

    // UI references
    private EditText padNameEtxt;
    private EditText padAreaEtxt;
    private LinearLayout submitLayout;

    private Paddock paddock;
    private int sectionNumber;

    PaddockAO paddockAO;

    public static final String ARG_ITEM_ID = "pad_dialog_fragment";

    public interface PadDialogFragmentListener {
        void onFinishDialog(int sectionNumber);
    }

    public CustomPadDialogFragment() {

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        paddockAO = new PaddockAO(getActivity());

        Bundle bundle = this.getArguments();
        paddock = bundle.getParcelable("selectedPaddock");
        sectionNumber = bundle.getInt(Constants.ARG_SECTION_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customDialogView = inflater.inflate(R.layout.fragment_paddock_add,
                null);
        builder.setView(customDialogView);

        padNameEtxt = (EditText) customDialogView.findViewById(R.id.etxt_paddock_name);
        padAreaEtxt = (EditText) customDialogView
                .findViewById(R.id.etxt_area);
        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit);
        submitLayout.setVisibility(View.GONE);

        setValue();

        builder.setTitle(R.string.update_pad);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.update,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {



                        paddock.setPaddockName(padNameEtxt.getText().toString());
                        paddock.setArea(Double.parseDouble(padAreaEtxt
                                .getText().toString()));
                        long result = paddockAO.updatePaddock(paddock);
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

    private void setValue() {
        if (paddock != null) {
            padNameEtxt.setText(paddock.getPaddockName());
            padAreaEtxt.setText(paddock.getArea() + "");

        }
    }
}
