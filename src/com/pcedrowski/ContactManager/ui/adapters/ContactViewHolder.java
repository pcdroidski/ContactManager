package com.pcedrowski.ContactManager.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.pcedrowski.ContactManager.R;

public class ContactViewHolder {
    TextView fullNameHolder;
    TextView emailHolder;
    ImageView photoHolder;

    public ContactViewHolder(View v){
        fullNameHolder = (TextView) v.findViewById(R.id.contact_name);
        emailHolder = (TextView) v.findViewById(R.id.contact_email);
        photoHolder = (ImageView) v.findViewById(R.id.contact_photo);
    }

}
