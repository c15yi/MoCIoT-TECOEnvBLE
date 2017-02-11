package com.example.cnavo.teco_envble.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.cnavo.teco_envble.R;
import com.example.cnavo.teco_envble.service.DBHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by cnavo on 07.02.2017.
 */

@EActivity(R.layout.main_activity)
public class MainActivity extends AppCompatActivity {

    private static final int NUM_OF_PAGES = 2;

    @ViewById(R.id.main_view_pager)
    ViewPager viewPager;

    private ViewPageAdapter viewPageAdapter;

    public static void start(@NonNull Activity activity) {
        MainActivity_.intent(activity).start();
    }

    @AfterViews
    void initViewPager() {
        this.viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(viewPageAdapter);

        DBHelper.getDBHelper().createDB();
        DBHelper.getDBHelper().readFullDB();
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

        GraphFragment graphFragment;
        private ConnectionFragment connectionFragment;

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
            this.connectionFragment = ConnectionFragment.create();
            this.graphFragment = GraphFragment.create();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.connectionFragment;
                case 1:
                    return this.graphFragment;
                default:
                    return this.connectionFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }
    }

}
