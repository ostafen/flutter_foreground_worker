package com.ostafen.flutter_foreground_worker.options;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Map;

public class NotificationOptions {
    public static final String CHANNEL_ID_KEY = "channelId";
    public static final String CHANNEL_NAME_KEY = "channelName";
    public static final String CHANNEL_DESCRIPTION_KEY = "channelDescription";
    public static final String NOTIFICATION_CONTENT_TITLE_KEY = "notificationContentTitle";
    public static final String NOTIFICATION_CONTENT_TEXT_KEY = "notificationContentText";
    public static final String SHOW_WHEN_KEY = "showWhen";
    public static final String PLAY_SOUND_KEY = "playSound";
    public static final String VISIBILITY_KEY = "visibility";

    private static final String CHANNEL_ID_DEFAULT = "APP_SERVICE_CHANNEL_ID";
    private static final String CHANNEL_DESCRIPTION_DEFAULT = "";
    private static final String CHANNEL_IMPORTANCE_KEY = "channelImportance";

    private static final String CONTENT_TITLE_DEFAULT = null;
    private static final String CONTENT_TEXT_DEFAULT = "Running";
    private static final String ENABLE_VIBRATION_KEY = "enableVibration";

    private String channelId;
    private String channelName;
    private String channelDescription;
    private String notificationContentTitle;
    private String notificationContentText;
    private int channelImportance;
    private int priority;
    private String contentTitle;
    private String contentText;
    private boolean enableVibration;
    private boolean playSound;
    private boolean showWhen;
    private boolean isSticky;
    private int visibility;
    private String iconResType;
    private String iconName;

    // required for sdk version <= 23
    private <T> T getOrDefault(Map<String, Object> map, String key, T defaultValue) {
        try {
            Object value = map.get(key);
            if(value != null)
                return (T) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
        return defaultValue;
    }
    
    private void load(Map<String, Object> options) {
        channelId = getOrDefault(options, CHANNEL_ID_KEY, CHANNEL_ID_DEFAULT);
        channelName = getOrDefault(options, CHANNEL_NAME_KEY, "");
        channelDescription = getOrDefault(options, CHANNEL_DESCRIPTION_KEY, CHANNEL_DESCRIPTION_DEFAULT);
        notificationContentTitle = getOrDefault(options, NOTIFICATION_CONTENT_TITLE_KEY, CONTENT_TITLE_DEFAULT);
        notificationContentText = getOrDefault(options, NOTIFICATION_CONTENT_TEXT_KEY, CONTENT_TEXT_DEFAULT);
        channelImportance = getOrDefault(options, CHANNEL_IMPORTANCE_KEY, 3); // importance default
        showWhen = getOrDefault(options, SHOW_WHEN_KEY, false);
        playSound = getOrDefault(options, PLAY_SOUND_KEY, false);
        enableVibration = getOrDefault(options, ENABLE_VIBRATION_KEY, false);
        visibility = (getOrDefault(options, VISIBILITY_KEY, NotificationCompat.VISIBILITY_PUBLIC));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static NotificationOptions fromMap(Map<String, Object> options) {
        NotificationOptions notificationOptions = new NotificationOptions();
        notificationOptions.load(options);
        return notificationOptions;
    }

    public String getChannelId() {
        return channelId;
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

    public int getChannelImportance() {
        return channelImportance;
    }

    public boolean getEnableVibration() {
        return enableVibration;
    }

    public int getPriority() {
        return priority;
    }
}
