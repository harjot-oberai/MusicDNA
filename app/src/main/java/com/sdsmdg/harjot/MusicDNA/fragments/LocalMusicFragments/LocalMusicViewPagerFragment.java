package com.sdsmdg.harjot.MusicDNA.fragments.LocalMusicFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalMusicViewPagerFragment extends Fragment {

    ViewPager viewPager;
    MyPageAdapter adapter;
    TabLayout tabLayout;

    ImageView backBtn;
    public ImageView searchIcon;
    public TextView fragTitle;
    public EditText searchBox;

    public boolean isSearchBoxVisible = false;

    public LocalMusicViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_local_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((HomeActivity) getActivity()).onQueryTextChange("");
        isSearchBoxVisible = false;

        backBtn = view.findViewById(R.id.local_fragment_back_btn);
        backBtn.setOnClickListener(v -> getActivity().onBackPressed());

        fragTitle = view.findViewById(R.id.local_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        searchBox = view.findViewById(R.id.local_fragment_search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((HomeActivity) getActivity()).onQueryTextChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchIcon = view.findViewById(R.id.local_fragment_search_icon);
        searchIcon.setOnClickListener(v -> {
            if (isSearchBoxVisible) {
                searchBox.setText("");
                searchBox.setVisibility(View.INVISIBLE);
                getActivity();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                searchIcon.setImageResource(R.drawable.ic_search);
                fragTitle.setVisibility(View.VISIBLE);
            } else {
                searchBox.setVisibility(View.VISIBLE);
                searchBox.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                searchIcon.setImageResource(R.drawable.ic_cross_white);
                fragTitle.setVisibility(View.INVISIBLE);
            }
            isSearchBoxVisible = !isSearchBoxVisible;
        });

        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(Color.parseColor("#111111"));
        tabLayout.setSelectedTabIndicatorColor(HomeActivity.themeColor);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        adapter = new MyPageAdapter(getChildFragmentManager());
        adapter.addFragment(new LocalMusicFragment(), getString(R.string.songs));
        adapter.addFragment(new AlbumFragment(), getString(R.string.albums));
        adapter.addFragment(new ArtistFragment(), getString(R.string.artists));
        adapter.addFragment(new RecentlyAddedSongsFragment(), getString(R.string.recent));
    }

    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    public Fragment getFragmentByPosition(int position) {
        return (adapter != null) ? adapter.getItem(position) : null;
    }
}
