package com.example.harjot.musicstreamer;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StreamMusicFragment.OnTrackSelectedListener,LocalMusicFragment.OnLocalTrackSelectedListener {

    static Toolbar toolbar;
    static TabLayout tabLayout;
    static ViewPager viewPager;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StreamMusicFragment(), "Stream");
        adapter.addFragment(new LocalMusicFragment(), "Local");
        viewPager.setAdapter(adapter);
    }

    public void onTrackSelected(int position) {

        Fragment frag = getSupportFragmentManager().findFragmentByTag("player");
        FragmentManager fm = getSupportFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            fm.beginTransaction()
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            PlayerFragment.mMediaPlayer.stop();
            PlayerFragment.mMediaPlayer.reset();
            PlayerFragment.mVisualizer.release();
            if (StreamMusicFragment.selectedTrack == PlayerFragment.track) {
                fm.beginTransaction()
                        .show(frag)
                        .commit();
            } else {
                fm.beginTransaction()
                        .remove(frag)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

    }

    @Override
    public void onLocalTrackSelected(int position) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("player");
        FragmentManager fm = getSupportFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            fm.beginTransaction()
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            PlayerFragment.mMediaPlayer.stop();
            PlayerFragment.mMediaPlayer.reset();
            PlayerFragment.mVisualizer.release();
            if (LocalMusicFragment.selectedTrack == PlayerFragment.localTrack) {
                fm.beginTransaction()
                        .show(frag)
                        .commit();
            } else {
                fm.beginTransaction()
                        .remove(frag)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("player");
        if (frag == null)
            super.onBackPressed();
        else
            getSupportFragmentManager().beginTransaction()
                    .hide(frag)
                    .commit();
    }
}
