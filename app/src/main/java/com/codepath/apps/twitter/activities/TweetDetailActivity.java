package com.codepath.apps.twitter.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.fragments.TweetDialog;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

public class TweetDetailActivity extends AppCompatActivity {

    private Tweet tweet;
    private Button btnRetweet;
    private Button btnFavorite;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // Set up the custom action bar
        this.setUpActionBar();

        // Load up the tweet
        this.tweet = getIntent().getParcelableExtra("tweet");

        // Set up our UI
        TextView tvScreenName = (TextView)findViewById(R.id.tv_screen_name);
        TextView tvName = (TextView)findViewById(R.id.tv_name);
        TextView tvMessage = (TextView)findViewById(R.id.tv_body);
        ImageView ivAvatar = (ImageView)findViewById(R.id.iv_avatar);
        Button btnRetweet = (Button)findViewById(R.id.btn_retweet);
        Button btnFavorite = (Button)findViewById(R.id.btn_favorite);

        tvScreenName.setText(tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvMessage.setText(tweet.getText());
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).fit().into(ivAvatar);

        // Do not show media asset by default
        ImageView media = (ImageView)findViewById(R.id.iv_media);
        media.setVisibility(View.GONE);
        // If we have a valid media, let's show it
        if (this.tweet.getMedia() != null) {
            Picasso.with(this).load(this.tweet.getMedia().getMediaUrl()).into(media);
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

        // Set up the client
        this.client = TwitterApplication.getRestClient();
    }

    public void onRetweet(View view) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        this.btnRetweet = (Button)view.findViewById(R.id.btn_retweet);

        this.client.retweet(this.tweet.getTweetId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_retweet, 0, 0, 0);

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
                Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.fail_retweet), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onFavorite(View view) {
        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        this.btnFavorite = (Button)view.findViewById(R.id.btn_favorite);

        // Favorite the item
        this.client.favorite(this.tweet.getTweetId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite_pressed, 0, 0, 0);

                // Set the favorite text and icon on favorite
                String favoriteCountStr = btnFavorite.getText().toString();
                if (favoriteCountStr == "") {
                    btnFavorite.setText("1");
                } else {
                    int faovriteCount = Integer.parseInt(favoriteCountStr);
                    faovriteCount++;
                    btnFavorite.setText(Integer.toString(faovriteCount));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.fail_favorite), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onReply(View view) {
        FragmentManager fm = getSupportFragmentManager();
        TweetDialog dialog = TweetDialog.newInstance(
                getResources().getString(R.string.tweet),
                tweet);
        dialog.show(fm, "tweet_dialog");
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
}
