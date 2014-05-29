package com.pcedrowski.ContactManager.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.provider.GeofenceManager;
import com.pcedrowski.ContactManager.services.ReceiveTransitionsService;
import com.pcedrowski.ContactManager.ui.fragments.ContactDetailFragment;
import com.pcedrowski.ContactManager.ui.fragments.ContactListFragment;

import java.util.ArrayList;
import java.util.List;


/**
 *
 Build an app that allows me to manage a list of (random) people. People
 consist of first name, last name and email address. The app should be able
 to:

 - Add new people and verify that I'm entering a valid email address
 - Remove people
 - Allow me to manually sort the list
 - Give me a button to automatically sort the list alphabetically
 - Save the list so that when I load the app next I am looking at the same
 list

 - In addition the list screen should show me first name and last name. When I
 select a person I should see a contact detail page.

 Bonus 1: Allow me to select a map location for each person and provide a
 map view for all of the people in my list.


 Bonus 2: Allow me to choose a photo from my on-device photos for a contact.
 The photo should appear in the list view as well.

 *
 */

public class MainActivity extends Activity implements ContactListFragment.ListContactListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener{

    private LocationClient mLocationClient;
    private List<Geofence> mEnabledGeofences;
    public enum REQUEST_TYPE {ADD, REMOVE}
    private REQUEST_TYPE mRequestType;
    private boolean mInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /** Play services APIs */
        mLocationClient = new LocationClient(this, this, this);
        mInProgress = false;

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            ContactListFragment fragment = new ContactListFragment();
            fragmentTransaction.replace(R.id.container_master, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addGeofences();
    }

    @Override
    public void onDestroy() {
        mLocationClient.disconnect();
        mInProgress = false;
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }


    @Override
    public void addNewContact() {
        Intent i = new Intent(this, ContactActivity.class);
        startActivity(i);
    }

    @Override
    public void showContact(long contactId){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        ContactDetailFragment contactFragment = new ContactDetailFragment();

        Bundle args = new Bundle();
        args.putLong("contact_id", contactId);
        contactFragment.setArguments(args);

        ft.replace(R.id.container_master, contactFragment, "contact_detail");
        ft.addToBackStack("contact_detail");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment contactDetail = getFragmentManager().findFragmentByTag("contact_detail");
        if (contactDetail != null && contactDetail.isAdded()){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.remove(contactDetail);
            fragmentTransaction.commit();
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /** Google Play APIs */
    private void addGeofences(){
        mRequestType = REQUEST_TYPE.ADD;

        // Fetch active Geofences from manager
        mEnabledGeofences = GeofenceManager.getInstance().getEnabledGeofences();
        if (mEnabledGeofences == null) {
            mEnabledGeofences = new ArrayList<Geofence>();
        }

        if (!mInProgress && mEnabledGeofences.size() > 0) {
            mInProgress = true;
            mLocationClient.connect();
        } else {
            //TODO
            Log.i("Geofencing", "Unable to connect to location client. Enabled geo: "
                    + mEnabledGeofences.size());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        PendingIntent pendingIntent;
        switch (mRequestType) {
            case ADD:
                pendingIntent = getTransitionPendingIntent();
                mLocationClient.addGeofences(mEnabledGeofences, pendingIntent, this);
                break;

            case REMOVE:
                //TODO
                break;
        }
    }

    @Override
    public void onDisconnected() {
        mLocationClient.disconnect();
        mInProgress = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO

    }

    @Override
    public void onAddGeofencesResult(int statusCode, String[] strings) {
        if (LocationStatusCodes.SUCCESS == statusCode) {
            Log.i("Geofencing", "Successfully added geofence result.");

        } else {
            Log.i("Geofencing", "Failed to add geofence result.");
            // If adding the geofences failed
        }

        mInProgress = false;
        mLocationClient.disconnect();
    }

    private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(this,
                ReceiveTransitionsService.class);

        return PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
