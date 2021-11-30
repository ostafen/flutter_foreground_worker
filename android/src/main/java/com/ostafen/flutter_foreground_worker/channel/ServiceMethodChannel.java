package com.ostafen.flutter_foreground_worker.channel;

import android.util.Log;

import java.util.function.Consumer;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

public class ServiceMethodChannel extends MethodChannelAdapter {
    private static final String CHANNEL_NAME = "service";
    private static final String INVOKE_SEND_MESSAGE = "onMessage";
    private static final String CALL_SEND_MESSAGE = "send";
    private static final String CALL_STOP_SERVICE = "stop";
    private static ServiceMethodChannel instance;

    public ServiceMethodChannel(BinaryMessenger messenger) {
        super(CHANNEL_NAME, messenger);
    }

    public static void setInstance(ServiceMethodChannel instance) {
        ServiceMethodChannel.instance = instance;
    }

    public static void sendMessage(Object message) {
        if(instance != null) {
            instance.channel.invokeMethod(INVOKE_SEND_MESSAGE, message);
        } else {
            Log.d("NATIVE/ServiceChannel", "unable to invoke sendMessage");
        }
    }

    public void onSendMessage(Consumer<Object> messageConsumer) {
        onMethodCall(CALL_SEND_MESSAGE, (arg)-> {
            messageConsumer.accept(arg);
            return null;
        });
    }

    public void onStop(Runnable callback) {
        registerRunnable(CALL_STOP_SERVICE, callback);
    }
}
