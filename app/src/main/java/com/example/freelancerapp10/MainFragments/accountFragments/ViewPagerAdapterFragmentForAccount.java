package com.example.freelancerapp10.MainFragments.accountFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;




public class ViewPagerAdapterFragmentForAccount extends FragmentStateAdapter {
    public ViewPagerAdapterFragmentForAccount(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new DetailFragment();
        }
        else if (position == 1) {
            return new ProfileFragment();
        }
        return new DetailFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
