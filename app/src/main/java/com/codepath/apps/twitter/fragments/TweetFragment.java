package com.codepath.apps.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.squareup.picasso.Picasso;

/**
 * Created by yahuijin on 9/30/15.
 */
public class TweetFragment extends TweetActionFragment {

    private TwitterClient client;
    private Tweet tweet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load up the tweet
        this.tweet = getActivity().getIntent().getParcelableExtra("tweet");

        // Set up the client
        this.client = TwitterApplication.getRestClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);

        // Set up our UI
        TextView tvScreenName = (TextView)view.findViewById(R.id.tv_screen_name);
        TextView tvName = (TextView)view.findViewById(R.id.tv_name);
        TextView tvMessage = (TextView)view.findViewById(R.id.tv_body);
        ImageView ivAvatar = (ImageView)view.findViewById(R.id.iv_avatar);
        Button btnRetweet = (Button)view.findViewById(R.id.btn_retweet);
        Button btnFavorite = (Button)view.findViewById(R.id.btn_favorite);

        tvScreenName.setText(tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvMessage.setText(tweet.getText());
        Picasso.with(getActivity()).load(tweet.getUser().getProfileImageUrl()).fit().into(ivAvatar);

        // Do not show media asset by default
        ImageView media = (ImageView)view.findViewById(R.id.iv_media);
        media.setVisibility(View.GONE);
        // If we have a valid media, let's show it
        if (this.tweet.getMedia() != null) {
            Picasso.with(getActivity()).load(this.tweet.getMedia().getMediaUrl()).into(media);
            media.setVisibility(View.VISIBLE);
        }

        // Set the button text if we have any sort of valid counts
        btnRetweet.setText("");
        if (this.tweet.getRetweetCount() != 0) {
            String tweetCount = Integer.toString(this.tweet.getRetweetCount());
            btnRetweet.setText(tweetCount);
        }

        btnFavorite.setText("");
        if (this.tweet.getFavoriteCount() != 0) {
            String favoriteCount = Integer.toString(this.tweet.getFavoriteCount());
            btnFavorite.setText(favoriteCount);
        }

        // Set the button states depending on if we have favorited or retweeted before
        btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite, 0, 0, 0);
        if (this.tweet.getFavorited()) {
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite_pressed, 0, 0, 0);
        }

        // If the tweet is from the current user then you cannot retweet your own things
        btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet, 0, 0, 0);
        btnRetweet.setEnabled(true);
        if (tweet.getUser().getScreenName().equals("@onionpixel")) {
            btnRetweet.setEnabled(false);
            btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet_disabled, 0, 0, 0);
        } else if (tweet.getRetweeted()) {
            btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet_pressed, 0, 0, 0);
        }

        return view;
    }

    public void onRetweet(View view) {
        Button btnRetweet = (Button)view.findViewById(R.id.btn_retweet);
        this.retweet(this.tweet, btnRetweet);
    }

    public void onFavorite(View view) {
        Button btnFavorite = (Button)view.findViewById(R.id.btn_favorite);
        this.favorite(this.tweet, btnFavorite);
    }

    public void onReply(View view) {
        this.reply(this.tweet);
    }
}
