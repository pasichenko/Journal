package com.makspasich.journal.fragments.SetAttendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.makspasich.journal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SetAttendanceFragment extends Fragment {
    private static final String TAG = "SetAttendanceFragment";
    private View mRootView;
    private Unbinder mUnbinder;

    //region BindView
    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;
    //endregion

    private String mKeyGroup;
    private String mDate;

    public SetAttendanceFragment(String mKeyGroup) {
        this.mKeyGroup = mKeyGroup;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        mDate = formatter.format(currentTime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.set_attendance_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        setHasOptionsMenu(true);
        mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private class ItemViewPager {
            String title;
            Fragment fragment;

            public ItemViewPager(String title, Fragment fragment) {
                this.title = title;
                this.fragment = fragment;
            }
        }

        private List<ItemViewPager> items;

        private PagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
            items = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                items.add(new ItemViewPager(String.valueOf(i), new ListAttendanceFragment(mKeyGroup, mDate, i)));
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position).fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items.get(position).title;
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}
