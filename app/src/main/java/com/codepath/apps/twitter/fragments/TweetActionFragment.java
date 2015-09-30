package com.codepath.apps.twitter.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by yahuijin on 9/30/15.
 */
public class TweetActionFragment extends Fragment {

    public void reply(Tweet tweet) {
        // Show compose dialog
        FragmentManager fm = getActivity().getSupportFragmentManager();
        TweetDialog dialog = TweetDialog.newInstance(getResources().getString(R.string.tweet), tweet);
        dialog.show(fm, "tweet_dialog");
    }

    public void retweet(final Tweet tweet, final Button btnRetweet) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        TwitterClient client = TwitterApplication.getRestClient();
        client.retweet(tweet.getTweetId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                tweet.setRetweeted(true);
                btnRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite_pressed, 0, 0, 0);

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
                Toast.makeText(getActivity(), getResources().getString(R.string.fail_retweet), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void favorite(final Tweet tweet, final Button btnFavorite) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        // Favorite the item
        TwitterClient client = TwitterApplication.getRestClient();
        client.favorite(tweet.getTweetId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                tweet.setFavorited(true);
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
                Toast.makeText(getActivity(), getResources().getString(R.string.fail_favorite), Toast.LENGTH_LONG).show();
            }
        });
    }
}
