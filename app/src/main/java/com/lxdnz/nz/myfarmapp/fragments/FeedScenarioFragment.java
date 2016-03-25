package com.lxdnz.nz.myfarmapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.databases.Stock;
import com.lxdnz.nz.myfarmapp.databases.StockAO;
import com.lxdnz.nz.myfarmapp.dialogs.CustomStockDialogFragment;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.Enums;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * Created by alex on 20/08/15.
 */
public class FeedScenarioFragment extends Fragment implements OnClickListener {

    String TAG = "Feed Scenario";

    int grazingresidual = Constants.getTargetresidual();


    private TextView txtstockarea;
    private TextView txtstockfeed;
    private TextView txtpaddockcover;
    private TextView txtstockneed;
    private TextView txtstocksupyn;
    private TextView txtstocksupplement;

    private Spinner feedpaddock;
    private Spinner feedstock;

    private Button feednosupplement;
    private Button feedchangesupplement;
    private Button feedcurrentsupplement;
    private Button adjuststock;

    private Paddock paddock;
    private Stock stockChange;
    private Stock stockUpdate;



    double availKgperha;
    double stockneedArea;
    double stockKg;
    double stockEating;
    double paddockArea;
    int stockQuantity;
    int checksup;
    double stocksup;
    double stockgrass;
    double stockGrassKg;
    double stockneedGrassArea;
    double stockneedSupplement;
    double needSupplementperAnimal;

    double stockavail;
    double stockarea;
    double updateArea;
    double updateSupKg;
    double updateGrassArea;

    PaddockAO paddockAO;
    StockAO stockAO;
    public int sectionNumber = 5;

    int spinstockvalue;
    int spinpaddockvalue;


    private GetPaddockTask paddockTask;
    private GetStockTask stockTask;

    public static final String ARG_ITEM_ID = "feed_scenario_fragment";

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




        View rootView = inflater.inflate(R.layout.fragment_feed_scenario, container,
                false);



        findViewsById(rootView);
        setListeners();

        paddockTask = new GetPaddockTask(getActivity());
        paddockTask.execute((Void) null);
        stockTask = new GetStockTask(getActivity());
        stockTask.execute((Void) null);

        feedpaddock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Your code here
                Paddock paddock = (Paddock) adapterView.getItemAtPosition(i);

                setPreferences("paddockChoice", i);

                paddockArea = paddock.getArea();
                availKgperha = (paddock.getCurrentCover() - grazingresidual);
                if (availKgperha<0){
                    availKgperha = 0;
                }

                double paddockAvailKg = availKgperha * paddock.getArea();
                BigDecimal baKg = new BigDecimal(paddockAvailKg).setScale(1, RoundingMode.HALF_UP);

                if(availKgperha > 0) {
                    stockneedArea = stockKg / availKgperha;
                }else{stockneedArea = 0;}

                BigDecimal bfa = new BigDecimal(stockneedArea).setScale(2, RoundingMode.HALF_EVEN);
                updateArea = bfa.doubleValue();
                if (updateArea == stockarea){
                    stockneedSupplement = 0;
                    needSupplementperAnimal = 0;

                }else {

                    stockneedSupplement = (stockKg - (stockarea * availKgperha));
                    if (stockQuantity != 0) {
                        needSupplementperAnimal = (stockneedSupplement / stockQuantity);
                    } else {
                        needSupplementperAnimal = 0;
                    }
                }

                BigDecimal bfs = new BigDecimal(stockneedSupplement).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal bfn = new BigDecimal(needSupplementperAnimal).setScale(2, RoundingMode.HALF_EVEN);
                updateSupKg = bfn.doubleValue();

                if (checksup == 0){
                    txtstocksupyn.setText(getString(R.string.feed_grass_only));
                    txtstockarea.setText(getString(R.string.feed_current_area) + stockarea + "ha");
                    updateGrassArea = updateArea;
                }else{
                    txtstockarea.setText(getString(R.string.feed_current_area) + stockarea + "ha "
                            + "and receiving " + stocksup + "kg/per animal in supplement");
                    txtstocksupyn.setText(getString(R.string.feedarea_current_sup));
                    stockGrassKg = stockgrass * stockQuantity;
                    if (availKgperha > 0) {
                        stockneedGrassArea = stockGrassKg / availKgperha;
                    }else{
                        stockneedGrassArea = 0;
                    }
                    BigDecimal bfg = new BigDecimal(stockneedGrassArea).setScale(2, RoundingMode.HALF_EVEN);
                    txtstocksupyn.append(bfg+"ha");
                    updateGrassArea = bfg.doubleValue();
                }


