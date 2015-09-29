package com.codepath.apps.twitter.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;

/**
 * Created by yahuijin on 9/28/15.
 */
public class TweetActionBarView extends RelativeLayout {

    Tweet tweet;

    public TweetActionBarView(Context context) {
        super(context);
        init(context);
    }

    public TweetActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TweetActionBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.tweet_action_bar_fragment, this);

        Button btnReply = (Button)findViewById(R.id.btn_reply);
        Button btnRetweet = (Button)findViewById(R.id.btn_retweet);
        Button btnFavorite = (Button)findViewById(R.id.btn_favorite);

        // Set the button text if we have any sort of valid counts
        btnRetweet.setText("");
        if (tweet.getRetweetCount() != 0) {
            String tweetCount = Integer.toString(tweet.getRetweetCount());
            btnRetweet.setText(tweetCount);
        }

        btnFavorite.setText("");
        if (tweet.getFavoriteCount() != 0) {
            String favoriteCount = Integer.toString(tweet.getFavoriteCount());
            btnFavorite.setText(favoriteCount);
        }

        // Set the button states depending on if we have favorited or retweeted before
        btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite, 0, 0, 0);
        if (tweet.getFavorited()) {
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite_pressed, 0, 0, 0);
        }

        btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet, 0, 0, 0);
        if (tweet.getRetweeted()) {
            btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet_pressed, 0, 0, 0);
        }
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }
}
