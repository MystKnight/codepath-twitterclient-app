package com.codepath.apps.twitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.TweetDetailActivity;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetsAdapter;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yahuijin on 9/29/15.
 */
public class TweetsListFragment extends TweetActionFragment {

    private TwitterClient client;
    private TweetsAdapter tweetsAdapter;
    private List<Tweet> tweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // Set up the list view
        this.lvTweets = (ListView)view.findViewById(R.id.lv_tweets);
        this.lvTweets.setAdapter(this.tweetsAdapter);

        // Navigate to the tweet details page
        this.lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tweet tweet = getTweets().get(i);

                // Declare our intent and pass the tweet object
                Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                intent.putExtra("tweet", tweet);
                startActivity(intent);
            }
        });

        // On swipe, we want to notify the adapter it needs to be cleared before loading more info
        this.swipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.blue_twitter);

        this.client = TwitterApplication.getRestClient();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Attempt to fetch tweets from our offline storage
        this.tweets = new ArrayList<>();
        this.tweetsAdapter = new TweetsAdapter(getActivity(), this.tweets);
    }

    public ListView getTweetListView() {
        return this.lvTweets;
    }

    public List<Tweet> getTweets() {
        return this.tweets;
    }

    public SwipeRefreshLayout getSwipeContainer() {
        return this.swipeContainer;
    }

    public void addAll(List<Tweet> tweets) {
        this.tweetsAdapter.addAll(tweets);
    }

    public void clear() {
        this.tweetsAdapter.clear();
    }

    public void onReply(View view) {
        int position = (Integer)view.getTag();
        // The tweet the user is replying to
        Tweet tweet = getTweets().get(position);

        // Show the tweet dialog
        this.reply(tweet);
    }

    public void onRetweet(View view) {
        // The tweet the user is replying to
        int position = (Integer)view.getTag();
        Tweet tweet = tweets.get(position);
        Button btnRetweet = (Button)view.findViewById(R.id.btn_retweet);

        // Retweet
        this.retweet(tweet, btnRetweet);
    }

    public void onFavorite(View view) {
        // Get the tweet and button
        int position = (Integer)view.getTag();
        Tweet tweet = tweets.get(position);
        Button btnFavorite = (Button)view.findViewById(R.id.btn_favorite);

        // Favorite
        favorite(tweet, btnFavorite);
    }
}
