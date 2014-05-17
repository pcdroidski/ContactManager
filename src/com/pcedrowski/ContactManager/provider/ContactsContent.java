package com.pcedrowski.ContactManager.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by pcedrowski on 5/11/14.
 */
public class ContactsContent {
    public static final String AUTHORITY_PREFIX = "com.pcedrowski.contact_manager.provider";

    public static final class Contact implements BaseColumns {
        public static final String TABLE_NAME = "contacts";

        /** Content URI for Categories data */
        public static final String AUTHORITY = AUTHORITY_PREFIX + ".contacts";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // PRIMARY KEY:
        public static final String KEY_ID = "_id";

        public static final String KEY_POSITION = "position";
        public static final String KEY_FIRST_NAME = "first_name";
        public static final String KEY_LAST_NAME = "last_name";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_PHOTO_URL = "photo_url";
        public static final String KEY_ADDRESS_LAT = "address_latitude";
        public static final String KEY_ADDRESS_LON = "address_longitude";

    }
}
