package com.ostafen.flutter_foreground_worker.channel;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MethodChannelAdapter implements MethodChannel.MethodCallHandler {
    protected MethodChannel channel;

    private final Map<String, Function<?, ?>> methodHandlers = new HashMap<>();

    public MethodChannelAdapter(MethodChannel channel) {
        this.channel = channel;
        channel.setMethodCallHandler(this);
    }

    public MethodChannelAdapter(String channelName, BinaryMessenger messenger) {
        this(new MethodChannel(messenger, channelName));
    }

    public void onMethodCall(String methodName, Function<?, ?> callback) {
        methodHandlers.put(methodName, callback);
    }

    protected void registerRunnable(String methodName, Runnable callback) {
        onMethodCall(methodName, (arg) -> {
            callback.run();
            return null;
        });
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        @SuppressWarnings("unchecked")
        Function<Object, Object> handler = (Function<Object, Object>) methodHandlers.get(call.method);

        if(handler == null) {
            result.notImplemented();
            return;
        }

        try {
            Object o = handler.apply(call.arguments);
            result.success(o);
        } catch(Exception e) {
           result.error(e.getClass().getName(), e.getMessage(), e.getLocalizedMessage());
        }
    }

}
