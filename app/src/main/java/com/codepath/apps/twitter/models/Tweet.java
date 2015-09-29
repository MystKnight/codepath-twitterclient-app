package com.codepath.apps.twitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.twitter.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yahuijin on 9/17/15.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable {

    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long tweetId;
    @Column(name = "text")
    private String text;
    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "retweet_count")
    private int retweetCount;
    @Column(name = "favorite_count")
    private int favoriteCount;
    @Column(name = "retweeted")
    private Boolean retweeted;
    @Column(name = "favorited")
    private Boolean favorited;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "media", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Media media;

    public Tweet() {
        super();
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            // Convert to our own object
            tweet.text = jsonObject.getString("text");
            tweet.tweetId = jsonObject.getLong("id");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

            // Check to see if we need to process a media asset
            JSONObject entities = jsonObject.getJSONObject("entities");
            if (entities.has("media")) {
                tweet.media = Media.fromJSON(entities.getJSONArray("media"));
            }
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // Convert from json
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);

                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    public static List<Tweet> getTweets() {
        // Return all stored tweets
        return new Select().from(Tweet.class).execute();
    }

    public static void clearTweets() {
        // Delete and clear out tweets
        new Delete().from(Tweet.class).execute();
    }

    public long getTweetId() {
        return tweetId;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public String getText() {
        return this.text;
    }

    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public String getSmartDate() {
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date date = null;

        try {
            date = df.parse(this.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Utils.getSmartDate(date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.tweetId);
        dest.writeString(this.text);
        dest.writeString(this.createdAt);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favoriteCount);
        dest.writeValue(this.retweeted);
        dest.writeValue(this.favorited);
        dest.writeParcelable(this.user, 0);
        dest.writeParcelable(this.media, 0);
    }

    protected Tweet(Parcel in) {
        this.tweetId = in.readLong();
        this.text = in.readString();
        this.createdAt = in.readString();
        this.retweetCount = in.readInt();
        this.favoriteCount = in.readInt();
        this.retweeted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.favorited = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.media = in.readParcelable(Media.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
