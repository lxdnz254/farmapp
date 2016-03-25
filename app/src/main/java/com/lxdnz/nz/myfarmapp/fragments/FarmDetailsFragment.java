package com.lxdnz.nz.myfarmapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.databases.StockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Created by alex on 13/08/15.
 */
public class FarmDetailsFragment extends Fragment {

    PaddockAO paddockAO;
    StockAO stockAO;
    public int sectionNumber = 0;

    public static final String ARG_ITEM_ID = "farm_details_fragment";

    private TextView farmSizeTxt;
    private TextView farmStockNumbersTxt;
    private TextView farmCoverTxt;
    private TextView farmGrowthTxt;
    private TextView farmAreaUsingTxt;
    private TextView farmDemandTxt;
    private TextView farmRoundTxt;


    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        paddockAO = new PaddockAO(getActivity());
        stockAO = new StockAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_farm_details, container,
                false);

        findViewsById(rootView);

        return rootView;
    }

    public static FarmDetailsFragment newInstance(int sectionNumber) {
        FarmDetailsFragment fragment = new FarmDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.action_farm);
        getActionBar().setTitle(R.string.action_farm);
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

    }


    public void findViewsById(View rootView) {

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

        farmSizeTxt = (TextView) rootView.findViewById(R.id.farm_det_farmsize);
        farmStockNumbersTxt = (TextView) rootView.findViewById(R.id.farm_det_farmstock);
        farmCoverTxt = (TextView) rootView.findViewById(R.id.farm_det_farmcover);
        farmGrowthTxt = (TextView) rootView.findViewById(R.id.farm_det_farmgrowth);
        farmAreaUsingTxt = (TextView) rootView.findViewById(R.id.farm_det_farmstock_using);
        farmDemandTxt = (TextView) rootView.findViewById(R.id.farm_det_farmstock_demand);
        farmRoundTxt = (TextView) rootView.findViewById(R.id.farm_det_farmround);



        farmSizeTxt.append(bfar+"ha");
        farmStockNumbersTxt.append(stockAO.TotalStock()+"");
        farmCoverTxt.append(bfc+" kg/DM/ha");
        farmGrowthTxt.append(bgrowth+"kg/day");
        farmAreaUsingTxt.append(bfa+"ha");
        farmDemandTxt.append(bdemand+"kg/day");
        farmRoundTxt.append(" " + bfr + " day round");





    }
}
