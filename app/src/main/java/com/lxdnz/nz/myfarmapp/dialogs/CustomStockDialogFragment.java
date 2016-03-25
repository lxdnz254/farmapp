package com.lxdnz.nz.myfarmapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.StockActivity;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.databases.Stock;
import com.lxdnz.nz.myfarmapp.databases.StockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.Enums;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alex on 28/07/15.
 */
public class CustomStockDialogFragment extends DialogFragment {

    // UI references
    private EditText stockNameEtxt;
    private EditText stockQuantityEtxt;
    private EditText stockDailyfeedEtxt;
    private EditText stockSupKgEtxt;
    private CheckBox stockSupplementBox;
    private TextView stockGrasskgTxt;
    private Spinner stockPaddockInSpinner;
    private EditText stockAreaUsingEtxt;
    private LinearLayout submitLayout;


    private int checksupplement = 0;

    private Stock stock;

    private int sectionNumber;





    StockAO stockAO;
    ArrayAdapter<Paddock> adapter;

    public static final String ARG_ITEM_ID = "stock_dialog_fragment";

    public interface StockDialogFragmentListener {
        void onFinishDialog(int sectionNumber);
    }

    public CustomStockDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        stockAO = new StockAO(getActivity());

        Bundle bundle = this.getArguments();
        stock = bundle.getParcelable("selectedStock");

        sectionNumber = bundle.getInt(Constants.ARG_SECTION_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customDialogView = inflater.inflate(R.layout.fragment_stock_add,
                null);
        builder.setView(customDialogView);

        stockNameEtxt = (EditText) customDialogView.findViewById(R.id.etxt_stock_name);
        stockQuantityEtxt = (EditText) customDialogView
                .findViewById(R.id.etxt_quantity);
        stockDailyfeedEtxt = (EditText) customDialogView.findViewById(R.id.etxt_dailyfeed);
        stockSupplementBox = (CheckBox) customDialogView.findViewById(R.id.echeckbox_supp);
                stockSupKgEtxt = (EditText) customDialogView.findViewById(R.id.etxt_suppkg);
        stockGrasskgTxt = (TextView) customDialogView.findViewById(R.id.etxt_grasskg);
        stockPaddockInSpinner = (Spinner) customDialogView.findViewById(R.id.spinner_paddock);
        stockAreaUsingEtxt = (EditText) customDialogView.findViewById(R.id.etxt_stock_areausing);
        submitLayout = (LinearLayout) customDialogView
                .findViewById(R.id.layout_submit);
        submitLayout.setVisibility(View.GONE);

        setValue();
        checkboxClickListener();

        builder.setTitle(R.string.update_stock);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.update,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (stockNameEtxt.getText().toString().isEmpty()) {
                            stock.setStockName("Mob default");
                        }else {
                            stock.setStockName(stockNameEtxt.getText().toString());
                        }
                        if (stockQuantityEtxt.getText().toString().isEmpty()) {
                            stock.setQuantity(1);
                        }else {
                            stock.setQuantity(Integer.parseInt(stockQuantityEtxt.getText()
                                    .toString()));
                        }
                        if (stockDailyfeedEtxt.getText().toString().isEmpty()) {
                            stock.setDailyfeed(0);
                        }else {
                            stock.setDailyfeed(Double.parseDouble(stockDailyfeedEtxt.getText().toString()));
                        }
                        stock.setSupplement(checksupplement);
                        if (stockSupKgEtxt.getText().toString().isEmpty()) {
                            stock.setSupKg(0);
                        }else{
                            stock.setSupKg(Double.parseDouble(stockSupKgEtxt.getText().toString()));
                        }
                        stock.setGrassKg();
                        if (stockAreaUsingEtxt.getText().toString().isEmpty()) {
                            stock.setAreaUsing(0);
                        }else {
                            stock.setAreaUsing(Double.parseDouble(stockAreaUsingEtxt.getText().toString()));
                        }
                        Paddock selectedPaddock = (Paddock) stockPaddockInSpinner.getSelectedItem();
                        stock.setPaddock(selectedPaddock);
                        long result = stockAO.updateStock(stock);
                        if (result > 0) {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.onFinishDialog(sectionNumber);
                        } else {
                            Toast.makeText(getActivity(),
                                    "Unable to update stock",
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

        PaddockAO paddockAO = new PaddockAO(getActivity());
        List<Paddock> paddocks = paddockAO.getPaddocks();
        adapter = new ArrayAdapter<Paddock>(getActivity(),
                android.R.layout.simple_spinner_item, paddocks);
        stockPaddockInSpinner.setAdapter(adapter);
        int pos = adapter.getPosition(stock.getPaddock());
        if (stock != null) {
            stockNameEtxt.setText(stock.getStockName());
            stockQuantityEtxt.setText(stock.getQuantity() + "");
            stockDailyfeedEtxt.setText(stock.getDailyfeed()+"");
            checksupplement = stock.getSupplement();
            if (checksupplement != 0) stockSupplementBox.setChecked(true);
            else{stockSupplementBox.setChecked(false);}
            stockSupKgEtxt.setText(stock.getSupKg() + "");
            stockGrasskgTxt.setText(R.string.grasskg);
            stockGrasskgTxt.append(stock.getGrassKg() + "");
            stockAreaUsingEtxt.setText(stock.getAreaUsing() + "");
            stockPaddockInSpinner.setSelection(pos);

        }
    }



    public void checkboxClickListener() {


        stockSupplementBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                boolean checked = ((CheckBox) view).isChecked();
                if (checked) {
                    if (checksupplement == 0) {
                        checksupplement++;

                        Toast.makeText(getActivity(), "Feeding Supplement", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    checksupplement = 0;

                    Toast.makeText(getActivity(), "Not Feeding Supplement", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
