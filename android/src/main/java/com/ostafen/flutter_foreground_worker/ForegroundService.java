package com.ostafen.flutter_foreground_worker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ostafen.flutter_foreground_worker.channel.AppMethodChannel;
import com.ostafen.flutter_foreground_worker.channel.ServiceMethodChannel;
import com.ostafen.flutter_foreground_worker.options.NotificationOptions;
import com.ostafen.flutter_foreground_worker.options.ServiceOptions;

import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.view.FlutterCallbackInformation;

public class ForegroundService extends Service {
    private static final int NOTIFICATION_ID = 101;

    public static boolean isRunning = false;

    private FlutterEngine engine;

    @Override
    public void onCreate()  {
        super.onCreate();

        ServiceOptions options = ServiceOptions.getInstance();
        long serviceCallbackHandle = options.getCallbackHandle();

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(options.getNotificationOptions());
        //}
        isRunning = true;
        ServiceMethodChannel serviceChannel = getForegroundServiceMethodChannel(serviceCallbackHandle);
        serviceChannel.onSendMessage(AppMethodChannel::sendMessage);
        serviceChannel.onStop(this::stopForegroundService);
        ServiceMethodChannel.setInstance(serviceChannel);

        //channel.setMethodCallHandler(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getAppName() {
        return getApplicationInfo().loadLabel(getPackageManager()).toString();
    }

    private String getChannelName(String name) {
        if(!name.isEmpty())
            return name;
        return getAppName();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    private void createNotificationChannelSdk26OrLater(NotificationOptions options, PendingIntent pendingIntent) {
        NotificationChannel channel = new NotificationChannel(
                options.getChannelId(),
                getChannelName(options.getChannelName()),
                options.getChannelImportance()
        );
        channel.setDescription(options.getChannelDescription());
        channel.enableVibration(options.getEnableVibration());

        if (!options.getPlaySound())
            channel.setSound(null, null);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(this, options.getChannelId());
        builder.setOngoing(true)
                .setShowWhen(options.getShowWhen())
                .setSmallIcon(getSmallIconId())
                .setContentIntent(pendingIntent)
                .setContentTitle(options.getNotificationContentTitle())
                .setContentText(options.getNotificationContentText())
                .setVisibility(options.getVisibility());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @SuppressLint("WrongConstant")
    private void createNotificationChannelOlderSdk(NotificationOptions options, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, options.getChannelId());
        builder.setOngoing(true)
                .setShowWhen(options.getShowWhen())
                .setSmallIcon(getSmallIconId())
                .setContentIntent(pendingIntent)
                .setContentTitle(options.getNotificationContentText())
                .setContentText(options.getNotificationContentText())
                .setVisibility(options.getVisibility());

        if (!options.getEnableVibration()) {
            builder.setVibrate(new long[]{0L});
        }

        if (!options.getPlaySound()) {
            builder.setSound(null);
        }

        builder.setPriority(options.getPriority());
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @SuppressLint("WrongConstant")
    private void createNotificationChannel(NotificationOptions options) {
        Intent launchIntentForPackage = getApplicationContext()
                .getPackageManager()
                .getLaunchIntentForPackage(getApplicationContext().getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntentForPackage, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelSdk26OrLater(options, pendingIntent);
        } else {
            createNotificationChannelOlderSdk(options, pendingIntent);
        }
    }

    public ServiceMethodChannel getForegroundServiceMethodChannel(long callbackHandle) {
        Context ctx = getApplicationContext();
        engine = new FlutterEngine(ctx.getApplicationContext());
        FlutterLoader loader = FlutterInjector.instance().flutterLoader();
        loader.startInitialization(this);
        loader.ensureInitializationComplete(this, null);

        FlutterCallbackInformation callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(callbackHandle);
        String appBundlePath = loader.findAppBundlePath();

        Log.d("NATIVE", appBundlePath);

        DartExecutor.DartCallback dartCallback = new DartExecutor.DartCallback(getAssets(), appBundlePath, callbackInfo);
        engine.getDartExecutor().executeDartCallback(dartCallback);
        return new ServiceMethodChannel(engine.getDartExecutor().getBinaryMessenger());
    }

    private int getSmallIconId() {
        return getApplicationContext().getResources().getIdentifier(
                "ic_launcher",
                "mipmap",
                getApplicationContext().getPackageName()
        );
    }

    public void stopForegroundService() {
        stopForeground(true);
        stopSelf();

        if(engine != null) {
            engine.destroy();
            engine = null;
        }

        ServiceMethodChannel.setInstance(null);
        isRunning = false;
    }
}
