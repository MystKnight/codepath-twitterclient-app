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

import java.util.List;

/**
 * Created by yahuijin on 9/29/15.
 */
public class MentionsFragment extends TweetsListFragment {

    private final String TYPE = "mentions";
    private TwitterClient client;
    private Boolean requiresClearingAdapter = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load offline content by default
        clear();
        addAll(Tweet.getTweetByType(this.TYPE));

        // Make the initial network call to populate the timeline
        this.client = TwitterApplication.getRestClient();
        this.populateMentions();
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
                populateMentions();
            }
        });

        // On swipe, we want to notify the adapter it needs to be cleared before loading more info
        getSwipeContainer().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requiresClearingAdapter = true;
                populateMentions();
            }
        });

        return view;
    }

    private void populateMentions() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        this.client.getMentions(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Got notified that we need to clear up our adapter
                if (requiresClearingAdapter) {
                    // We've just cleared so let's reset
                    requiresClearingAdapter = false;
                    // Clear our cached data
                    Tweet.clearTweetsByType(TYPE);
                    // Clear the adapter
                    clear();
                }

                // Process the tweets and add it to our adapter
                List<Tweet> tweets = Tweet.fromJSONArray(response, TYPE);
                // Add the tweets and hide the refresh button
                addAll(tweets);
                // Reset the swipe refresh bar if it is in progress
                getSwipeContainer().setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                getSwipeContainer().setRefreshing(false);
                Toast.makeText(getActivity(), getResources().getString(R.string.fail_fetch_timeline), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onFinishDialog() {
        // Refresh when we close the dialog fragment
        // Clear out the adapter so we can load in all new data
        // Scroll up to the top of the table view to see what just got updated
        getTweetListView().setSelectionAfterHeaderView();
        this.requiresClearingAdapter = true;
        this.populateMentions();
    }
}
