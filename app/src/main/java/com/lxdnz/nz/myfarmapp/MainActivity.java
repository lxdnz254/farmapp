package com.lxdnz.nz.myfarmapp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.lxdnz.nz.myfarmapp.dialogs.CoverUpdateDialogFragment.CoverDialogFragmentListener;
import com.lxdnz.nz.myfarmapp.dialogs.CustomPadDialogFragment.PadDialogFragmentListener;
import com.lxdnz.nz.myfarmapp.dialogs.CustomStockDialogFragment.StockDialogFragmentListener;
import com.lxdnz.nz.myfarmapp.dialogs.MapDialog.MapDialogListener;
import com.lxdnz.nz.myfarmapp.dialogs.SettingsDialogFragment.SetDialogFragmentListener;
import com.lxdnz.nz.myfarmapp.dialogs.FarmWalkEmailDialogFragment.FarmWalkDialogFragmentListener;
import com.lxdnz.nz.myfarmapp.fragments.FarmDetailsFragment;
import com.lxdnz.nz.myfarmapp.fragments.FeedScenarioFragment;
import com.lxdnz.nz.myfarmapp.fragments.NavigationDrawerFragment;
import com.lxdnz.nz.myfarmapp.fragments.PaddockAddFragment;
import com.lxdnz.nz.myfarmapp.fragments.PaddockDrawFragment;
import com.lxdnz.nz.myfarmapp.fragments.PaddockListFragment;
import com.lxdnz.nz.myfarmapp.fragments.StockAddFragment;
import com.lxdnz.nz.myfarmapp.fragments.StockListFragment;
import com.lxdnz.nz.myfarmapp.fragments.TwoFeedFragment;
import com.lxdnz.nz.myfarmapp.helpers.Constants;
import com.lxdnz.nz.myfarmapp.helpers.Enums;

import com.android.vending.billing.IInAppBillingService;
import com.lxdnz.nz.myfarmapp.util.IabHelper;
import com.lxdnz.nz.myfarmapp.util.IabResult;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
        , PadDialogFragmentListener, StockDialogFragmentListener, SetDialogFragmentListener,
        CoverDialogFragmentListener, MapDialogListener

        {


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /*
    private Fragment contentFragment;
    private PaddockActivity paddockActivity;
    private StockActivity stockActivity;
    PaddockListFragment paddockListFragment;
    StockListFragment stockListFragment;
    */
    public String TAG = "MainActivity";


    public int sectionNumber;

    /**
     * Implement Billing Service Connection
     */
/*
    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    IabHelper mHelper;

*/


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*
        // Get the subscription service
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        String base64EncodedPublicKey;
        base64EncodedPublicKey = Constants.First64+Constants.Mid64+Constants.End64;

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    mHelper = null;
                }
                // Hooray, IAB is fully set up!
            }
        });
        */



    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment mFragment = null;
        String title = getResources().getString(R.string.app_name);
        switch (position){
            case 0:
                mFragment = FarmDetailsFragment.newInstance(0);
                break;

            case 1:
                mFragment = PaddockListFragment.newInstance(1);

                break;
            case 2:
                mFragment = PaddockAddFragment.newInstance(2);

                break;
            case 3:
                mFragment = StockListFragment.newInstance(3);

                break;
            case 4:
                mFragment = StockAddFragment.newInstance(4);
                break;
            case 5:
                mFragment = FeedScenarioFragment.newInstance(5);
                break;
            case 6:
                mFragment = TwoFeedFragment.newInstance(6);
                break;
            case 7:
                mFragment = PaddockDrawFragment.newInstance(7);


        }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mFragment)
                    .addToBackStack(null)
                    .commit();
       /* }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            MapFragment mapFragment = new MapFragment().newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mapFragment)
                    .addToBackStack(null)
                    .commit();


        } */

    }

    public void onSectionAttached(int number) {
        sectionNumber = number;
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section0);
                break;
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    protected void setFragmentTitle(int resourceId) {
      //  Log.i(TAG, " ResourceID " + resourceId);
        setTitle(resourceId);

        // getActionBar().setTitle(resourceId);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();





        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishDialog(int sectionNumber) {
      //  Log.i(TAG, "onFinishDialog");
        switch (sectionNumber) {
            case 0:
            ReplaceFragment(Enums.FragmentEnums.FarmDetailsFragment,sectionNumber);
                break;
            case 1:
                ReplaceFragment(Enums.FragmentEnums.PaddockListFragment, sectionNumber);
                break;
            case 2:
                ReplaceFragment(Enums.FragmentEnums.PaddockAddFragment, sectionNumber);
                break;
            case 3:
                ReplaceFragment(Enums.FragmentEnums.StockListFragment, sectionNumber);
                break;
            case 4:
                ReplaceFragment(Enums.FragmentEnums.StockAddFragment, sectionNumber);
                break;
            case 5:
                ReplaceFragment(Enums.FragmentEnums.FeedScenarioFragment, sectionNumber);
                break;
            case 6:
                ReplaceFragment(Enums.FragmentEnums.TwoFeedFragment, sectionNumber);
                break;
            case 7:
                ReplaceFragment(Enums.FragmentEnums.PaddockDrawFragment, sectionNumber);
                break;
        }

    }

    public void ReplaceFragment(Enums.FragmentEnums frag, int sectionNumber){

        NavigationDrawerFragment navFrag = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (frag){
            case FarmDetailsFragment:
                FarmDetailsFragment farmFrag = FarmDetailsFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container, farmFrag)
                        .addToBackStack(null).commit();
                break;
            case PaddockAddFragment:
                PaddockAddFragment paddockAddFrag = PaddockAddFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container,paddockAddFrag)
                        .addToBackStack(null).commit();
                break;
            case PaddockListFragment:
                PaddockListFragment paddockListFrag = PaddockListFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container,paddockListFrag)
                        .addToBackStack(null).commit();
                break;
            case StockAddFragment:
                StockAddFragment stockAddFrag = StockAddFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container,stockAddFrag)
                        .addToBackStack(null).commit();
                break;
            case StockListFragment:
                StockListFragment stockListFrag = StockListFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container,stockListFrag)
                        .addToBackStack(null).commit();
                break;
            case FeedScenarioFragment:
                FeedScenarioFragment feedScenarioFrag = FeedScenarioFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container, feedScenarioFrag)
                        .addToBackStack(null).commit();
                break;
            case TwoFeedFragment:
                TwoFeedFragment twoFeedFrag = TwoFeedFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container, twoFeedFrag)
                        .addToBackStack(null).commit();
                break;
            case PaddockDrawFragment:
                PaddockDrawFragment paddockDrawFrag = PaddockDrawFragment.newInstance(sectionNumber);
                navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
                fragmentManager.beginTransaction().replace(R.id.container, paddockDrawFrag)
                        .addToBackStack(null).commit();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavigationDrawerFragment navFrag = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navFrag.mDrawerToggle.setDrawerIndicatorEnabled(true);
        onSectionAttached(navFrag.mCurrentSelectedPosition);


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        if (mService != null) {
            unbindService(mServiceConn);
        }
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
        */
    }

}
