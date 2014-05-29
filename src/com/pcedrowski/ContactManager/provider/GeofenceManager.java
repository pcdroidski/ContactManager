package com.pcedrowski.ContactManager.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.google.android.gms.location.Geofence;
import com.pcedrowski.ContactManager.ContactManager;
import com.pcedrowski.ContactManager.models.SimpleGeofence;

import java.util.ArrayList;

/**
 * Created by pcedrowski on 5/27/14.
 */
public class GeofenceManager {
    private static final GeofenceManager INSTANCE = new GeofenceManager();

    private Context mContext;
    private ContentResolver contentResolver;
//    private static List<String> activeGeofences;

    public static GeofenceManager getInstance(){
        return INSTANCE;
    }

    public GeofenceManager() {
        mContext = ContactManager.getContext();
        contentResolver = mContext.getContentResolver();
//        activeGeofences = new ArrayList<String>();
    }

    /** Geofence management */
    public SimpleGeofence getSimpleGeofence(long id){
        Cursor c = contentResolver.query(ContactsContent.Contact.CONTENT_URI, new String[] {
                        ContactsContent.Contact.KEY_ID, ContactsContent.Contact.KEY_ADDRESS,
                        ContactsContent.Contact.KEY_ADDRESS_LON, ContactsContent.Contact.KEY_ADDRESS_LAT,
                        ContactsContent.Contact.KEY_EXPIRATION_DURATION, ContactsContent.Contact.KEY_RADIUS,
                        ContactsContent.Contact.KEY_TRANSITION_TYPE, ContactsContent.Contact.KEY_ENABLED_GEOFENCE,
                        ContactsContent.Contact.KEY_ACTIVE_GEOFENCE},
                ContactsContent.Contact.KEY_ID + "=" + id, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return new SimpleGeofence(c);
    }
    public ArrayList<Geofence> getEnabledGeofences(){
        ArrayList<Geofence> geofences = new ArrayList<Geofence>();

//        query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
        Cursor c = contentResolver.query(ContactsContent.Contact.CONTENT_URI, new String[] {
                        ContactsContent.Contact.KEY_ID, ContactsContent.Contact.KEY_ADDRESS,
                        ContactsContent.Contact.KEY_ADDRESS_LON, ContactsContent.Contact.KEY_ADDRESS_LAT,
                        ContactsContent.Contact.KEY_EXPIRATION_DURATION, ContactsContent.Contact.KEY_RADIUS,
                        ContactsContent.Contact.KEY_TRANSITION_TYPE, ContactsContent.Contact.KEY_ENABLED_GEOFENCE,
                        ContactsContent.Contact.KEY_ACTIVE_GEOFENCE},
                ContactsContent.Contact.KEY_ENABLED_GEOFENCE + "=1", null, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    SimpleGeofence geofence = new SimpleGeofence(c);
                    geofences.add(geofence.toGeofence());

                } while (c.moveToNext());
            }
        }

        return geofences;
    }

    public void activateGeofence(long id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContent.Contact.KEY_ACTIVE_GEOFENCE, 1);

        contentResolver.update(ContactsContent.Contact.CONTENT_URI, contentValues,
                ContactsContent.Contact.KEY_ID + "=" + id, null
        );
    }

    public void deactivateGeofence(long id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContent.Contact.KEY_ACTIVE_GEOFENCE, 0);

        contentResolver.update(ContactsContent.Contact.CONTENT_URI, contentValues,
                ContactsContent.Contact.KEY_ID + "=" + id, null
        );
    }

}
