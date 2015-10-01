package com.codepath.apps.twitter.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import com.codepath.apps.twitter.R;

/**
 * Created by yahuijin on 9/29/15.
 */
public class TwitterFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private final int PAGE_COUNT = 3;
    private String tabTitles[];
    private SparseArray<Fragment> fragments = new SparseArray<>();

    public TwitterFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.tabTitles = new String[] {
                context.getResources().getString(R.string.home),
                String.format("@%s", context.getResources().getString(R.string.mentions)),
                context.getResources().getString(R.string.me)
        };
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                TimelineFragment timelineFragment = new TimelineFragment();
                this.fragments.put(position, timelineFragment);
                return timelineFragment;
            }
            case 1: {
                MentionsFragment mentionsFragment = new MentionsFragment();
                this.fragments.put(position, mentionsFragment);
                return mentionsFragment;
            }
            case 2: {
                // Todo: Create a container fragment with child fragments to support real profile view
                UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance("onionpixel");
                this.fragments.put(position, userTimelineFragment);
                return userTimelineFragment;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
}
