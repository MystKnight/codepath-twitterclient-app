package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitter.utils.EndlessScrollListener;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.fragments.TweetDialog;
import com.codepath.apps.twitter.models.TweetsAdapter;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements TweetDialog.TweetDialogListener {

    private TwitterClient client;
    private TweetsAdapter tweetsAdapter;
    private List<Tweet> tweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    private Boolean requiresClearingAdapter = false;
    private long sinceId = -1;
    private long maxId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set up the custom action bar
        this.setUpActionBar();

        // Attempt to fetch tweets from our offline storage
        this.tweets = Tweet.getTweets();
        this.tweetsAdapter = new TweetsAdapter(this, this.tweets);

        // Set up the list view
        this.lvTweets = (ListView) findViewById(R.id.lv_tweets);
        this.lvTweets.setAdapter(this.tweetsAdapter);
        this.lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                requiresClearingAdapter = false;
                Log.d("DEBUG - LOAD MORE", Integer.toString(page));
                populateTimeline();
            }
        });
        this.lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tweet tweet = tweets.get(i);

                // Declare our intent and pass the tweet object
                Intent intent = new Intent(getApplicationContext(), TweetDetailActivity.class);
                intent.putExtra("tweet", tweet);
                startActivity(intent);
            }
        });

        // On swipe, we want to notify the adapter it needs to be cleared before loading more info
        this.swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        this.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requiresClearingAdapter = true;
                maxId = -1;
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.blue_twitter);

        // Make the initial network call to populate the timeline
        this.client = TwitterApplication.getRestClient();
        this.populateTimeline();
    }

    private void populateTimeline() {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        this.client.getTimeline(this.sinceId, this.maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Got notified that we need to clear up our adapter
                if (requiresClearingAdapter) {
                    // Clear our cached data
                    Tweet.clearTweets();
                    // Clear the adapter
                    tweetsAdapter.clear();
                }

                // Process the tweets and add it to our adapter
                List<Tweet> tweets = Tweet.fromJSONArray(response);
                // Add the tweets and hide the refresh button
                tweetsAdapter.addAll(tweets);
                // Reset the swipe refresh bar if it is in progress
                swipeContainer.setRefreshing(false);

                // Set the max id so we are ready to paginate when we infinite scroll
                setPaginateMaxId(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
                Toast.makeText(TimelineActivity.this, getResources().getString(R.string.fail_fetch_timeline), Toast.LENGTH_LONG).show();
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

    @Override
    public void onFinishDialog() {
        // Refresh when we close the dialog fragment
        // Clear out the adapter so we can load in all new data
        // Scroll up to the top of the table view to see what just got updated
        this.lvTweets.setSelectionAfterHeaderView();
        this.requiresClearingAdapter = true;
        this.maxId = -1;
        this.populateTimeline();
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

     public void onReply(View view) {
         int position = (Integer)view.getTag();
         // The tweet the user is replying to
         Tweet tweet = this.tweets.get(position);

         // Show compose dialog
         FragmentManager fm = getSupportFragmentManager();
         TweetDialog dialog = TweetDialog.newInstance(getResources().getString(R.string.tweet), tweet);
         dialog.show(fm, "tweet_dialog");
    }

    public void onRetweet(View view) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        final Button btnRetweet = (Button)view.findViewById(R.id.btn_retweet);
        int position = (Integer)view.getTag();
        // The tweet the user is replying to
        Tweet tweet = this.tweets.get(position);

        // Retweet the tweet
        this.client.retweet(tweet.getTweetId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet_pressed, 0, 0, 0);

                // Set the retweet text and icon on retweet
                String retweetCountStr = btnRetweet.getText().toString();
                if (retweetCountStr == "") {
                    btnRetweet.setText("1");
                } else {
                    int retweetCount = Integer.parseInt(retweetCountStr);
                    retweetCount++;
                    btnRetweet.setText(Integer.toString(retweetCount));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TimelineActivity.this, getResources().getString(R.string.fail_retweet), Toast.LENGTH_LONG).show();
                Log.d("DEBUG", error.toString());
            }
        });
    }

    public void onFavorite(View view) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        final Button btnFavorite = (Button)view.findViewById(R.id.btn_favorite);
        int position = (Integer)view.getTag();
        // The tweet the user is replying to
        Tweet tweet = this.tweets.get(position);

        // Favorite the item
        this.client.favorite(tweet.getTweetId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite_pressed, 0, 0, 0);

                // Set the favorite text and icon on favorite
                String favoriteCountStr = btnFavorite.getText().toString();
                if (favoriteCountStr == "") {
                    btnFavorite.setText("1");
                } else {
                    int favoriteCount = Integer.parseInt(favoriteCountStr);
                    favoriteCount++;
                    btnFavorite.setText(Integer.toString(favoriteCount));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TimelineActivity.this, getResources().getString(R.string.fail_favorite), Toast.LENGTH_LONG).show();
            }
        });
    }
}
