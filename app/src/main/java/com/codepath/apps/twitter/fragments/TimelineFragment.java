package com.codepath.apps.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.EndlessScrollListener;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yahuijin on 9/29/15.
 */
public class TimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long sinceId = -1;
    private long maxId = -1;
    private Boolean requiresClearingAdapter = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the initial network call to populate the timeline
        this.client = TwitterApplication.getRestClient();
        this.populateTimeline();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Set listeners for scroll and on tap to go to details
        getTweetListView().setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                requiresClearingAdapter = false;
                Log.d("DEBUG - LOAD MORE", Integer.toString(page));
                populateTimeline();
            }
        });

        // On swipe, we want to notify the adapter it needs to be cleared before loading more info
        getSwipeContainer().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requiresClearingAdapter = true;
                maxId = -1;
                populateTimeline();
            }
        });

        return view;
    }

    private void populateTimeline() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        this.client.getTimeline(this.sinceId, this.maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Got notified that we need to clear up our adapter
                if (requiresClearingAdapter) {
                    // We've just cleared so let's reset
                    requiresClearingAdapter = false;
                    // Clear our cached data
                    Tweet.clearTweets();
                    // Clear the adapter
                    clear();
                }

                // Process the tweets and add it to our adapter
                List<Tweet> tweets = Tweet.fromJSONArray(response);
                // Add the tweets and hide the refresh button
                addAll(tweets);
                // Reset the swipe refresh bar if it is in progress
                getSwipeContainer().setRefreshing(false);

                // Set the max id so we are ready to paginate when we infinite scroll
                setPaginateMaxId(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                getSwipeContainer().setRefreshing(false);
                Toast.makeText(getActivity(), getResources().getString(R.string.fail_fetch_timeline), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setPaginateMaxId(List<Tweet> tweets) {
        // Sort tweet ids and then grab the lowest id to paginate with
        Collections.sort(tweets, new Comparator<Tweet>() {
            @Override
            public int compare(Tweet tweet, Tweet t1) {
                if (tweet.getTweetId() < t1.getTweetId()) {
                    return 0;
                }

                return 1;
            }
        });

        // Set the max id so we can paginate correctly
        if (tweets.size() > 0) {
            // Get the last tweet
            Tweet tweet = tweets.get(tweets.size() - 1);
            this.maxId = tweet.getTweetId() - 1;
        }
    }

    public void onFinishDialog() {
        // Refresh when we close the dialog fragment
        // Clear out the adapter so we can load in all new data
        // Scroll up to the top of the table view to see what just got updated
        getTweetListView().setSelectionAfterHeaderView();
        this.requiresClearingAdapter = true;
        this.maxId = -1;
        this.populateTimeline();
    }
}
