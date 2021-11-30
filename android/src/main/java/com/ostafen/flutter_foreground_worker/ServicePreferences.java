package com.ostafen.flutter_foreground_worker;


import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

public class ServicePreferences {
    public static final String PREFERENCES_KEY = "PREFERENCES_KEY";
    public static final String CALLBACK_HANDLE_KEY = "callbackHandle";
    public static final String CHANNEL_NAME_KEY = "channelName";
    public static final String CHANNEL_DESCRIPTION_KEY = "channelDescription";
    public static final String NOTIFICATION_CONTENT_TITLE_KEY = "notificationContentTitle";
    public static final String NOTIFICATION_CONTENT_TEXT_KEY = "notificationContentText";
    public static final String SHOW_WHEN_KEY = "showWhen";
    public static final String PLAY_SOUND_KEY = "playSound";
    public static final String VISIBILITY_KEY = "visibility";

    private static final String CHANNEL_NAME_DEFAULT = "CHANNEL_NAME_DEFAULT";
    private static final String CHANNEL_DESCRIPTION_DEFAULT = "CHANNEL_DESCRIPTION_DEFAULT";

    private static final String CONTENT_TITLE_DEFAULT = null;
    private static final String CONTENT_TEXT_DEFAULT = "Running";

    private long serviceCallbackHandle;
    private String channelName;
    private String channelDescription;
    private String notificationContentTitle;
    private String notificationContentText;

    private boolean showWhen;
    private boolean playSound;
    private int visibility;

    public ServicePreferences(SharedPreferences preferences) {
        loadPreferences(preferences);
    }

    private void loadPreferences(SharedPreferences preferences) {
        this.serviceCallbackHandle = preferences.getLong(CALLBACK_HANDLE_KEY, -1);
        this.channelName = preferences.getString(CHANNEL_NAME_KEY, CHANNEL_NAME_DEFAULT);
        this.channelDescription = preferences.getString(CHANNEL_DESCRIPTION_KEY, CHANNEL_DESCRIPTION_DEFAULT);
        this.notificationContentTitle = preferences.getString(NOTIFICATION_CONTENT_TITLE_KEY, CONTENT_TITLE_DEFAULT);
        this.notificationContentText = preferences.getString(NOTIFICATION_CONTENT_TEXT_KEY, CONTENT_TEXT_DEFAULT);
        this.showWhen = preferences.getBoolean(SHOW_WHEN_KEY, false);
        this.playSound = preferences.getBoolean(PLAY_SOUND_KEY, false);
        this.visibility = preferences.getInt(VISIBILITY_KEY, NotificationCompat.VISIBILITY_PUBLIC);
    }

    public long getServiceCallbackHandle() {
        return serviceCallbackHandle;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public String getNotificationContentTitle() {
        return notificationContentTitle;
    }

    public String getNotificationContentText() {
        return notificationContentText;
    }

    public boolean getShowWhen() {
        return showWhen;
    }

    public boolean getPlaySound() {
        return playSound;
    }

    public int getVisibility() {
        return visibility;
    }
}
