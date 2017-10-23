package com.gmonetix.pedals;

import android.app.Application;
import com.gmonetix.pedals.util.SharedPref;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author Gmonetix
 */

public class App extends Application {

    public static DatabaseReference dbRef;
    public FirebaseDatabase db;
    public static SharedPref sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPref = new SharedPref(this);
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference();
    }

    public static DatabaseReference getDbReference() {
        return dbRef;
    }

    public static SharedPref getSharedPref() {
        return sharedPref;
    }

}
