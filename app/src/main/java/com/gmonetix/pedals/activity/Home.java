package com.gmonetix.pedals.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.gmonetix.pedals.App;
import com.gmonetix.pedals.R;
import com.gmonetix.pedals.dialog.WorkWithUsDialog;
import com.gmonetix.pedals.fragment.BookFragment;
import com.gmonetix.pedals.fragment.ProfileFragment;
import com.gmonetix.pedals.fragment.UserGuideFragment;
import com.gmonetix.pedals.model.Admin;
import com.gmonetix.pedals.util.Const;
import com.gmonetix.pedals.util.SharedPref;
import com.gmonetix.pedals.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import eu.long1.spacetablayout.SpaceTabLayout;

/**
 * @author Gmonetix
 */

public class Home extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.home_button_nav)
    SpaceTabLayout tabLayout;
    @BindView(R.id.home_viewPager)
    ViewPager viewPager;

    private SharedPref sharedPref;

    private List<Fragment> fragmentList;
    private BookFragment bookFragment;
    private UserGuideFragment userGuideFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        sharedPref = App.getSharedPref();

        bookFragment = new BookFragment();
        profileFragment = new ProfileFragment();
        userGuideFragment = new UserGuideFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(bookFragment);
        fragmentList.add(userGuideFragment);
        fragmentList.add(profileFragment);

        tabLayout.initialize(viewPager, getSupportFragmentManager(), fragmentList, savedInstanceState);

        //get admin meta
        App.getDbReference().child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Admin admin = dataSnapshot.getValue(Admin.class);
                sharedPref.setAdminEmail(admin.email);
                sharedPref.setAdminPhone(String.valueOf(admin.phone));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG",""+databaseError.getMessage());
            }
        });

        //get price list
        App.getDbReference().child("PRICE").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sharedPref.setPriceGear(dataSnapshot.child("GEAR").getValue(Integer.class));
                sharedPref.setPriceGearless(dataSnapshot.child("GEARLESS").getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG",""+databaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_onlineServices:
                Util.openUrl(this, Const.GMONETIX_WEBSITE);
                break;
            case R.id.menu_workWithUs:
                WorkWithUsDialog workWithUsDialog = new WorkWithUsDialog(Home.this);
                workWithUsDialog.show();
                break;
            case R.id.menu_shareApp:
                Util.shareApp(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
