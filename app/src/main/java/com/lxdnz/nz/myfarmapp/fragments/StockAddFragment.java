package com.lxdnz.nz.myfarmapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.databases.Stock;
import com.lxdnz.nz.myfarmapp.databases.StockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;

import java.lang.ref.WeakReference;
import java.util.List;



/**
 * Created by alex on 28/07/15.
 */
public class StockAddFragment extends Fragment implements OnClickListener {

    // UI references
    private EditText stockNameEtxt;
    private EditText stockQuantityEtxt;
    private EditText stockDailyfeed;
    private CheckBox stockSupplement;
    private EditText stockSupKg;
    private TextView stockGrassKg;
    private Spinner stockPaddockin;
    private EditText stockAreaUsing;

    private Button addButton;
    private Button resetButton;

    private int checksupplement = 0;
    private double grassKg;



    Stock stock = null;
    private StockAO stockAO;
    private PaddockAO paddockAO;
    private AddStockTask addStockTask;
    private GetPaddockTask task;

    public static final String ARG_ITEM_ID = "stock_add_fragment";
    public int sectionNumber = 4;

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        stockAO = new StockAO(getActivity());
        paddockAO = new PaddockAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_add, container,
                false);

        findViewsById(rootView);

        setListeners();

        task = new GetPaddockTask(getActivity());
        task.execute((Void) null);

        return rootView;
    }

    public static StockAddFragment newInstance(int sectionNumber) {
        StockAddFragment fragment = new StockAddFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void setListeners() {

        checkboxClickListener();
        addButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    protected void resetAllFields() {
        stockNameEtxt.setText("");
        stockQuantityEtxt.setText("");
        stockDailyfeed.setText("");
        stockSupplement.setChecked(false);
        stockSupKg.setText("");
        stockGrassKg.setText(R.string.grasskg);
        stockAreaUsing.setText("");
        if (stockPaddockin.getAdapter().getCount() > 0)
            stockPaddockin.setSelection(0);

    }

    private void setStock() {
        stock = new Stock();
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
        if (stockDailyfeed.getText().toString().isEmpty()) {
            stock.setDailyfeed(0);
        }else {
            stock.setDailyfeed(Double.parseDouble(stockDailyfeed.getText().toString()));
        }
        stock.setSupplement(checksupplement);
        if (stockSupKg.getText().toString().isEmpty()) {
            stock.setSupKg(0);
            }else{
            stock.setSupKg(Double.parseDouble(stockSupKg.getText().toString()));
        }
        stock.setGrassKg();
        if (stockAreaUsing.getText().toString().isEmpty()) {
            stock.setAreaUsing(0);
        }else {
            stock.setAreaUsing(Double.parseDouble(stockAreaUsing.getText().toString()));
        }
        Paddock selectedPaddock = (Paddock) stockPaddockin.getSelectedItem();
        stock.setPaddock(selectedPaddock);
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.add_stock);
        getActionBar().setTitle(R.string.add_stock);
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

    private void findViewsById(View rootView) {
        stock = new Stock();
        stockNameEtxt = (EditText) rootView.findViewById(R.id.etxt_stock_name);
        stockQuantityEtxt = (EditText) rootView.findViewById(R.id.etxt_quantity);
        stockDailyfeed = (EditText) rootView.findViewById(R.id.etxt_dailyfeed);
        stockSupplement = (CheckBox) rootView.findViewById(R.id.echeckbox_supp);
        stockSupKg = (EditText) rootView.findViewById(R.id.etxt_suppkg);
        stockGrassKg = (TextView) rootView.findViewById(R.id.etxt_grasskg);
        if(!Double.isNaN(stock.getGrassKg())) {
            stockGrassKg.append(stock.getGrassKg() + "kg daily");
        }
        stockAreaUsing = (EditText) rootView.findViewById(R.id.etxt_stock_areausing);
        stockPaddockin = (Spinner) rootView.findViewById(R.id.spinner_paddock);

        addButton = (Button) rootView.findViewById(R.id.button_add);
        resetButton = (Button) rootView.findViewById(R.id.button_reset);
    }

    @Override
    public void onClick(View view) {


        if (view == addButton) {
            setStock();

            addStockTask = new AddStockTask(getActivity());
            addStockTask.execute((Void) null);
        } else if (view == resetButton) {
            resetAllFields();
        }
    }

    public void checkboxClickListener() {


        stockSupplement.setOnClickListener(new OnClickListener() {

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
                stockPaddockin.setAdapter(adapter);

                addButton.setEnabled(true);
            }
        }
    }

    public class AddStockTask extends AsyncTask<Void, Void, Long> {

        private final WeakReference<Activity> activityWeakRef;

        public AddStockTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected Long doInBackground(Void... arg0) {
            long result = stockAO.addStock(stock);
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {
                if (result != -1)
                    Toast.makeText(activityWeakRef.get(), "Stock Saved",
                            Toast.LENGTH_LONG).show();
            }
        }
    }
}
