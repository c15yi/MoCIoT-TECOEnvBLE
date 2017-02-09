package com.example.cnavo.teco_envble;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.cnavo.teco_envble.service.DBHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

/**
 * Created by cnavo on 07.02.2017.
 */

@EActivity(R.layout.main_activity)
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends AppCompatActivity {

    private static final int NUM_OF_PAGES = 2;

    @OptionsMenuItem(R.id.main_menu_store_in_db_menu_item)
    MenuItem storeInDbMenuItem;
    @ViewById(R.id.main_view_pager)
    ViewPager viewPager;

    private SharedPreferences sharedPreferences;
    private ViewPageAdapter viewPageAdapter;

    public static void start(@NonNull Activity activity) {
        MainActivity_.intent(activity).start();
    }

    @AfterViews
    void initViewPager() {
        this.viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(viewPageAdapter);

        DBHelper handler = new DBHelper();
        handler.createDB();
        //handler.writeDB("sensorValues temperature=23,test=8,hum=10");
        handler.readDB("select * from sensorValues");
    }

    @OptionsMenuItem(R.id.main_menu_store_in_db_menu_item)
    void initSharedPreferences(MenuItem menuItem) {
        if (this.sharedPreferences == null) {
            this.sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.store_in_db_shared_preference), menuItem.isChecked());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ViewPageAdapter extends FragmentStatePagerAdapter {

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ConnectionFragment.create();
                case 1:
                    return GraphFragment.create();
                default:
                    return ConnectionFragment.create();
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }
    }

}
