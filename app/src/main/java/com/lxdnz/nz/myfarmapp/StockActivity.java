package com.lxdnz.nz.myfarmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


import com.lxdnz.nz.myfarmapp.fragments.StockAddFragment;
import com.lxdnz.nz.myfarmapp.fragments.StockListFragment;

/**
 * Created by alex on 28/07/15.
 */
public class StockActivity extends FragmentActivity {

    private Fragment contentFragment;
    private StockListFragment stockListFragment;
    private StockAddFragment stockAddFragment;
    public String TAG = "StockActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        /*
         * This is called when orientation is changed.
         */
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("content")) {
                String content = savedInstanceState.getString("content");
                if (content.equals(StockAddFragment.ARG_ITEM_ID)) {
                    if (fragmentManager
                            .findFragmentByTag(StockAddFragment.ARG_ITEM_ID) != null) {
                        setFragmentTitle(R.string.add_stock);
                        contentFragment = fragmentManager
                                .findFragmentByTag(StockAddFragment.ARG_ITEM_ID);
                    }
                }
            }
            if (fragmentManager.findFragmentByTag(StockListFragment.ARG_ITEM_ID) != null) {
                stockListFragment = (StockListFragment) fragmentManager
                        .findFragmentByTag(StockListFragment.ARG_ITEM_ID);
                contentFragment = stockListFragment;
            }
        } else {
            stockListFragment = new StockListFragment();
            setFragmentTitle(R.string.stock_activity);
            switchContent(stockListFragment, StockListFragment.ARG_ITEM_ID);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof StockAddFragment) {
            outState.putString("content", StockAddFragment.ARG_ITEM_ID);
        } else {
            outState.putString("content", StockListFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_stock:
                setFragmentTitle(R.string.add_stock);
                stockAddFragment = new StockAddFragment();
                switchContent(stockAddFragment, StockAddFragment.ARG_ITEM_ID);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * We consider StockListFragment as the home fragment and it is not added to
     * the back stack.
     */
    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate())
            ;

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);

            if (!(fragment instanceof StockListFragment)) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }

    protected void setFragmentTitle(int resourseId) {
        Log.i(TAG, " ResourseID " + resourseId);
        setTitle(resourseId);

         // getActionBar().setTitle(resourseId);

    }

    /*
     * We call super.onBackPressed(); when the stack entry count is > 0. if it
     * is instanceof EmpListFragment or if the stack entry count is == 0, then
     * we prompt the user whether to quit the app or not by displaying dialog.
     * In other words, from EmpListFragment on back press it quits the app.

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else if (contentFragment instanceof StockListFragment
                || fm.getBackStackEntryCount() == 0) {
            //Shows an alert dialog on quit
            onShowQuitDialog();
        }
    }

    public void onShowQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage("Do You Want To Quit?");
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

*/
}
