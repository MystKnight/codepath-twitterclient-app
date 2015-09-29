package com.codepath.apps.twitter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by yahuijin on 9/17/15.
 */
public class Utils {

    public static String getSmartDate(long since1970) {
        // Gets the short version of the dates
        String smartDate;
        Date date = new Date(since1970);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);

        Calendar calendarNow = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendarNow.setTime(new Date());

        long timeDiff = calendarNow.getTime().getTime() - calendar.getTime().getTime();
        long timeDiffSecs = timeDiff / 1000;
        long timeDiffMins = timeDiff / (60 * 1000);
        long timeDiffHours = timeDiff / (60 * 60 * 1000);
        long timeDiffDays = timeDiff / (60 * 60 * 1000 * 24);
        long timeDiffWeeks = timeDiff / (60 * 60 * 1000 * 24 * 7);

        // If the post is less than a minute, show now
        if (timeDiffSecs < 60) {
            smartDate = "Now";
        }
        // If the post is less than 60 mins old, show in minutes
        else if (timeDiffMins < 60) {
            smartDate = String.format("%dm", timeDiffMins);
        }
        // If the post is less than 24 hours old, show in hours
        else if (timeDiffHours < 24) {
            smartDate = String.format("%dh", timeDiffHours);
        }
        // If the post is less than 7 days old, show in days
        else if (timeDiffDays < 7) {
            smartDate = String.format("%dd", timeDiffDays);
        }
        // Otherwise, show in weeks
        else {
            smartDate = String.format("%dw", timeDiffWeeks);
        }

        return smartDate;
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
