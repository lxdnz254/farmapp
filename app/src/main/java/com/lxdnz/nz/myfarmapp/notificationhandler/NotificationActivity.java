package com.lxdnz.nz.myfarmapp.notificationhandler;

//import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by alex on 14/03/16.
 */
public class NotificationActivity extends FragmentActivity implements NotificationActivityClose {


    String TAG = "Update Paddock Cover";

    public NotificationActivity(){


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        DialogFragment notificationDialog = new NotificationDialog().newInstance();
        notificationDialog.show(getSupportFragmentManager(), TAG);

    }

    @Override
    public void close() {
        finish();
    }
}
