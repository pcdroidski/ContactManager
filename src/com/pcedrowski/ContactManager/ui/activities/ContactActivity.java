package com.pcedrowski.ContactManager.ui.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.pcedrowski.ContactManager.R;
import com.pcedrowski.ContactManager.ui.fragments.NewEditContactFragment;

public class ContactActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /** Fetch extra from intent */
        Bundle extras = getIntent().getExtras();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        NewEditContactFragment fragment = new NewEditContactFragment();
        fragment.setArguments(extras);
        fragmentTransaction.replace(R.id.container_master, fragment);
        fragmentTransaction.commit();
    }
}
