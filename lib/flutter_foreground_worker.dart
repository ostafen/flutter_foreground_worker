import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:flutter_foreground_worker/notification_options.dart';

class FlutterForegroundWorker {
  static final _streamController = StreamController<Map<String, dynamic>>.broadcast();
  static ForegroundWorker? currentWorker;
  static final MethodChannel _channel = const MethodChannel('main')..
  setMethodCallHandler((call) async {
    switch(call.method) {
      case 'onServiceMessage':
        _streamController.sink.add(jsonDecode(call.arguments));
    }
  });

  static Stream<Map<String, dynamic>> get messageStream {
    return _streamController.stream;
  }

  static Future<void> sendMessage(Map<String, dynamic> msg) async {
    await _channel.invokeMethod("sendMessage",  jsonEncode(msg));
  }

  static Future<void> startForegroundService({
        NotificationOptions? notificationOptions,
        required Function entryPoint
      }) async {
    var entryPointHandle = PluginUtilities.getCallbackHandle(entryPoint)!.toRawHandle();
    var options = {};
    options['notification'] = notificationOptions != null ? notificationOptions.toJson() : {};
    options['callbackHandle'] = entryPointHandle;
    await _channel.invokeMethod("startForegroundService", options);
  }

  static Future<void> stopForegroundService() async {
    await _channel.invokeMethod("stopForegroundService");
  }

  static Future<bool> isRunning() async {
    return await _channel.invokeMethod("isRunning");
  }

  static void load(ForegroundWorker worker) async {
    // each call to sendMessage before ensureInitialized is executed will not be
    // delivered. This happens for example by putting sendMessage on the initState method of the main app component
    // a fix could be to put message in a queue and "ok" method call before delivering the first message from inside plugin
    WidgetsFlutterBinding.ensureInitialized();

    var workerStreamController = StreamController<Map<String, dynamic>>.broadcast();

    // Create methodChannel
    const MethodChannel channel = MethodChannel("service");
    worker._setMethodChannel(channel);
    worker._setMessageStream(workerStreamController.stream);

    channel.setMethodCallHandler(
            (call) async {
          final args = call.arguments;

          switch (call.method) {
            case 'onMessage':
              var msg = jsonDecode(args);
              //worker.onMessage(msg);
              workerStreamController.sink.add(msg);
          }
        }
    );

    worker.run();
  }
}

abstract class ForegroundWorker {
  late MethodChannel _channel;
  late Stream<Map<String, dynamic>> messageStream;

  void _setMethodChannel(MethodChannel channel) {
    _channel = channel;
  }

  void _setMessageStream(Stream<Map<String, dynamic>> stream) {
    messageStream = stream;
  }

  void shutdown() {
    _channel.invokeMethod("stop");
  }

  Future<void> send(Map<String, dynamic> message) async {
    await _channel.invokeMethod("send", jsonEncode(message));
  }

  void run();
}
