package com.codepath.apps.twitter.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.network.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by yahuijin on 9/20/15.
 */
public class TweetDialog extends DialogFragment {

    private TextView tvCharCounter;
    private EditText etTweet;
    private final int TWITTER_URL_LENGTH = 22;
    private final int TWITTER_MAX_CHARACTERS = 140;

    public interface  TweetDialogListener {
        void onFinishDialog();
    }

    public static TweetDialog newInstance(String title, Tweet tweet) {
        TweetDialog dialog = new TweetDialog();

        Bundle args = new Bundle();
        args.putString("title", title);

        if (tweet != null) {
            args.putParcelable("tweet", tweet);
        }

        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = getActivity().getLayoutInflater().inflate(R.layout.tweet_dialog, container);

        Tweet tweet = getArguments().getParcelable("tweet");

        // Setup UI
        TextView tvScreenName = (TextView)view.findViewById(R.id.tv_screen_name);
        tvScreenName.setText(User.getCurrentUser().getScreenName());

        this.tvCharCounter = (TextView)view.findViewById(R.id.tv_char_counter);
        this.etTweet = (EditText)view.findViewById(R.id.et_tweet);

        ImageView ivAvatar = (ImageView)view.findViewById(R.id.iv_avatar);
        Picasso.with(getContext()).load(User.getCurrentUser().getProfileImageUrl()).fit().into(ivAvatar);

        // Whenever a tweet is passed in, it means we are replying to someone
        if (tweet != null) {
            etTweet.setText(tweet.getUser().getScreenName() + " ");
            // Calculate character count with the initial screen name
            int counter = calculateCharacterCount(etTweet.getText().toString());
            tvCharCounter.setText(Integer.toString(counter));
        }

        etTweet.requestFocus();
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Calculate character count
                int counter = calculateCharacterCount(charSequence.toString());
                tvCharCounter.setTextColor(Color.BLACK);
                // Set text to red if we go over the character limit
                if (counter < 0) {
                    tvCharCounter.setTextColor(Color.RED);
                }
                tvCharCounter.setText(Integer.toString(counter));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button btnTweet = (Button)view.findViewById(R.id.btn_tweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure we hae internet
                if (!Utils.isNetworkAvailable(getContext())) {
                    Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    return;
                }

                String tweet = etTweet.getText().toString();
                // Check to make sure the string is not empty
                if (tweet.equals("")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error_empty_tweet), Toast.LENGTH_LONG).show();
                    return;
                }

                // Make sure of 140 character count
                if (tweet.length() > TWITTER_MAX_CHARACTERS) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error_tweet_length), Toast.LENGTH_LONG).show();
                    return;
                }

                TwitterClient client = TwitterApplication.getRestClient();
                client.updateStatus(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // Call out to our activity listener
                        TweetDialogListener listener = (TweetDialogListener)getActivity();
                        listener.onFinishDialog();

                        Log.d("DEBUG", response.toString());
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(getContext(), getResources().getString(R.string.error_tweeting), Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", errorResponse.toString());
                    }
                });
            }
        });

        Button btnCancel = (Button)view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private Matcher checkURL(CharSequence s) {
        Matcher matcher = Patterns.WEB_URL.matcher(s);
        return matcher;
    }

    private int calculateCharacterCount(CharSequence string) {
        Matcher matcher = this.checkURL(string);

        int linkCount = 0;
        while (matcher.find()) {
            linkCount++;
        }

        String removedURLs = matcher.replaceAll("");

        return (TWITTER_MAX_CHARACTERS - (linkCount * TWITTER_URL_LENGTH) - removedURLs.toString().length());
    }
}
