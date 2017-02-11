package com.example.cnavo.teco_envble.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.cnavo.teco_envble.R;
import com.example.cnavo.teco_envble.service.DBHelper;
import com.example.cnavo.teco_envble.service.Descriptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by cnavo on 07.02.2017.
 */

@EActivity(R.layout.main_activity)
public class MainActivity extends AppCompatActivity {

    private static final int NUM_OF_PAGES = 8;

    @ViewById(R.id.main_view_pager)
    ViewPager viewPager;
    @ViewById(R.id.main_tab_layout)
    TabLayout tabLayout;

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

        GraphFragment coFragment;
        GraphFragment no2Fragment;
        GraphFragment nh3Fragment;
        GraphFragment temperatureFragment;
        GraphFragment humidityFragment;
        GraphFragment pressureFragment;
        GraphFragment dustFragment;

        private String[] pageTitles = {getResources().getString(R.string.connections),
                Descriptions.CO.toString(),
                Descriptions.NO2.toString(),
                Descriptions.NH3.toString(),
                Descriptions.TEMPERATURE.toString(),
                Descriptions.HUMIDITY.toString(),
                Descriptions.PRESSURE.toString(),
                Descriptions.DUST.toString()};
        private ConnectionFragment connectionFragment;

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
            this.connectionFragment = ConnectionFragment.create();
            this.coFragment = GraphFragment.create(Descriptions.CO.toString());
            this.no2Fragment = GraphFragment.create(Descriptions.NO2.toString());
            this.nh3Fragment = GraphFragment.create(Descriptions.NH3.toString());
            this.temperatureFragment = GraphFragment.create(Descriptions.TEMPERATURE.toString());
            this.humidityFragment = GraphFragment.create(Descriptions.HUMIDITY.toString());
            this.pressureFragment = GraphFragment.create(Descriptions.PRESSURE.toString());
            this.dustFragment = GraphFragment.create(Descriptions.DUST.toString());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.connectionFragment;
                case 1:
                    this.coFragment.loadGraph();
                    return this.coFragment;
                case 2:
                    this.no2Fragment.loadGraph();
                    return this.no2Fragment;
                case 3:
                    this.nh3Fragment.loadGraph();
                    return this.nh3Fragment;
                case 4:
                    this.temperatureFragment.loadGraph();
                    return this.temperatureFragment;
                case 5:
                    this.humidityFragment.loadGraph();
                    return this.humidityFragment;
                case 6:
                    this.pressureFragment.loadGraph();
                    return this.pressureFragment;
                case 7:
                    this.dustFragment.loadGraph();
                    return this.dustFragment;
                default:
                    return this.connectionFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }
    }

}