                txtpaddockcover.setText("Paddock "+paddock.getPaddockName()
                        + " is "+paddock.getArea()+ "ha big. "
                        + getString(R.string.feedcover) + paddock.getCurrentCover()
                        + ". At the target residual of :" +grazingresidual
                        + " there is :"+baKg + "Kgs available");
                if (stockneedArea != 0) {
                    txtstockneed.setText(getString(R.string.feedarea_no_sup) + bfa + "ha");
                    if (stockneedArea > paddockArea){
                        txtstockneed.append(". This paddock is not big enough! Try another paddock, " +
                                "feed supplement or use the Two Feed Scenario page.");
                    }
                }else{txtstockneed.setText("This paddock is below target residual");
                }
                txtstocksupplement.setText(getString(R.string.feedarea_extra_sup) + bfs +
                        "kgs at " + bfn + "kg per animal");


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        feedstock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Your code here

                Stock stock = (Stock) adapterView.getItemAtPosition(i);

                setPreferences("stockChoice", i);

                stockUpdate = stock;
                stockavail = availKgperha;
                stockarea = stock.getAreaUsing();
                checksup = stock.getSupplement();
                stocksup = stock.getSupKg();
                stockgrass = stock.getGrassKg();


                stockKg = stock.getDailyfeed() * stock.getQuantity();
                BigDecimal bfk = new BigDecimal(stockKg).setScale(2, RoundingMode.HALF_EVEN);
                stockEating = stock.getAreaUsing() * availKgperha;
                stockQuantity = stock.getQuantity();
                if (availKgperha > 0) {
                    stockneedArea = stockKg / availKgperha;
                }else{
                    stockneedArea = 0;
                }
                BigDecimal bfa = new BigDecimal(stockneedArea).setScale(2, RoundingMode.HALF_EVEN);
                updateArea = bfa.doubleValue();
                if (updateArea == stockarea){
                    stockneedSupplement = 0;
                    needSupplementperAnimal = 0;
                }else {
                    stockneedSupplement = stockKg - stockEating;
                    if (stockQuantity != 0) {
                        needSupplementperAnimal = (stockneedSupplement / stockQuantity);
                    } else {
                        needSupplementperAnimal = 0;
                    }

                }

                if (checksup == 0){
                    txtstockarea.setText(getString(R.string.feed_current_area) + stock.getAreaUsing() + "ha");
                    txtstocksupyn.setText(getString(R.string.feed_grass_only));
                    updateGrassArea = updateArea;
                }else{
                    txtstockarea.setText(getString(R.string.feed_current_area) + stock.getAreaUsing() + "ha "
                    + "and receiving " + stock.getSupKg() + "kg/per animal in supplement");
                    txtstocksupyn.setText(getString(R.string.feedarea_current_sup));
                    stockGrassKg = stockgrass * stock.getQuantity();
                    if (availKgperha > 0) {
                        stockneedGrassArea = stockGrassKg / availKgperha;
                    }else{
                        stockneedGrassArea = 0;
                    }
                    BigDecimal bfg = new BigDecimal(stockneedGrassArea).setScale(2, RoundingMode.HALF_EVEN);
                    txtstocksupyn.append(bfg+"ha");
                    updateGrassArea = bfg.doubleValue();
                }

                BigDecimal bfs = new BigDecimal(stockneedSupplement).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal bfn = new BigDecimal(needSupplementperAnimal).setScale(2, RoundingMode.HALF_EVEN);
                updateSupKg = bfn.doubleValue();


