package com.pcedrowski.ContactManager.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Toast;
import com.mobeta.android.dslv.DragSortListView;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.provider.ContactsContent;
import com.pcedrowski.ContactManager.ui.adapters.ContactsAdapter;
import com.pcedrowski.ContactManager.ui.adapters.SeparatedListAdapter;

public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;

    private SeparatedListAdapter separatedListAdapter;
    private ContactsAdapter contactsAdapter;
    private ContactsAdapter nearbyContactsAdapter;
    private DragSortListView dragSortListView;

    private ListContactListener listContactListener;

    // These are the Contacts rows that we will retrieve
    final String[] PROJECTION = new String[] {
            ContactsContent.Contact.KEY_ID,
            ContactsContent.Contact.KEY_POSITION,
            ContactsContent.Contact.KEY_FIRST_NAME,
            ContactsContent.Contact.KEY_LAST_NAME,
            ContactsContent.Contact.KEY_EMAIL,
            ContactsContent.Contact.KEY_ADDRESS,
            ContactsContent.Contact.KEY_PHOTO_URL,
            ContactsContent.Contact.KEY_ACTIVE_GEOFENCE
    };

    final String SORT_ORDER_POSITIONS = ContactsContent.Contact.KEY_POSITION
            + " ASC";
    final String SORT_ORDER_A_Z = ContactsContent.Contact.KEY_LAST_NAME
            + " COLLATE NOCASE ASC";

    final String GEO_ACTIVE = ContactsContent.Contact.KEY_ACTIVE_GEOFENCE
            + "=1";
    final String GEO_NOT_ACTIVE = ContactsContent.Contact.KEY_ACTIVE_GEOFENCE
            + "=0";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        setRetainInstance(true);
        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(4, null, this);

        separatedListAdapter = new SeparatedListAdapter(mContext);
        contactsAdapter = new ContactsAdapter(mContext, null);
        nearbyContactsAdapter = new ContactsAdapter(mContext, null);

        separatedListAdapter.addSection("Nearby Contacts", nearbyContactsAdapter);
        separatedListAdapter.addSection("All Contacts", contactsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(4, null, this);


        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getActionBar().setTitle("Contact List");
    }

    @Override
    public void onPause(){
        super.onPause();

        if (contactsAdapter != null)
            contactsAdapter.persistChanges();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_list_fragment, container, false);

        dragSortListView = (DragSortListView) rootView.findViewById(R.id.list_view);
        dragSortListView.setAdapter(separatedListAdapter);
        View emptyTextView = rootView.findViewById(R.id.empty_text);
        dragSortListView.setEmptyView(emptyTextView);
        dragSortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listContactListener.showContact(id);
            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listContactListener = (ListContactListener) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_contact:
                listContactListener.addNewContact();
                return true;

            case R.id.sort_contacts:
                Toast.makeText(mContext, "Sorting alphabetically", Toast.LENGTH_SHORT).show();
                sortContactsAZ();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /** Loader methods */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case 0:
                return new CursorLoader(getActivity(), ContactsContent.Contact.CONTENT_URI, PROJECTION,
                        GEO_NOT_ACTIVE, null, SORT_ORDER_POSITIONS);

            case 1:
                return new CursorLoader(getActivity(), ContactsContent.Contact.CONTENT_URI, PROJECTION,
                        GEO_NOT_ACTIVE, null, SORT_ORDER_A_Z);

            case 4:
                return new CursorLoader(getActivity(), ContactsContent.Contact.CONTENT_URI, PROJECTION,
                        GEO_ACTIVE, null, SORT_ORDER_A_Z);

            default:
                return new CursorLoader(getActivity(), ContactsContent.Contact.CONTENT_URI, PROJECTION,
                        null, null, SORT_ORDER_POSITIONS);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.isClosed()) {
            switch (loader.getId()){
                case 0:
                case 1:
                    contactsAdapter.changeCursor(data);
                    break;
                case 4:
                    nearbyContactsAdapter.changeCursor(data);
            }

        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case 0:
            case 1:
                contactsAdapter.changeCursor(null);
                break;
            case 4:
                nearbyContactsAdapter.changeCursor(null);
        }
    }

    public interface ListContactListener {
        public void addNewContact();
        public void showContact(long contactId);
    }


    private void sortContactsAZ(){
        getLoaderManager().restartLoader(1, null, this);
    }

}
