package com.codepath.apps.twitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yahuijin on 9/23/15.
 */
@Table(name = "Media")
public class Media extends Model implements Parcelable {

    @Column(name = "media_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long mediaId;
    @Column(name = "media_url")
    private String mediaUrl;
    @Column(name = "width")
    private int width;
    @Column(name = "height")
    private int height;

    public static Media fromJSON(JSONArray jsonArray) {
        Media media = new Media();

        for (int i = 0; i < jsonArray.length(); i++) {
            // Only process the first photo for now
            try {
                // Convert to our own object
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                media.mediaId = jsonObject.getLong("id");
                media.mediaUrl = jsonObject.getString("media_url");

                JSONObject sizes = jsonObject.getJSONObject("sizes");
                JSONObject medium = sizes.getJSONObject("medium");
                media.width = medium.getInt("w");
                media.height = medium.getInt("h");
                media.save();

                break;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return media;
    }

    public long getMediaId() {
        return mediaId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mediaId);
        dest.writeString(this.mediaUrl);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.mediaId = in.readLong();
        this.mediaUrl = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
