package com.pcedrowski.ContactManager.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.google.android.gms.location.Geofence;
import com.pcedrowski.ContactManager.ContactManager;
import com.pcedrowski.ContactManager.models.Contact;

import java.util.ArrayList;

/**
 * Created by pcedrowski on 5/27/14.
 */
public class ContactsManager {
    private static final ContactsManager INSTANCE = new ContactsManager();

    // Geofence constants
    public static final long GEOFENCE_EXPIRATION_TIME = 0;
    public static final float GEOFENCE_RADIUS = 100;

    private Context mContext;
    private ContentResolver contentResolver;

    public static ContactsManager getInstance(){
        return INSTANCE;
    }

    public ContactsManager() {
        mContext = ContactManager.getContext();
        contentResolver = mContext.getContentResolver();

    }

    /** Contact management */
    public Cursor getAllContacts(){

        return null;
    }
    public Cursor getContact(long id){
        Cursor c = contentResolver.query(ContactsContent.Contact.CONTENT_URI, new String[] {ContactsContent.Contact.KEY_ID, ContactsContent.Contact.KEY_POSITION,
                        ContactsContent.Contact.KEY_FIRST_NAME, ContactsContent.Contact.KEY_LAST_NAME, ContactsContent.Contact.KEY_EMAIL,
                        ContactsContent.Contact.KEY_PHOTO_URL, ContactsContent.Contact.KEY_ADDRESS,
                        ContactsContent.Contact.KEY_ADDRESS_LON, ContactsContent.Contact.KEY_ADDRESS_LAT,
                        ContactsContent.Contact.KEY_EXPIRATION_DURATION, ContactsContent.Contact.KEY_RADIUS,
                        ContactsContent.Contact.KEY_TRANSITION_TYPE, ContactsContent.Contact.KEY_ENABLED_GEOFENCE,
                        ContactsContent.Contact.KEY_ACTIVE_GEOFENCE},
                ContactsContent.Contact.KEY_ID + "=" + id, null, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public ArrayList<Contact> getContacts(String[] gIds){
        Cursor c = contentResolver.query(ContactsContent.Contact.CONTENT_URI, new String[] {ContactsContent.Contact.KEY_ID, ContactsContent.Contact.KEY_POSITION,
                        ContactsContent.Contact.KEY_FIRST_NAME, ContactsContent.Contact.KEY_LAST_NAME, ContactsContent.Contact.KEY_EMAIL,
                        ContactsContent.Contact.KEY_PHOTO_URL, ContactsContent.Contact.KEY_ADDRESS,
                        ContactsContent.Contact.KEY_ADDRESS_LON, ContactsContent.Contact.KEY_ADDRESS_LAT,
                        ContactsContent.Contact.KEY_EXPIRATION_DURATION, ContactsContent.Contact.KEY_RADIUS,
                        ContactsContent.Contact.KEY_TRANSITION_TYPE, ContactsContent.Contact.KEY_ENABLED_GEOFENCE,
                        ContactsContent.Contact.KEY_ACTIVE_GEOFENCE},
                ContactsContent.Contact.KEY_ID + "= ?", gIds, null);

        if (c != null) {
            c.moveToFirst();
        }
        ArrayList<Contact> contactArrayList = new ArrayList<Contact>();
        do {
            Contact contact = new Contact(c);
            contactArrayList.add(contact);
        } while(c.moveToNext());

        return contactArrayList;
    }
    public void addContact(String firstName, String lastName, String email, String address, String photoPath,
                           double addressLatitude, double addressLongitude){
        /** Contact info */
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContent.Contact.KEY_FIRST_NAME, firstName);
        contentValues.put(ContactsContent.Contact.KEY_LAST_NAME, lastName);
        contentValues.put(ContactsContent.Contact.KEY_EMAIL, email);
        contentValues.put(ContactsContent.Contact.KEY_ADDRESS, address);
        contentValues.put(ContactsContent.Contact.KEY_PHOTO_URL, photoPath);

        /** Geofence values */
        contentValues.put(ContactsContent.Contact.KEY_ADDRESS_LAT, addressLatitude);
        contentValues.put(ContactsContent.Contact.KEY_ADDRESS_LON, addressLongitude);
        contentValues.put(ContactsContent.Contact.KEY_RADIUS, GEOFENCE_RADIUS);
        contentValues.put(ContactsContent.Contact.KEY_EXPIRATION_DURATION, GEOFENCE_EXPIRATION_TIME);
        contentValues.put(ContactsContent.Contact.KEY_TRANSITION_TYPE, Geofence.GEOFENCE_TRANSITION_ENTER);
        contentValues.put(ContactsContent.Contact.KEY_ENABLED_GEOFENCE, 1);
        contentValues.put(ContactsContent.Contact.KEY_ACTIVE_GEOFENCE, 0);

        contentResolver.insert(ContactsContent.Contact.CONTENT_URI, contentValues);
    }
    public void updateContact(long id, String firstName, String lastName, String email, String address, String photoPath,
                              double addressLatitude, double addressLongitude, int geofenceEnabled){
        /** Contact info */
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContent.Contact.KEY_FIRST_NAME, firstName);
        contentValues.put(ContactsContent.Contact.KEY_LAST_NAME, lastName);
        contentValues.put(ContactsContent.Contact.KEY_EMAIL, email);
        contentValues.put(ContactsContent.Contact.KEY_ADDRESS, address);
        contentValues.put(ContactsContent.Contact.KEY_PHOTO_URL, photoPath);

        /** Geofence values */
        contentValues.put(ContactsContent.Contact.KEY_ADDRESS_LAT, addressLatitude);
        contentValues.put(ContactsContent.Contact.KEY_ADDRESS_LON, addressLongitude);
        contentValues.put(ContactsContent.Contact.KEY_RADIUS, GEOFENCE_RADIUS);
        contentValues.put(ContactsContent.Contact.KEY_EXPIRATION_DURATION, GEOFENCE_EXPIRATION_TIME);
        contentValues.put(ContactsContent.Contact.KEY_TRANSITION_TYPE, Geofence.GEOFENCE_TRANSITION_ENTER);
        contentValues.put(ContactsContent.Contact.KEY_ENABLED_GEOFENCE, geofenceEnabled);

        contentResolver.update(ContactsContent.Contact.CONTENT_URI, contentValues, ContactsContent.Contact.KEY_ID + "=" + id, null);
    }
    public void updateContacts(){

    }

}
