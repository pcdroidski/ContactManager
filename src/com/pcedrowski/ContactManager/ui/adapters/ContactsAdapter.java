package com.pcedrowski.ContactManager.ui.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mobeta.android.dslv.DragSortCursorAdapter;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.provider.ContactsContent;

import java.io.FileDescriptor;
import java.io.IOException;

public class ContactsAdapter extends DragSortCursorAdapter {
    private static final String TAG = ContactsAdapter.class.getCanonicalName();
    private LayoutInflater inflater;
    private Context mContext;
    private LruCache<String, Bitmap> holderImages;

    public ContactsAdapter(Context context, Cursor c) {
        super(context, c);

        mContext = context;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holderImages = new LruCache<String, Bitmap>(2000);
    }


    public void persistChanges() {
        Cursor c = getCursor();
        if (c != null) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                int listPos = getListPosition(c.getPosition());

                if (listPos == REMOVED) {
                    // Delete the record
                    mContext.getContentResolver().delete(ContactsContent.Contact.CONTENT_URI,
                            ContactsContent.Contact.KEY_ID + "=" + c.getInt(c.getColumnIndex("_id")), null);
                } else if (listPos != c.getInt(c.getColumnIndex(ContactsContent.Contact.KEY_POSITION))) {
                    ContentValues contactArgs = new ContentValues();
                    contactArgs.put(ContactsContent.Contact.KEY_POSITION, listPos);

                    // Update the record
                    mContext.getContentResolver().update(ContactsContent.Contact.CONTENT_URI, contactArgs,
                            ContactsContent.Contact.KEY_ID + "=" + c.getInt(c.getColumnIndex("_id")), null);
                }
            }
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View itemView = inflater.inflate(R.layout.contact_list_item, null, false);

        // Instantiate new ContactViewHolder object that references the views
        ContactViewHolder holder = new ContactViewHolder(itemView);
        holder.photoHolder.setImageDrawable(null);
        itemView.setTag(holder);

        return itemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContactViewHolder holder = (ContactViewHolder) view.getTag();

        // Collect data from cursor
        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContent.Contact.KEY_ID));
        String contactFirstName = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_FIRST_NAME));
        String contactLastName = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_LAST_NAME));
        String contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_EMAIL));
        String contactPhotoUrl = cursor.getString(cursor.getColumnIndex(ContactsContent.Contact.KEY_PHOTO_URL));

        // Set data obtained
        holder.fullNameHolder.setText(contactFirstName + " " + contactLastName);
        holder.emailHolder.setText(contactEmail);
        holder.photoHolder.setImageBitmap(fetchImage(contactPhotoUrl));
    }

    private Bitmap decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        Bitmap bitmap = null;
        try {
            parcelFD = mContext.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 2;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e.getLocalizedMessage());
        } finally {
            if (parcelFD != null) {
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }

        return bitmap;
    }

    private void setThumbnail(String uri, Bitmap b) {
        holderImages.put(uri, b);
    }

    private Bitmap getThumbnail(String uri) {
        Bitmap thumbnail = null;
        if (uri != null)
            thumbnail = holderImages.get(uri);
        return thumbnail;
    }

    protected Bitmap fetchImage (String uri) {
        Bitmap thumbnail = getThumbnail(uri); // try to fetch thumbnail
        if (thumbnail != null)
            return thumbnail;

        if (uri == null){
            // Empty profile picture
            thumbnail = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.empty_profile_picture);

        } else {
            // Fetch bitmap from file descriptor
            thumbnail = decodeUri(Uri.parse(uri));
            setThumbnail(uri, thumbnail); // save thumbnail for later reuse
        }

        return thumbnail;
    }
}
