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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yahuijin on 9/30/15.
 */
public class UserTimelineFragment extends TweetsListFragment {

    private final String TYPE = "usertimeline";
    private TwitterClient client;
    private Boolean requiresClearingAdapter = true;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();

        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);

        return userTimelineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load offline content by default
        clear();
        addAll(Tweet.getTweetByType(this.TYPE));

        this.client = TwitterApplication.getRestClient();
        this.populateUserTimeline();
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
                populateUserTimeline();
            }
        });

        // On swipe, we want to notify the adapter it needs to be cleared before loading more info
        getSwipeContainer().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requiresClearingAdapter = true;
                populateUserTimeline();
            }
        });

        //View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    public void populateUserTimeline() {
        String screenName = getArguments().getString("screen_name");
        this.client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
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
                Toast.makeText(getActivity(), getResources().getString(R.string.fail_fetch_usertimeline), Toast.LENGTH_LONG).show();            }
        });
    }
}
