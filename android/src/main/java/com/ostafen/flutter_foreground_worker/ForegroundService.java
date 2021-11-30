package com.ostafen.flutter_foreground_worker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ostafen.flutter_foreground_worker.channel.AppMethodChannel;
import com.ostafen.flutter_foreground_worker.channel.ServiceMethodChannel;

import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.view.FlutterCallbackInformation;

public class ForegroundService extends Service {
    private static final String CHANNEL_ID = "APP_SERVICE_CHANNEL_NAME";
    private static final int NOTIFICATION_ID = 101;

    public static boolean isRunning = false;

    private FlutterEngine engine;

    private ServicePreferences getServicePreferences() {
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences(ServicePreferences.PREFERENCES_KEY, Context.MODE_PRIVATE);
        return new ServicePreferences(preferences);
    }

    @Override
    public void onCreate()  {
        super.onCreate();

        ServicePreferences preferences = getServicePreferences();
        long serviceCallbackHandle = preferences.getServiceCallbackHandle();

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(preferences);
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

    private void createNotificationChannel(ServicePreferences preferences) {
        Intent launchIntentForPackage = getApplicationContext()
                .getPackageManager()
                .getLaunchIntentForPackage(getApplicationContext().getPackageName());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntentForPackage, PendingIntent.FLAG_IMMUTABLE);

        String channelName = preferences.getChannelName();
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);

        if(!preferences.getPlaySound())
            notificationChannel.setSound(null, null);

        notificationChannel.setDescription(preferences.getChannelDescription());
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        @SuppressLint("WrongConstant")
        Notification notification = notificationBuilder
                .setOngoing(true)
                .setShowWhen(preferences.getShowWhen())
                .setSmallIcon(getSmallIconId())
                .setContentTitle(preferences.getNotificationContentTitle())
                .setContentText(preferences.getNotificationContentText())
                .setContentIntent(pendingIntent)
                .setVisibility(preferences.getVisibility())
                .build();

        startForeground(NOTIFICATION_ID, notification);
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
