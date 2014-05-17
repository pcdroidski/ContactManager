package com.pcedrowski.ContactManager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

/**
 * Created by pcedrowski on 5/11/14.
 */
public class ContactsProvider extends ContentProvider{
    private DbHelper mDbHelper;
    private static final UriMatcher uriMatcher;

    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;

    @Override
    public boolean onCreate() {
        mDbHelper = DbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setDistinct(true);

        switch (uriMatcher.match(uri)){
            case CONTACTS:
                queryBuilder.setTables(ContactsContent.Contact.TABLE_NAME);
                break;
            case CONTACT_ID:
                queryBuilder.setTables(ContactsContent.Contact.TABLE_NAME);
                queryBuilder.appendWhere(ContactsContent.Contact.KEY_ID + "=" + uri.getLastPathSegment());

                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        /** Tell the cursor what uri to watch for data change */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (uriMatcher.match(uri) != CONTACTS){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        /** Ensure all values are populated */
        if (!values.containsKey(ContactsContent.Contact.KEY_FIRST_NAME)) {
            values.put(ContactsContent.Contact.KEY_FIRST_NAME, "Unknown First");
        }
        if (!values.containsKey(ContactsContent.Contact.KEY_LAST_NAME)) {
            values.put(ContactsContent.Contact.KEY_LAST_NAME, "Unknown Last");
        }
        if (!values.containsKey(ContactsContent.Contact.KEY_EMAIL)) {
            values.put(ContactsContent.Contact.KEY_EMAIL, "Unknown Email");
        }

        int position = getMaxColumnData();
        values.put(ContactsContent.Contact.KEY_POSITION, position);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(ContactsContent.Contact.TABLE_NAME, ContactsContent.Contact.KEY_ID, values);
        if (rowId > 0){
            Uri eventUri = ContentUris.withAppendedId(ContactsContent.Contact.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(eventUri, null);
            return eventUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)){
            case CONTACTS:
                count = db.delete(ContactsContent.Contact.TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                String contactId = uri.getLastPathSegment();
                String newSelection = ContactsContent.Contact.KEY_ID+ "=" + contactId;
                count = db.delete(ContactsContent.Contact.TABLE_NAME, newSelection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)){
            case CONTACTS:
                count = db.update(ContactsContent.Contact.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CONTACT_ID:
                String contactId = uri.getLastPathSegment();
                String newSelection = ContactsContent.Contact.KEY_ID+ "=" + contactId;
                count = db.update(ContactsContent.Contact.TABLE_NAME, values, newSelection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContactsContent.Contact.AUTHORITY, null, CONTACTS);
        uriMatcher.addURI(ContactsContent.Contact.AUTHORITY, "#", CONTACT_ID);
    }

    public int getMaxColumnData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final SQLiteStatement stmt = db
                .compileStatement("SELECT MAX(" + ContactsContent.Contact.KEY_POSITION + ") FROM " + ContactsContent.Contact.TABLE_NAME);

        return (int) stmt.simpleQueryForLong();
    }
}
