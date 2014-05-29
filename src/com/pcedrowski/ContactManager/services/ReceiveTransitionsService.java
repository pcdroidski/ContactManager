package com.pcedrowski.ContactManager.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.models.Contact;
import com.pcedrowski.ContactManager.provider.ContactsManager;
import com.pcedrowski.ContactManager.provider.GeofenceManager;
import com.pcedrowski.ContactManager.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationClient.getTriggeringGeofences;

/**
 * Created by pcedrowski on 5/27/14.
 */
public class ReceiveTransitionsService extends IntentService {

    public ReceiveTransitionsService() {
        super("ReceiveTransitionsService");
    }
    /**
     * Handles incoming intents
     * @param intent The Intent sent by Location Services. This
     * Intent is provided
     * to Location Services (inside a PendingIntent) when you call
     * addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // First check for errors
        if (LocationClient.hasError(intent)) {
            // Get the error code with a static method
            int errorCode = LocationClient.getErrorCode(intent);
            // Log the error
            Log.e("ReceiveTransitionsIntentService",
                    "Location Services error: " +
                            Integer.toString(errorCode)
            );
            /*
             * You can also send the error code to an Activity or
             * Fragment with a broadcast Intent
             */
            //TODO

        /*
         * If there's no error, get the transition type and the IDs
         * of the geofence or geofences that triggered the transition
         */
        } else {
            // Get the type of transition (entry or exit)
            int transitionType =
                    LocationClient.getGeofenceTransition(intent);
            // Test that a valid transition was reported
            if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) || (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)) {
                List<Geofence> triggerList = getTriggeringGeofences(intent);

                String[] triggerIds = new String[triggerList.size()];

                for (int i = 0; i < triggerList.size(); i++) {
                    // Update the database with the geofence ids
                    String gId = triggerList.get(i).getRequestId();
                    triggerIds[i] = gId;
                    long geoId = Long.parseLong(gId);

                    if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                        Log.i("ReceiveTransitionsIntentService",
                                "Geofence " + geoId + " entered!");
                        GeofenceManager.getInstance().activateGeofence(geoId);
                    } else {
                        Log.i("ReceiveTransitionsIntentService",
                                "Geofence " + geoId + "  exited!");
                        GeofenceManager.getInstance().deactivateGeofence(geoId);
                    }
                }

                // Need to fetch and parse out all contacts within triggerIds
                ArrayList<Contact> contactArrayList = ContactsManager.getInstance().getContacts(triggerIds);
                for (Contact contact : contactArrayList){
                    makeNotification(contact);
                }

                // Send broadcast to refresh adapters
                Intent broadcastIntent = new Intent("com.contact_manager.refresh");
                sendBroadcast(broadcastIntent);

            }
            // An invalid transition was reported
            else {
                Log.e("ReceiveTransitionsIntentService",
                        "Geofence transition error: " +
                            Integer.toString(transitionType));
            }
        }
    }

    private void makeNotification(Contact contact){
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.contacts_icon)
                        .setContentTitle("Contact Nearby")
                        .setContentText(contact.getFullName() + " is close to you!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 2;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
