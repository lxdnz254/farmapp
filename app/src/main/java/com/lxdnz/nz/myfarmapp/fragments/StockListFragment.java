package com.lxdnz.nz.myfarmapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.adapters.StockListAdapter;
import com.lxdnz.nz.myfarmapp.databases.Stock;
import com.lxdnz.nz.myfarmapp.databases.StockAO;
import com.lxdnz.nz.myfarmapp.dialogs.CustomStockDialogFragment;
import com.lxdnz.nz.myfarmapp.helpers.Constants;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by alex on 28/07/15.
 */
public class StockListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, Serializable {

    public static final String ARG_ITEM_ID = "stock_list";
    public String TAG = "Stock";

    Activity activity;
    ListView stockListView;
    ArrayList<Stock> stocks;

    StockListAdapter stockListAdapter;
    StockAO stockAO;
    public int sectionNumber = 3;

    private GetStockTask task;

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public static StockListFragment newInstance(int sectionNumber) {
        StockListFragment fragment = new StockListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        stockAO = new StockAO(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_list, container,
                false);
        findViewsById(view);

        task = new GetStockTask(activity);
        task.execute((Void) null);

        stockListView.setOnItemClickListener(this);
        stockListView.setOnItemLongClickListener(this);

        return view;
    }

    private void findViewsById(View view) {
        stockListView = (ListView) view.findViewById(R.id.list_stock);
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.stock_activity);
        getActionBar().setTitle(R.string.stock_activity);
        super.onResume();
        if (stockListAdapter !=null){
        stockListAdapter.notifyDataSetChanged();}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }


    @Override
    public void onItemClick(AdapterView<?> list, View arg1, int position,
                            long arg3) {
        Stock stock = (Stock) list.getItemAtPosition(position);

        if (stock != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("selectedStock", stock);
            arguments.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
            CustomStockDialogFragment customStockDialogFragment = new CustomStockDialogFragment();
            customStockDialogFragment.setArguments(arguments);
            customStockDialogFragment.show(getFragmentManager(),
                    CustomStockDialogFragment.ARG_ITEM_ID);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long arg3) {
        Stock stock = (Stock) parent.getItemAtPosition(position);

        // Use AsyncTask to delete from database
        stockAO.deleteStock(stock);
        stockListAdapter.remove(stock);
        return true;
    }

    public class GetStockTask extends AsyncTask<Void, Void, ArrayList<Stock>> {

        private final WeakReference<Activity> activityWeakRef;

        public GetStockTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected ArrayList<Stock> doInBackground(Void... arg0) {
            ArrayList<Stock> stockList = stockAO.getStock();
            return stockList;
        }

        @Override
        protected void onPostExecute(ArrayList<Stock> stockList) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {
                Log.d("stock", stockList.toString());
                stocks = stockList;
                if (stockList != null) {
                    if (stockList.size() != 0) {
                        stockListAdapter = new StockListAdapter(activity,
                                stockList);
                        stockListView.setAdapter(stockListAdapter);
                    } else {
                        Toast.makeText(activity, "No Stock Records",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
        }
    }

}
