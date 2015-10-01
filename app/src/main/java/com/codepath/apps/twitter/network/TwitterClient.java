package com.codepath.apps.twitter.network;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "59hz1x6gOM5eylAkNwq8S4kt9";
	public static final String REST_CONSUMER_SECRET = "1ayPnSPRWYaj9KPPcRT6GPBV9jOofMGwR2R40BBjbRLSSwXsUt";
	public static final String REST_CALLBACK_URL = "oauth://cptwitter"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");

		client.get(apiUrl, null, handler);
	}

	public void getTimeline(long since_id, long max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (since_id != -1) {
			params.put("since_id", since_id);
		}

		if (max_id != -1) {
			params.put("max_id", max_id);
		}

		client.get(apiUrl, params, handler);
	}

	public void getMentions(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);

		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");

		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);

		client.get(apiUrl, params, handler);
	}

	public void updateStatus(String tweet, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("status", tweet);

		this.updateStatusHelper(params, handler);
	}

	public void reply(long replyToStatusId, String tweet, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		// Make sure the username is part of the status
		params.put("status", tweet);
		params.put("in_reply_to_status_id", replyToStatusId);

		this.updateStatusHelper(params, handler);
	}

	public void retweet(long tweetId, AsyncHttpResponseHandler handler) {
		String constructUrl = String.format("statuses/retweet/%d.json", tweetId);
		String apiUrl = getApiUrl(constructUrl);

		client.post(apiUrl, null, handler);
	}

	public void favorite(long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");

		RequestParams params = new RequestParams();
		params.put("id", tweetId);

		client.post(apiUrl, params, handler);
	}

	private void updateStatusHelper(RequestParams params, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		client.post(apiUrl, params, handler);
	}
}