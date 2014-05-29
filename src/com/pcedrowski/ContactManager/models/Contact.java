package com.pcedrowski.ContactManager.models;

import android.database.Cursor;
import com.pcedrowski.ContactManager.provider.ContactsContent;

/**
 * Created by pcedrowski on 5/11/14.
 */
public class Contact {
    private long contactId;
    private String firstName;
    private String lastName;
    private String email;
    private String photoUrl;

    private String address;
    private double address_latitude;
    private double address_longitude;
    private boolean geofenceEnabled;


    public Contact(){

    }

    public Contact(Cursor cursor){
        contactId = cursor.getInt(cursor.getColumnIndex(ContactsContent.Contact.KEY_ID));
        firstName= cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_FIRST_NAME));
        lastName= cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_LAST_NAME));
        email = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_EMAIL));
        address = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_ADDRESS));
        photoUrl = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_PHOTO_URL));
        address_latitude = cursor.getDouble(cursor.getColumnIndex(ContactsContent.Contact.KEY_ADDRESS_LAT));
        address_longitude = cursor.getDouble(cursor.getColumnIndex(ContactsContent.Contact.KEY_ADDRESS_LON));
        geofenceEnabled = cursor.getInt(cursor.getColumnIndex(ContactsContent.Contact.KEY_ENABLED_GEOFENCE)) == 1;
    }

    /** Getters and Setters */
    public long getContactId() {
        return contactId;
    }
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getAddressLatitude() {
        return address_latitude;
    }

    public double getAddressLongitude() {
        return address_longitude;
    }

    /** Custom methods */
    public String getFullName(){
        return String.format("%s %s", firstName, lastName);
    }

    public boolean isGeofenceEnabled(){
        return geofenceEnabled;
    }

}
