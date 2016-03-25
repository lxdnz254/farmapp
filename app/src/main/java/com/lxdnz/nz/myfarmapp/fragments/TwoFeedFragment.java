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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;

/**
 * Created by alex on 18/09/15.
 */
public class TwoFeedFragment extends Fragment implements View.OnClickListener {

    int grazingresidual = Constants.getTargetresidual();

    private Spinner spinstock;
    private Spinner paddockmorning;
    private Spinner paddockevening;

    private SeekBar areamorning;
    private SeekBar areaevening;

    private Button acceptarea;
    private Button adjuststock;

    private TextView stockneed;
    private TextView area1;
    private TextView area2;
    private TextView totalarea;
    private TextView totalstockkg;


    int stockQuantity;
    int checksup;
    int coverMorning;
    int coverEvening;
    int availMorning;
    int availEvening;


    double stockGrass;
    double stockSupKg;
    double stockTotalKg;
    double Kground;
    double checkAreaAM;
    double maxAreaMorning;
    double maxAreaEvening;
    double usingAreaMorning;
    double usingAreaEvening;
    double updateArea;

    String nameMorning;
    String nameEvening;


    Paddock paddockAM;
    Paddock paddockPM;
    Stock stockUpdate;
    Stock stockChange;
    PaddockAO paddockAO;
    StockAO stockAO;
    public int sectionNumber = 6;
    int valuestockspinner;
    int valuemorningspinner;
    int valueeveningspinner;

    private GetPaddockTask paddockTask;
    private GetStockTask stockTask;

    public static final String ARG_ITEM_ID = "two_feed_fragment";
    public static String TAG = "two feed";

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


        View rootView = inflater.inflate(R.layout.fragment_two_feed_scenario, container,
                false);

        findViewsById(rootView);
        setListeners();

        paddockTask = new GetPaddockTask(getActivity());
        paddockTask.execute((Void) null);
        stockTask = new GetStockTask(getActivity());
        stockTask.execute((Void) null);

        // stock selection

        spinstock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                // Your code here
                Stock stock = (Stock) adapterView.getItemAtPosition(i);
                setPreferences("twostockChoice", i);
                stockUpdate = stock;
                checksup = stock.getSupplement();
                stockQuantity = stock.getQuantity();
                stockGrass = stock.getGrassKg();
                stockSupKg = stock.getSupKg();
                stockTotalKg = stockQuantity*stockGrass;

                stockneed.setText(getString(R.string.feedstock) + stockGrass + " Kg of grass per animal");
                Totals();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        // paddock selection

        paddockmorning.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Your code here
                Paddock paddock = (Paddock) adapterView.getItemAtPosition(i);
                setPreferences("morningpaddockChoice", i);

                paddockAM = paddock;
                nameMorning = paddock.getPaddockName();
                coverMorning = paddock.getCurrentCover();
                availMorning = (paddock.getCurrentCover() - grazingresidual);
                if (availMorning < 0) {
                    availMorning = 0;
                }
                checkAreaAM = paddock.getArea();
                maxAreaMorning = paddock.getArea();
                if (nameEvening == nameMorning) {
                    Toast.makeText(getActivity(), "Paddocks the same! Evening area available will be adjusted", Toast.LENGTH_SHORT).show();
                    maxAreaEvening = paddock.getArea() - usingAreaMorning;
                }

                areamorning.setMax((int) (maxAreaMorning * 100));
                setAreaText(0);
                Totals();


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        paddockevening.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //Your code here
                Paddock paddock = (Paddock) adapterView.getItemAtPosition(i);
                setPreferences("eveningpaddockChoice", i);
                paddockPM = paddock;
                nameEvening = paddock.getPaddockName();
                coverEvening = paddock.getCurrentCover();
                availEvening = (paddock.getCurrentCover() - grazingresidual);
                if (availEvening < 0) {
                    availEvening = 0;
                }
                if (nameEvening == nameMorning) {
                    Toast.makeText(getActivity(), "Paddocks the same! Evening area available will be adjusted", Toast.LENGTH_SHORT).show();
                    maxAreaEvening = paddock.getArea() - usingAreaMorning;
                } else {
                    maxAreaEvening = paddock.getArea();
                }


