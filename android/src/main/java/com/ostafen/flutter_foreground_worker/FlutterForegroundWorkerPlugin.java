package com.ostafen.flutter_foreground_worker;

import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/** HelloPlugin */
public class FlutterForegroundWorkerPlugin implements FlutterPlugin, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private ForegroundServiceManager serviceManager;
  private FlutterPluginBinding flutterPluginBinding;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.flutterPluginBinding = flutterPluginBinding;
    Log.d("NATIVE", "on attached to engine");
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    serviceManager = null;
    flutterPluginBinding = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    FlutterActivity activity = (FlutterActivity) binding.getActivity();
    serviceManager = new ForegroundServiceManager(flutterPluginBinding, activity);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    Log.d("NATIVE", "on detached plugin");
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) { }

  @Override
  public void onDetachedFromActivity() {
    serviceManager = null;
  }
}

