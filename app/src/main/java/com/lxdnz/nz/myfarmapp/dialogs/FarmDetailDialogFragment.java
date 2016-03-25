package com.lxdnz.nz.myfarmapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.databases.StockAO;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by alex on 15/08/15.
 */
public class FarmDetailDialogFragment extends DialogFragment {

    PaddockAO paddockAO;
    StockAO stockAO;

    private TextView farmTitle;
    private TextView farmSizeTxt;
    private TextView farmStockNumbersTxt;
    private TextView farmCoverTxt;
    private TextView farmGrowthTxt;
    private TextView farmAreaUsingTxt;
    private TextView farmDemandTxt;
    private TextView farmRoundTxt;


    public FarmDetailDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        paddockAO = new PaddockAO(getActivity());
        stockAO = new StockAO(getActivity());


        View rootView = inflater.inflate(R.layout.fragment_farm_details, container,
                false);
        getDialog().setTitle("Farm Details");
        findViewsById(rootView);

        return rootView;
    }

    private void findViewsById(View rootView){

        double farmround = paddockAO.TotalArea() / stockAO.TotalStockArea();
        double farmcover = paddockAO.AverageCover();
        double farmareusing = stockAO.TotalStockArea();
        double farmarea = paddockAO.TotalArea();
        double farmgrowth = paddockAO.FarmGrowth();
        double farmdemand = stockAO.StockDemand()/paddockAO.TotalArea();

        if (Double.isInfinite(farmround) || Double.isNaN(farmround)) {farmround = 0;}
        if (Double.isInfinite(farmcover) || Double.isNaN(farmcover)) {farmcover = 0;}
        if (Double.isInfinite(farmareusing) || Double.isNaN(farmareusing)) {farmareusing = 0;}
        if (Double.isInfinite(farmarea) || Double.isNaN(farmarea)) {farmarea = 0;}
        if (Double.isInfinite(farmgrowth) || Double.isNaN(farmgrowth)) {farmgrowth = 0;}
        if (Double.isInfinite(farmdemand) || Double.isNaN(farmdemand)) {farmdemand = 0;}

        BigDecimal bfr = new BigDecimal(farmround).setScale(0, RoundingMode.HALF_EVEN);
        BigDecimal bfc = new BigDecimal(farmcover).setScale(0, RoundingMode.HALF_EVEN);
        BigDecimal bfa = new BigDecimal(farmareusing).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal bfar = new BigDecimal(farmarea).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal bgrowth = new BigDecimal(farmgrowth).setScale(0, RoundingMode.HALF_EVEN);
        BigDecimal bdemand = new BigDecimal(farmdemand).setScale(0, RoundingMode.HALF_EVEN);

        farmTitle = (TextView) rootView.findViewById(R.id.farm_det_welcome);
        farmSizeTxt = (TextView) rootView.findViewById(R.id.farm_det_farmsize);
        farmStockNumbersTxt = (TextView) rootView.findViewById(R.id.farm_det_farmstock);
        farmCoverTxt = (TextView) rootView.findViewById(R.id.farm_det_farmcover);
        farmGrowthTxt = (TextView) rootView.findViewById(R.id.farm_det_farmgrowth);
        farmAreaUsingTxt = (TextView) rootView.findViewById(R.id.farm_det_farmstock_using);
        farmDemandTxt = (TextView) rootView.findViewById(R.id.farm_det_farmstock_demand);
        farmRoundTxt = (TextView) rootView.findViewById(R.id.farm_det_farmround);

        farmTitle.setText("");
        farmSizeTxt.append(bfar+"ha");
        farmStockNumbersTxt.append(stockAO.TotalStock()+"");
        farmCoverTxt.append(bfc+" kg/DM/ha");
        farmGrowthTxt.append(bgrowth+"kg/day");
        farmAreaUsingTxt.append(bfa+"ha");
        farmDemandTxt.append(bdemand+"kg/day");
        farmRoundTxt.append(" " + bfr + " day round");
    }
}
