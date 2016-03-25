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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.lxdnz.nz.myfarmapp.MainActivity;
import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.adapters.PaddockListAdapter;
import com.lxdnz.nz.myfarmapp.databases.Paddock;
import com.lxdnz.nz.myfarmapp.databases.PaddockAO;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.dialogs.CustomPadDialogFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by alex on 25/07/15.
 */
public class PaddockListFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener{

    public static final String ARG_ITEM_ID = "paddock_list";
    public String TAG = "PaddockList:";

    Activity activity;
    ListView paddockListView;
    ArrayList<Paddock> paddocks;

    PaddockListAdapter paddockListAdapter;
    PaddockAO paddockAO;

    private GetPaddockTask task;
    public int sectionNumber = 1;



    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public static PaddockListFragment newInstance(int sectionNumber) {
        PaddockListFragment fragment = new PaddockListFragment();
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
        paddockAO = new PaddockAO(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paddock_list, container,
                false);
        findViewsById(view);

        task = new GetPaddockTask(activity);
        task.execute((Void) null);

        paddockListView.setOnItemClickListener(this);
        paddockListView.setOnItemLongClickListener(this);

        return view;
    }

    private void findViewsById(View view) {
        paddockListView = (ListView) view.findViewById(R.id.list_paddock);
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.find_paddock);
        getActionBar().setTitle(R.string.find_paddock);
        super.onResume();
        if (paddockListAdapter != null){
        paddockListAdapter.notifyDataSetChanged();}

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
        Paddock paddock = (Paddock) list.getItemAtPosition(position);

        if (paddock != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable("selectedPaddock", paddock);
            arguments.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
            CustomPadDialogFragment customPadDialogFragment = new CustomPadDialogFragment();
            customPadDialogFragment.setArguments(arguments);
            customPadDialogFragment.show(getFragmentManager(),
                    CustomPadDialogFragment.ARG_ITEM_ID);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long arg3) {
        Paddock paddock = (Paddock) parent.getItemAtPosition(position);

        // Use AsyncTask to delete from database
        paddockAO.deletePaddock(paddock);
        paddockListAdapter.remove(paddock);
        return true;
    }

    public class GetPaddockTask extends AsyncTask<Void, Void, ArrayList<Paddock>> {

        private final WeakReference<Activity> activityWeakRef;

        public GetPaddockTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected ArrayList<Paddock> doInBackground(Void... arg0) {
            ArrayList<Paddock> paddockList = paddockAO.getPaddocks();
            return paddockList;
        }

        @Override
        protected void onPostExecute(ArrayList<Paddock> padList) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {
                Log.d("paddocks", padList.toString());
                paddocks = padList;
                if (padList != null) {
                    if (padList.size() != 0) {
                        paddockListAdapter = new PaddockListAdapter(activity,
                                padList);
                        paddockListView.setAdapter(paddockListAdapter);
                    } else {
                        Toast.makeText(activity, "No Paddock Records",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
        }
    }


}

