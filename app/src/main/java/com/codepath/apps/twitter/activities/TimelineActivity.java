package com.codepath.apps.twitter.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitter.fragments.MentionsFragment;
import com.codepath.apps.twitter.fragments.TimelineFragment;
import com.codepath.apps.twitter.fragments.TweetsListFragment;
import com.codepath.apps.twitter.fragments.TwitterFragmentPagerAdapter;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.fragments.TweetDialog;

public class TimelineActivity extends AppCompatActivity implements TweetDialog.TweetDialogListener {

    private TwitterFragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set up the custom action bar
        this.setUpActionBar();

        // Create the new fragment adapter
        FragmentManager fm = getSupportFragmentManager();
        this.fragmentPagerAdapter = new TwitterFragmentPagerAdapter(fm, this);

        // Set up the view pager
        this.viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerAdapter);

        // Set up the tab strip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    private void setUpActionBar() {
        // Set action bar for custom views
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.custom_actionbar, null);

        ImageButton imageButton = (ImageButton)customView.findViewById(R.id.ib_create);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                TweetDialog dialog = TweetDialog.newInstance(
                        getResources().getString(R.string.tweet),
                        null);
                dialog.show(fm, "tweet_dialog");
            }
        });

        getSupportActionBar().setCustomView(customView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onFinishDialog() {
        Log.d("DEBUG", "Finish Dialog called");
        Fragment fragment = this.fragmentPagerAdapter.getFragment(this.viewPager.getCurrentItem());

        // Check to see which fragment is current in display
        // Refresh the correct fragment
        if (fragment instanceof TimelineFragment) {
            TimelineFragment timelineFragment = (TimelineFragment)fragment;
            timelineFragment.onFinishDialog();
        } else if (fragment instanceof MentionsFragment) {
            MentionsFragment mentionsFragment = (MentionsFragment)fragment;
            mentionsFragment.onFinishDialog();
        }
    }

    public void onReply(View view) {
        this.getFragment().onReply(view);
    }

    public void onRetweet(View view) {
        this.getFragment().onRetweet(view);
    }

    public void onFavorite(View view) {
        this.getFragment().onFavorite(view);
    }

    private TweetsListFragment getFragment() {
        return (TweetsListFragment)this.fragmentPagerAdapter.getFragment(this.viewPager.getCurrentItem());
    }
}
