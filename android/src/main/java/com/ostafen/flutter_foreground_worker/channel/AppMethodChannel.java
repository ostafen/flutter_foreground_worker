package com.ostafen.flutter_foreground_worker.channel;

import android.util.Log;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.flutter.plugin.common.BinaryMessenger;

public class AppMethodChannel extends MethodChannelAdapter {
    private static AppMethodChannel instance;
    private static final String CHANNEL_NAME = "main";
    private static final String CALL_START_SERVICE = "startForegroundService";
    private static final String CALL_STOP_SERVICE = "stopForegroundService";
    private static final String CALL_SEND_SERVICE_MESSAGE = "sendMessage";
    private static final String CALL_SERVICE_IS_RUNNING = "isRunning";

    public AppMethodChannel(BinaryMessenger messenger) {
        super(CHANNEL_NAME, messenger);
    }

    public static void setInstance(AppMethodChannel instance) {
        AppMethodChannel.instance = instance;
    }

    public void onStartForegroundService(Consumer<Map<String, Object>> callbackConsumer) {
        onMethodCall(CALL_START_SERVICE, (Map<String, Object> callbackHandle) -> {
           callbackConsumer.accept(callbackHandle);
           return null;
        });
    }

    public void onStopForegroundService(Runnable callback) {
        registerRunnable(CALL_STOP_SERVICE, callback);
    }

    public void onServiceSendMessage(Consumer<Object> messageConsumer) {
        onMethodCall(CALL_SEND_SERVICE_MESSAGE, (message) -> {
            messageConsumer.accept(message);
            return null;
        });
    }

    public void onServiceIsRunning(Supplier<Boolean> supplier) {
        onMethodCall(CALL_SERVICE_IS_RUNNING, (args) -> supplier.get());
    }

    public static void sendMessage(Object message) {
        if(instance != null) {
            instance.channel.invokeMethod("onServiceMessage", message);
        } else {
            Log.d("NATIVE", "channel was null during send");
        }
    }
}
