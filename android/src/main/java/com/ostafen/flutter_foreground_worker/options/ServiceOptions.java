package com.ostafen.flutter_foreground_worker.options;

import java.util.Map;

public class ServiceOptions {
    private static ServiceOptions instance;
    private NotificationOptions notificationOptions;
    private static final String CALLBACK_HANDLE_KEY = "callbackHandle";
    private long callbackHandle;

    public static ServiceOptions fromMap(Map<String, Object> options) {
        ServiceOptions preferences = new ServiceOptions();
        preferences.callbackHandle = (long) options.getOrDefault(CALLBACK_HANDLE_KEY, -1);
        Map<String, Object> notificationObject = (Map<String, Object>) options.get("notification");
        preferences.notificationOptions = NotificationOptions.fromMap(notificationObject);
        return preferences;
    }

    public static ServiceOptions getInstance() {
        return instance;
    }

    public static void setInstance(ServiceOptions instance) {
        ServiceOptions.instance = instance;
    }

    public long getCallbackHandle() {
        return callbackHandle;
    }

    public NotificationOptions getNotificationOptions() {
        return notificationOptions;
    }

}
