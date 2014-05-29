package com.pcedrowski.ContactManager.models;

import android.database.Cursor;
import com.google.android.gms.location.Geofence;
import com.pcedrowski.ContactManager.provider.ContactsContent;

/**
 * Created by pcedrowski on 5/27/14.
 */
public class SimpleGeofence {
    private final int mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;
    private boolean enabled;
    private boolean active;

    /**
     * @param contactId The Geofence's contact ID
     * @param latitude Latitude of the Geofence's center.
     * @param longitude Longitude of the Geofence's center.
     * @param radius Radius of the geofence circle.
     * @param expiration Geofence expiration duration
     * @param transition Type of Geofence transition.
     */
    public SimpleGeofence(int contactId, double latitude,
            double longitude, float radius, long expiration,
            int transition) {

        this.mId = contactId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }

    public SimpleGeofence(Cursor cursor){
        this.mId = cursor.getInt(cursor.getColumnIndex(ContactsContent.Contact.KEY_ID));
        this.mLatitude = cursor.getDouble(cursor.getColumnIndex(ContactsContent.Contact.KEY_ADDRESS_LAT));
        this.mLongitude = cursor.getDouble(cursor.getColumnIndex(ContactsContent.Contact.KEY_ADDRESS_LON));
        this.mRadius = cursor.getFloat(cursor.getColumnIndex(ContactsContent.Contact.KEY_RADIUS));;
        this.mExpirationDuration = cursor.getLong(cursor.getColumnIndex(ContactsContent.Contact.KEY_EXPIRATION_DURATION));;
        this.mTransitionType = cursor.getInt(cursor.getColumnIndex(ContactsContent.Contact.KEY_TRANSITION_TYPE));
        this.enabled = cursor.getInt(cursor.getColumnIndex(ContactsContent.Contact.KEY_ENABLED_GEOFENCE)) == 1;
        this.active = cursor.getInt(cursor.getColumnIndex(ContactsContent.Contact.KEY_ACTIVE_GEOFENCE)) == 1;
    }

    public int getId() {
        return mId;
    }
    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public float getRadius() {
        return mRadius;
    }
    public long getExpirationDuration() {
        return mExpirationDuration;
    }
    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a
     * SimpleGeofence.
     *
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                .setRequestId(getId()+"")
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(
                        getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(mExpirationDuration)
                .build();
    }


    public boolean isEnabled(){
        return enabled;
    }
    public boolean isActive(){
        return active;
    }
}
