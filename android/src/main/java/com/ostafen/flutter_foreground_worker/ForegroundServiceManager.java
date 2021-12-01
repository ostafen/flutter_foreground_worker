package com.ostafen.flutter_foreground_worker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.ostafen.flutter_foreground_worker.channel.AppMethodChannel;
import com.ostafen.flutter_foreground_worker.channel.ServiceMethodChannel;
import com.ostafen.flutter_foreground_worker.options.ServiceOptions;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;

public class ForegroundServiceManager {
    private final FlutterActivity flutterActivity;
    private final Context appContext;

    public ForegroundServiceManager(FlutterPlugin.FlutterPluginBinding flutterPluginBinding, FlutterActivity activity) {
        appContext = flutterPluginBinding.getApplicationContext();
        flutterActivity = activity;
        createAppMethodChannel(flutterPluginBinding.getBinaryMessenger());
        setup();
    }

    private void createAppMethodChannel(BinaryMessenger messenger) {
        AppMethodChannel channel = new AppMethodChannel(messenger);

        channel.onStartForegroundService((optionsMap) -> {
            ServiceOptions options = ServiceOptions.fromMap(optionsMap);
            ServiceOptions.setInstance(options);
            startForegroundService();
        });

        channel.onServiceIsRunning(() -> ForegroundService.isRunning);

        channel.onServiceSendMessage(ServiceMethodChannel::sendMessage);

        channel.onStopForegroundService(this::stopForegroundService);

        AppMethodChannel.setInstance(channel);
    }

    private void setup() {
        // TODO: put inside ForegroundService onDestroy
        flutterActivity.getLifecycle().addObserver((new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                AppMethodChannel.setInstance(null);
            }
        }));
    }

    private void startForegroundService() {
        Intent serviceIntent = new Intent(flutterActivity, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flutterActivity.startForegroundService(serviceIntent);
        } else {
            flutterActivity.startService(serviceIntent);
        }

        Log.d("DART/NATIVE", "binding service");
    }

    private void stopForegroundService() {
        Intent intent = new Intent(flutterActivity, ForegroundService.class);
        flutterActivity.stopService(intent);
    }
}
