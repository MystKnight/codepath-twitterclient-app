package com.codepath.apps.twitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.fragments.TweetFragment;

public class TweetDetailActivity extends AppCompatActivity {

    TweetFragment tweetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // Set up the custom action bar
        this.setUpActionBar();

        this.tweetFragment = (TweetFragment)getSupportFragmentManager().findFragmentById(R.id.tweet_fragment);
    }

    private void setUpActionBar() {
        // Set action bar for custom views
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_detail_actionbar, null);

        ImageButton imageButton = (ImageButton) mCustomView.findViewById(R.id.ib_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    public void onReply(View view) {
        this.tweetFragment.onReply(view);
    }

    public void onRetweet(View view) {
        this.tweetFragment.onRetweet(view);
    }

    public void onFavorite(View view) {
        this.tweetFragment.onFavorite(view);
    }
}
