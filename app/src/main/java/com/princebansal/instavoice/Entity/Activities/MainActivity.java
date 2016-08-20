
/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Entity.Activities;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.princebansal.instavoice.Entity.Fragments.ChatFragment;
import com.princebansal.instavoice.Entity.Fragments.FollowFragment;
import com.princebansal.instavoice.R;


public class MainActivity extends AppCompatActivity {

    private static MediaRecorder mediaRecorder;

    private static final int NUM_PAGES = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    public boolean messageSelectedForFavourite = false;
    public Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setInit();
        setData();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void setInit() {
        adapter = new PagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.page_titles));
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    private void setData() {
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.i(TAG, "onCreateOptionsMenu: ");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles;

        public PagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ChatFragment chatFragment = ChatFragment.newInstance();
                    return chatFragment;
                case 1:
                    FollowFragment followFragment = FollowFragment.newInstance();
                    return followFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

    public static MediaRecorder getMediaRecorderInstance(){
        if(mediaRecorder==null){
            mediaRecorder=new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
        }
        return mediaRecorder;
    }

}
