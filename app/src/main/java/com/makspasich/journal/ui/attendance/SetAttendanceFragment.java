package com.makspasich.journal.ui.attendance;

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

import com.makspasich.journal.R;
import com.makspasich.journal.databinding.SetAttendanceFragmentBinding;

public class SetAttendanceFragment extends Fragment {
    private static final String TAG = "SetAttendanceFragment";
    private SetAttendanceFragmentBinding binding;

    public SetAttendanceFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        binding = SetAttendanceFragmentBinding.inflate(inflater, container, false);
        binding.viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        return binding.getRoot();
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
        binding = null;
        super.onDestroyView();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        private PagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return AttendanceFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position + 1);
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
