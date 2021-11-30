import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

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

  static Future<void> startForegroundService(Function mainFunction) async {
    var callbackHandle = PluginUtilities.getCallbackHandle(mainFunction)!.toRawHandle();
    await _channel.invokeMethod("startForegroundService", callbackHandle);
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
  late MethodChannel channel;

  late Stream<Map<String, dynamic>> messageStream;

  void _setMethodChannel(MethodChannel channel) {
    this.channel = channel;
  }

  void _setMessageStream(Stream<Map<String, dynamic>> stream) {
    messageStream = stream;
  }

  void shutdown() {
    channel.invokeMethod("stop");
  }

  Future<void> send(Map<String, dynamic> message) async {
    await channel.invokeMethod("send", jsonEncode(message));
  }

  void run();
}