                txtstockfeed.setText(getString(R.string.feedstock) + bfk + "KgDM");
                if (stockneedArea !=0) {
                    txtstockneed.setText(getString(R.string.feedarea_no_sup) + bfa + "ha");
                    if (stockneedArea > paddockArea){
                        txtstockneed.append(". This paddock is not big enough! Try another paddock, " +
                                "feed supplement or use the Two Feed Scenario page.");
                    }
                }else{
                    txtstockneed.setText("This paddock is below target residual");
                }
                txtstocksupplement.setText(getString(R.string.feedarea_extra_sup) + bfs +
                        "kgs at " + bfn + "kg per animal");


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });




        return rootView;
    }



    public static FeedScenarioFragment newInstance(int sectionNumber) {
        FeedScenarioFragment fragment = new FeedScenarioFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.title_section5);
        getActionBar().setTitle(R.string.title_section5);
        super.onResume();

        sharedPrefs();

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


        outState.putInt("PaddockSpin", feedpaddock.getSelectedItemPosition());
        outState.putInt("StockSpin", feedstock.getSelectedItemPosition());

    }


    public void findViewsById(View rootView) {

        feedpaddock = (Spinner) rootView.findViewById(R.id.spinner_paddock);
        feedstock = (Spinner) rootView.findViewById(R.id.spinner_stock);

        txtstockarea = (TextView) rootView.findViewById(R.id.feed_current_area);
        txtstockfeed = (TextView) rootView.findViewById(R.id.feed_stock_kg);
        txtpaddockcover = (TextView) rootView.findViewById(R.id.feed_current_cover);
        txtstocksupyn = (TextView) rootView.findViewById(R.id.feed_supyn);
        txtstockneed = (TextView) rootView.findViewById(R.id.feed_area);
        txtstocksupplement = (TextView) rootView.findViewById(R.id.feed_supplement);

        feednosupplement = (Button) rootView.findViewById(R.id.areabutton);
        feedchangesupplement = (Button) rootView.findViewById(R.id.supplementbutton);
        feedcurrentsupplement = (Button) rootView.findViewById(R.id.currentbutton);
        adjuststock = (Button) rootView.findViewById(R.id.adjuststockbutton);



    }

    private void setPreferences(String string, int i){

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FarmApp", 0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt(string, i);

        prefEditor.commit();

    }

    private void sharedPrefs(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FarmApp", Context.MODE_PRIVATE);
        int spinnerStockValue = sharedPref.getInt("stockChoice",-1);

        if(spinnerStockValue != -1) {
            feedstock.setSelection(spinnerStockValue);
        }else{
            feedstock.setSelection(0);
        }
        spinstockvalue = spinnerStockValue;
        int spinnerPaddockValue = sharedPref.getInt("paddockChoice", -1);
        if(spinnerPaddockValue != -1) {
            feedpaddock.setSelection(spinnerPaddockValue);
        }else{
            feedpaddock.setSelection(0);
        }
        spinpaddockvalue = spinnerPaddockValue;

    }


    private void setListeners() {

        feednosupplement.setOnClickListener(this);
        feedchangesupplement.setOnClickListener(this);
        feedcurrentsupplement.setOnClickListener(this);
        adjuststock.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        stockChange = stockUpdate;

        if (view == feednosupplement){


            stockChange.setAreaUsing(updateArea);
            stockChange.setSupplement(0);
            stockAO.updateStock(stockChange);
            MainActivity activity = (MainActivity)getActivity();
            activity.ReplaceFragment(Enums.FragmentEnums.FeedScenarioFragment, sectionNumber);

        }else if (view == feedchangesupplement) {
            stockChange.setSupKg(updateSupKg);
            stockAO.updateStock(stockChange);
            MainActivity activity = (MainActivity) getActivity();
            activity.ReplaceFragment(Enums.FragmentEnums.FeedScenarioFragment, sectionNumber);

        }else if (view == feedcurrentsupplement){
            stockChange.setAreaUsing(updateGrassArea);
            stockAO.updateStock(stockChange);
            MainActivity activity = (MainActivity) getActivity();
            activity.ReplaceFragment(Enums.FragmentEnums.FeedScenarioFragment, sectionNumber);

        }else if (view == adjuststock){


            Bundle arguments = new Bundle();
            arguments.putParcelable("selectedStock", stockUpdate);
            arguments.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
            CustomStockDialogFragment customStockDialogFragment = new CustomStockDialogFragment();
            customStockDialogFragment.setArguments(arguments);
            customStockDialogFragment.show(getFragmentManager(),
                    CustomStockDialogFragment.ARG_ITEM_ID);
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
                feedpaddock.setAdapter(adapter);
                if (spinpaddockvalue != -1) {
                    feedpaddock.setSelection(spinpaddockvalue);
                }




                feednosupplement.setEnabled(true);
                feedchangesupplement.setEnabled(true);
            }
        }
    }

    public class GetStockTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Activity> activityWeakRef;
        private List<Stock> stock;


        public GetStockTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            stock = stockAO.getStock();


            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {


                ArrayAdapter<Stock> adapter = new ArrayAdapter<Stock>(
                        activityWeakRef.get(),
                        android.R.layout.simple_spinner_item, stock);
                feedstock.setAdapter(adapter);
                if (spinstockvalue != -1) {
                    feedstock.setSelection(spinstockvalue);
                }

                feednosupplement.setEnabled(true);
                feedchangesupplement.setEnabled(true);
            }
        }
    }

}
