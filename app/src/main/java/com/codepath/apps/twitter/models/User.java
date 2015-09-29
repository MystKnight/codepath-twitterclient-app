package com.codepath.apps.twitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yahuijin on 9/17/15.
 */
@Table(name = "Users")
public class User extends Model implements Parcelable {

    @Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long userId;
    @Column(name = "name")
    private String name;
    @Column(name = "screen_name")
    private String screenName;
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    public User() {
        super();
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            // Convert to our own object
            user.userId = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static User getCurrentUser() {
        List<User> userList = new Select().from(User.class).where("screen_name = 'onionpixel'").execute();
        User currentUser = new User();

        if (userList.size() > 0) {
            currentUser = userList.get(0);
        }

        return currentUser;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return String.format("@%s", screenName);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
    }

    protected User(Parcel in) {
        this.userId = in.readLong();
        this.name = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
