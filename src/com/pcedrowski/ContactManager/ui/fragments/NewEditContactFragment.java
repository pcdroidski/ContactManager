package com.pcedrowski.ContactManager.ui.fragments;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.Patterns;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.models.Contact;
import com.pcedrowski.ContactManager.provider.ContactsManager;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewEditContactFragment extends Fragment {
    private Context mContext;
    private ContactsManager contactsManager;

    private Contact contact;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private EditText addressField;
    private ImageView photoField;
    private static String photoPath;

    private double addressLatitude;
    private double addressLongitude;

    private static final int IMAGE_PICKER_SELECT = 999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        contactsManager = ContactsManager.getInstance();

        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = getArguments();
        if (args != null) {
            long contactId = args.getLong("contact_id");
            contact = new Contact(contactsManager.getContact(contactId));
            getActivity().getActionBar().setTitle("Edit Contact");
        } else {
            getActivity().getActionBar().setTitle("New Contact");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_contact_fragment, container, false);

        firstNameField = (EditText) rootView.findViewById(R.id.new_first_name);
        lastNameField = (EditText) rootView.findViewById(R.id.new_last_name);
        emailField = (EditText) rootView.findViewById(R.id.new_email);
        addressField = (EditText) rootView.findViewById(R.id.new_address);
        photoField = (ImageView) rootView.findViewById(R.id.new_contact_photo);
        photoField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);
            }
        });
        photoField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new RemovePhotoDialog().show(getFragmentManager(), "remove_photo");
                return true;
            }
        });

        if (contact != null){
            firstNameField.setText(contact.getFirstName());
            lastNameField.setText(contact.getLastName());
            emailField.setText(contact.getEmail());
            addressField.setText(contact.getAddress());

            if (contact.getPhotoUrl() != null)
                decodeUri(Uri.parse(contact.getPhotoUrl()));
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.new_contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.save_contact:
                saveContact();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveContact(){
        String address = addressField.getText().toString();

        if (!address.isEmpty()) {
            GetAddressTask getAddressTask = new GetAddressTask(mContext);
            getAddressTask.execute(address);
        } else {
            storeContent(null);
        }
    }

    private void storeContent(String address){
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();

        if (firstName.isEmpty()){
            Toast.makeText(mContext, "First name is empty!", Toast.LENGTH_SHORT).show();
        } else if (lastName.isEmpty()){
            Toast.makeText(mContext, "Last name is empty!", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()){
            Toast.makeText(mContext, "Email address is empty!", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(mContext, "Email address is invalid!", Toast.LENGTH_SHORT).show();
        } else if (address != null && !address.isEmpty() && addressLatitude == 0 && addressLongitude == 0) {
            Toast.makeText(mContext, "Unable to find a location for the address specified.", Toast.LENGTH_SHORT).show();
        } else {
            if (contact == null) {
                contactsManager.addContact(firstName, lastName, email, address, photoPath, addressLatitude, addressLongitude);
                Toast.makeText(mContext, "Contact created successfully!", Toast.LENGTH_SHORT).show();
            } else {
                contactsManager.updateContact(contact.getContactId(), firstName, lastName, email, address, photoPath, addressLatitude, addressLongitude, 1);
                Toast.makeText(mContext, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("contact_id", contact.getContactId());
                getActivity().setResult(Activity.RESULT_OK, data);
            }
            getActivity().finish();
        }
    }

    /** * Photo Selection result */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            decodeUri(data.getData());
        }
    }

    public void decodeUri(Uri uri) {
        photoPath = uri.toString();
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

    /** Google maps api address fetcher */
    private class GetAddressTask extends AsyncTask<String, Void, Address> {
        Context mContext;
        String orgAddress;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @params params One or more Location objects
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         */
        @Override
        protected Address doInBackground(String... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the first address
            orgAddress = params[0];
            // Create a list to contain the results
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(orgAddress, 5);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return null;
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Something went wrong.";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return null;
            }

            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address fetchedAddress = addresses.get(0);

                // Return the address
                return fetchedAddress;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address == null) {
                // Failed to find location
                addressLatitude = 0;
                addressLongitude = 0;
            } else {
                // Display the results of the lookup.
                addressLatitude = address.getLatitude();
                addressLongitude = address.getLongitude();
            }
            storeContent(orgAddress);
        }
    }

    public class RemovePhotoDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.remove_photo_confirm)
                    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            photoPath = null;
                            photoField.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.empty_profile_picture));
                            Toast.makeText(mContext, "Photo removed!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
            return builder.create();
        }
    }

}
