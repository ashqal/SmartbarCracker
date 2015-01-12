package com.ashqal.smartbar;

import java.util.Locale;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.ashqal.smartbar.fragments.MoreFragment;
import com.ashqal.smartbar.fragments.SettingFragment;
import com.ashqal.smartbar.fragments.SetupFragment;
import com.ashqal.xposed.managers.ConfigManager;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化配置文件
        ConfigManager.Instance();

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setOffscreenPageLimit(0);


        final ActionBar actionBar = getSupportActionBar();
        final TabPageIndicator indicator = (TabPageIndicator)mInflater.inflate(R.layout.actionbar_top_pager,null);
        indicator.setViewPager(mViewPager);

        android.support.v7.app.ActionBar.LayoutParams lp =
                new ActionBar.LayoutParams( ActionBar.LayoutParams.WRAP_CONTENT
                        ,ActionBar.LayoutParams.MATCH_PARENT,Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        actionBar.setCustomView(indicator,lp);
        lp.gravity = Gravity.RIGHT;
        actionBar.setDisplayShowCustomEnabled(true);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment f = null;
            switch( position )
            {
                case 0:
                    f = SetupFragment.newInstance("setup");
                    break;
                case 1:
                    f = SettingFragment.newInstance();
                    break;
                case 2:
                    f = MoreFragment.newInstance();
                    break;

            }
            return f;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            //
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_setup).toUpperCase(l);
                case 1:
                    return getString(R.string.title_setting).toUpperCase(l);
                case 2:
                    return getString(R.string.title_more).toUpperCase(l);
                default:
                    break;
            }
            return null;
        }
    }


}
