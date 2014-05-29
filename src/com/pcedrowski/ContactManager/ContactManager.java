package com.pcedrowski.ContactManager;

import android.app.Application;
import android.content.Context;

/**
 * Created by pcedrowski on 5/27/14.
 */
public class ContactManager extends Application {
    private static ContactManager INSTANCE;
    private static Context mContext;

    public static ContactManager getInstance(){
        return INSTANCE;
    }

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /** Setup context and instance */
        INSTANCE = this;
        mContext = this;
    }
}
