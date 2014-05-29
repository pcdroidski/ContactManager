package com.pcedrowski.ContactManager.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.models.Contact;
import com.pcedrowski.ContactManager.provider.ContactsManager;
import com.pcedrowski.ContactManager.ui.activities.ContactActivity;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ContactDetailFragment extends Fragment {
    private Context mContext;
    private Contact contact;
    private ContactsManager contactsManager;

    private TextView fullNameField;
    private TextView emailField;
    private TextView addressField;
    private ImageView photoField;

    private MapView mapView;
    private GoogleMap googleMap;

    public static int EDIT_CONTACT = 2345;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        contactsManager = ContactsManager.getInstance();
        setRetainInstance(true);
        setHasOptionsMenu(true);

        /** Get contact info */
        Bundle args = getArguments();
        long contactId = args.getLong("contact_id");
        contact = new Contact(contactsManager.getContact(contactId));

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(contact.getFullName());

        MapsInitializer.initialize(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_details_fragment, container, false);

        fullNameField = (TextView) rootView.findViewById(R.id.contact_full_name);
        emailField = (TextView) rootView.findViewById(R.id.contact_email);
        addressField = (TextView) rootView.findViewById(R.id.contact_address);
        photoField = (ImageView) rootView.findViewById(R.id.contact_photo);

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();

        fillViews();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("ContactDetails", "Menu pressed: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i("ContactDetails", "Home");
                getActivity().onBackPressed();
                return true;

            case R.id.edit_contact:
                Log.i("ContactDetails", "edit");
                Intent edit = new Intent(getActivity(), ContactActivity.class);
                edit.putExtra("contact_id", contact.getContactId());
                startActivityForResult(edit, EDIT_CONTACT);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CONTACT && resultCode == Activity.RESULT_OK) {
            long cId = data.getLongExtra("contact_id", -1);
            contact = new Contact(contactsManager.getContact(cId));
            fillViews();
        }
    }

    private void fillViews(){
        fullNameField.setText(contact.getFullName());
        emailField.setText(contact.getEmail());
        addressField.setText(contact.getAddress());

        if (contact.getPhotoUrl() != null && !contact.getPhotoUrl().isEmpty())
            decodeUri(Uri.parse(contact.getPhotoUrl()));

        if (googleMap != null && contact.getAddress() != null && !contact.getAddress().isEmpty()){
            mapView.setVisibility(View.VISIBLE);
            LatLng ADDRESS = new LatLng(contact.getAddressLatitude(), contact.getAddressLongitude());
            googleMap.addMarker(new MarkerOptions()
                .position(ADDRESS)
                .draggable(false));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(ADDRESS).zoom(14.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.moveCamera(cameraUpdate);
        } else {
            mapView.setVisibility(View.GONE);
        }

    }

    public void decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
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
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            photoField.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            // handle errors
        } catch (Exception e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
    }



}