                areaevening.setMax((int) (maxAreaEvening * 100));
                setAreaText(1);
                Totals();

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });





        // area selection

        areamorning.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {



                usingAreaMorning = (double) progress / 100;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setAreaText(0);
                if (usingAreaEvening > 0){
                    setAreaText(1);
                }
                if (nameMorning == nameEvening){
                    BigDecimal roundMax = new BigDecimal(checkAreaAM-usingAreaMorning).setScale(2, RoundingMode.HALF_UP);
                    maxAreaEvening = roundMax.doubleValue();

                    areaevening.setMax((int)(maxAreaEvening*100));
                }


                Totals();

            }
        });

        areaevening.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {



                usingAreaEvening = (double) progress / 100;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setAreaText(1);


                Totals();

            }
        });





    return rootView;
    }


    public static TwoFeedFragment newInstance(int sectionNumber) {
        TwoFeedFragment fragment = new TwoFeedFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.title_section6);
        getActionBar().setTitle(R.string.title_section6);
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



    }

    private void setPreferences(String string, int i){

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FarmApp", 0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt(string, i);

        prefEditor.commit();

    }

    private void sharedPrefs() {

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FarmApp", Context.MODE_PRIVATE);
        int twostockspinnerValue = sharedPref.getInt("twostockChoice", -1);
        if (twostockspinnerValue != -1){
            spinstock.setSelection(twostockspinnerValue);
            valuestockspinner = twostockspinnerValue;
        }else{
            spinstock.setSelection(0);
            valuestockspinner = 0;
        }
        int morningspinnerValue = sharedPref.getInt("morningpaddockChoice", -1);
        if (morningspinnerValue != -1){
            paddockmorning.setSelection(morningspinnerValue);
            valuemorningspinner = morningspinnerValue;
        }else{
            paddockmorning.setSelection(0);
            valuemorningspinner = 0;
        }
        int eveningspinnerValue = sharedPref.getInt("eveningpaddockChoice", -1);
        if (eveningspinnerValue != -1){
            paddockevening.setSelection(eveningspinnerValue);
            valueeveningspinner = eveningspinnerValue;
        }else {
            paddockevening.setSelection(0);
            valueeveningspinner = 0;
        }


    }

    public void findViewsById(View rootView){

        spinstock = (Spinner) rootView.findViewById(R.id.spinner);
        paddockmorning = (Spinner) rootView.findViewById(R.id.spinner2);
        paddockevening = (Spinner) rootView.findViewById(R.id.spinner3);

        acceptarea = (Button) rootView.findViewById(R.id.area_button);
        adjuststock = (Button) rootView.findViewById(R.id.adjust_button);

        areamorning = (SeekBar) rootView.findViewById(R.id.seekBar);
        areaevening = (SeekBar) rootView.findViewById(R.id.seekBar2);

        stockneed = (TextView) rootView.findViewById(R.id.textView2);
        area1 = (TextView) rootView.findViewById(R.id.textView5);
        area2 = (TextView) rootView.findViewById(R.id.textView8);
        totalarea = (TextView) rootView.findViewById(R.id.total_area);
        totalstockkg = (TextView) rootView.findViewById(R.id.total_feed);



    }

    private void setListeners() {
        acceptarea.setOnClickListener(this);
        adjuststock.setOnClickListener(this);


    }

    @Override
    public void onClick(View view){
        stockChange = stockUpdate;
        if (view == acceptarea){
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            stockChange.setAreaUsing(updateArea);
            if (hour > 13){
                stockChange.setPaddock(paddockPM);
                Toast.makeText(getActivity(), "Stock in Evening Paddock", Toast.LENGTH_SHORT).show();
            }else {
                stockChange.setPaddock(paddockAM);
                Toast.makeText(getActivity(), "Stock in Morning Paddock", Toast.LENGTH_SHORT).show();
            }
            stockAO.updateStock(stockChange);
            MainActivity activity = (MainActivity)getActivity();
            activity.ReplaceFragment(Enums.FragmentEnums.TwoFeedFragment, sectionNumber);
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

    private void Totals() {
        double totalArea;
        double totalKgs;

        totalArea = usingAreaMorning+usingAreaEvening;
        BigDecimal roundArea = new BigDecimal(totalArea).setScale(2, RoundingMode.HALF_EVEN);
        updateArea = roundArea.doubleValue();
        totalarea.setText("Total Area used:"+roundArea+"ha");
        totalKgs = ((availMorning*usingAreaMorning)/stockQuantity)+((availEvening*usingAreaEvening)/stockQuantity);
        if (Double.isNaN(totalKgs) || Double.isInfinite(totalKgs)) {
            Kground = 0;
        }else{

            BigDecimal roundKg = new BigDecimal(totalKgs).setScale(1, RoundingMode.HALF_EVEN);
            Kground = roundKg.doubleValue();
        }
        totalstockkg.setText("Total kg per animal offered:"+Kground+"kgs");

    }

    private void setAreaText(int i){
        if (i == 0){
            area1.setText("Area using:"+usingAreaMorning+"ha. Current Cover:"+coverMorning);
        }else if(i == 1){
            area2.setText("Area using:"+usingAreaEvening+"ha. Current Cover:"+coverEvening);
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

                paddockmorning.setAdapter(adapter);
                if (valuemorningspinner != -1){
                    paddockmorning.setSelection(valuemorningspinner);
                }
                paddockevening.setAdapter(adapter);
                if (valueeveningspinner != -1){
                    paddockevening.setSelection(valueeveningspinner);
                }



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
                spinstock.setAdapter(adapter);
                if (valuestockspinner != -1){
                    spinstock.setSelection(valuestockspinner);
                }




            }
        }
    }

}
