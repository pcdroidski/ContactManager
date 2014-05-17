package com.pcedrowski.ContactManager.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pcedrowski on 5/11/14.
 */
public class DbHelper extends SQLiteOpenHelper {
    public final String TAG = getClass().getSimpleName();
    /** Database file information */
    public static final String DATABASE_NAME = "contact_manager";
    private static final int DATABASE_VERSION = 1;

    private static DbHelper INSTANCE = null;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DbHelper getInstance(final Context mCtx){
        if (INSTANCE == null){
            INSTANCE = new DbHelper(mCtx);
        }
        return INSTANCE;
    }

    private static final String CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + ContactsContent.Contact.TABLE_NAME+ " (" +
                    ContactsContent.Contact.KEY_ID + " INTEGER primary key, " +
                    ContactsContent.Contact.KEY_POSITION + " INTEGER, " +
                    ContactsContent.Contact.KEY_FIRST_NAME + " STRING, " +
                    ContactsContent.Contact.KEY_LAST_NAME + " STRING," +
                    ContactsContent.Contact.KEY_EMAIL + " String," +
                    ContactsContent.Contact.KEY_ADDRESS + " String," +
                    ContactsContent.Contact.KEY_PHOTO_URL + " String," +
                    ContactsContent.Contact.KEY_ADDRESS_LAT + " DOUBLE," +
                    ContactsContent.Contact.KEY_ADDRESS_LON + " DOUBLE" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        while (oldVersion != newVersion){
            // TODO Handle any upgrade commands here
        }

    }


//    /** Custom methods to access Database */
//    public Cursor getAllItemRecords() {
//        SQLiteDatabase db = INSTANCE.getWritableDatabase();
//
//        return db.query(ITEM_TABLE, new String[] { ITEM_KEY_ROWID, ITEM_NAME,
//                        ITEM_DETAILS, ITEM_POSITION }, null, null, null, null,
//                "item_position " + sort_order);
//    }
//
//    public Cursor getItemRecord(long rowId) throws SQLException {
//        SQLiteDatabase db = INSTANCE.getWritableDatabase();
//        Cursor mLetterCursor = db.query(true, ITEM_TABLE, new String[] {
//                        ITEM_KEY_ROWID, ITEM_NAME, ITEM_DETAILS, ITEM_POSITION },
//                ITEM_KEY_ROWID + "=" + rowId, null, null, null, null, null);
//        if (mLetterCursor != null) {
//            mLetterCursor.moveToFirst();
//        }
//        return mLetterCursor;
//    }
//
//    public long insertItemRecord(String item_name, String item_details) {
//        SQLiteDatabase db = INSTANCE.getWritableDatabase();
//
//        int item_Position = getMaxColumnData();
//        ContentValues initialItemValues = new ContentValues();
//        initialItemValues.put(ITEM_NAME, item_name);
//        initialItemValues.put(ITEM_DETAILS, item_details);
//        initialItemValues.put(ITEM_POSITION, (item_Position + 1));
//
//        return db.insert(ITEM_TABLE, null, initialItemValues);
//    }
//
//    public boolean deleteItemRecord(long rowId) {
//        SQLiteDatabase db = INSTANCE.getWritableDatabase();
//        return db.delete(ITEM_TABLE, ITEM_KEY_ROWID + "=" + rowId, null) > 0;
//    }
//
//    public boolean updateItemRecord(long rowId, String item_name,
//                                    String item_details) {
//        SQLiteDatabase db = INSTANCE.getWritableDatabase();
//
//        ContentValues ItemArgs = new ContentValues();
//        ItemArgs.put(ITEM_NAME, item_name);
//        ItemArgs.put(ITEM_DETAILS, item_details);
//        return db.update(ITEM_TABLE, ItemArgs, ITEM_KEY_ROWID + "=" + rowId,
//                null) > 0;
//    }
//
//    public boolean updateItemPosition(long rowId, Integer position) {
//        ContentValues ItemArgs = new ContentValues();
//        ItemArgs.put(ITEM_POSITION, position);
//        return mDb.update(ITEM_TABLE, ItemArgs, ITEM_KEY_ROWID + "=" + rowId,
//                null) > 0;
//    }
}
