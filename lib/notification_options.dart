/// Notification options for Android platform.
class NotificationOptions {
  static const String contentTextDefault = "Running";

  /// Constructs an instance of [AndroidNotificationOptions].
  const NotificationOptions({
    required this.channelId,
    required this.channelName,
    this.channelDescription,
    this.channelImportance = NotificationChannelImportance.importanceDefault,
    this.priority = NotificationPriority.priorityDefault,
    this.contentTitle,
    this.contentText = contentTextDefault,
    this.enableVibration = false,
    this.playSound = false,
    this.showWhen = false,
    this.isSticky = true,
    this.visibility = NotificationVisibility.visibilityPublic,
    this.iconData,
  });

  /// Unique ID of the notification channel.
  final String channelId;

  /// The name of the notification channel.
  /// This value is displayed to the user in the notification settings.
  final String channelName;

  /// The description of the notification channel.
  /// This value is displayed to the user in the notification settings.
  final String? channelDescription;

  /// The importance of the notification channel.
  /// See https://developer.android.com/training/notify-user/channels?hl=ko#importance
  /// The default is `NotificationChannelImportance.DEFAULT`.
  final NotificationChannelImportance channelImportance;

  final String? contentTitle;

  final String contentText;

  /// Priority of notifications for Android 7.1 and lower.
  /// The default is `NotificationPriority.DEFAULT`.
  final NotificationPriority priority;

  /// Whether to enable vibration when creating notifications.
  /// The default is `false`.
  final bool enableVibration;

  /// Whether to play sound when creating notifications.
  /// The default is `false`.
  final bool playSound;

  /// Whether to show the timestamp when the notification was created in the content view.
  /// The default is `false`.
  final bool showWhen;

  /// Whether or not the system will restart the service if the service is killed.
  /// The default is `true`.
  final bool isSticky;

  /// Control the level of detail displayed in notifications on the lock screen.
  /// The default is `NotificationVisibility.VISIBILITY_PUBLIC`.
  final NotificationVisibility visibility;

  /// The data of the icon to display in the notification.
  /// If the value is null, the app launcher icon is used.
  final NotificationIconData? iconData;

  /// Returns the data fields of [AndroidNotificationOptions] in JSON format.
  Map<String, dynamic> toJson() {
    return {
      'channelId': channelId,
      'channelName': channelName,
      'channelDescription': channelDescription,
      'channelImportance': channelImportance.rawValue,
      'priority': priority.rawValue,
      'contentTitle': contentTitle,
      'contentText': contentText,
      'enableVibration': enableVibration,
      'playSound': playSound,
      'showWhen': showWhen,
      'isSticky': isSticky,
      'visibility': visibility.rawValue,
      'iconData': iconData?.toJson(),
    };
  }
}

/// The level of detail displayed in notifications on the lock screen.
class NotificationVisibility {
  /// Constructs an instance of [NotificationVisibility].
  const NotificationVisibility(this.rawValue);

  /// Show this notification in its entirety on all lockscreens.
  static const NotificationVisibility visibilityPublic = NotificationVisibility(1);

  /// Do not reveal any part of this notification on a secure lockscreen.
  static const NotificationVisibility visibilitySecret = NotificationVisibility(-1);

  /// Show this notification on all lockscreens, but conceal sensitive or private information on secure lockscreens.
  static const NotificationVisibility visibilityPrivate = NotificationVisibility(0);

  /// The raw value of [NotificationVisibility].
  final int rawValue;
}

/// The resource type of the notification icon.
enum ResourceType {
  /// A resources in the drawable folder.
  /// The drawable folder is where all kinds of images are stored.
  drawable,

  /// A resources in the mipmap folder.
  /// The mipmap folder is usually where the launcher icon image is stored.
  mipmap,
}

/// The resource prefix of the notification icon.
enum ResourcePrefix {
  /// A resources with the `ic_` prefix.
  ic,

  /// A resources with the `img_` prefix.
  img,
}

/// Data for setting the notification icon.
class NotificationIconData {
  /// Constructs an instance of [NotificationIconData].
  const NotificationIconData({
    required this.resType,
    required this.resPrefix,
    required this.name,
  });

  /// The resource type of the notification icon.
  /// If the resource is in the drawable folder, set it to [ResourceType.drawable],
  /// if the resource is in the mipmap folder, set it to [ResourceType.mipmap].
  final ResourceType resType;

  /// The resource prefix of the notification icon.
  /// If the notification icon name is `ic_simple_notification`,
  /// set it to [ResourcePrefix.ic] and set [name] to `simple_notification`.
  final ResourcePrefix resPrefix;

  /// Notification icon name without prefix.
  final String name;

  /// Returns the data fields of [NotificationIconData] in JSON format.
  Map<String, String> toJson() {
    return {
      'resType': resType.toString().split('.').last,
      'resPrefix': resPrefix.toString().split('.').last,
      'name': name,
    };
  }
}

/// Priority of notifications for Android 7.1 and lower.
class NotificationPriority {
  /// Constructs an instance of [NotificationPriority].
  const NotificationPriority(this.rawValue);

  /// No sound and does not appear in the status bar.
  static const NotificationPriority priorityMin = NotificationPriority(-2);

  /// No sound.
  static const NotificationPriority priorityLow = NotificationPriority(-1);

  /// Makes a sound.
  static const NotificationPriority priorityDefault = NotificationPriority(0);

  /// Makes a sound and appears as a heads-up notification.
  static const NotificationPriority priorityHigh = NotificationPriority(1);

  /// Same as HIGH, but used when you want to notify notification immediately.
  static const NotificationPriority priorityMax = NotificationPriority(2);

  /// The raw value of [NotificationPriority].
  final int rawValue;
}

/// The importance of the notification channel.
/// See https://developer.android.com/training/notify-user/channels?hl=ko#importance
class NotificationChannelImportance {
  /// Constructs an instance of [NotificationChannelImportance].
  const NotificationChannelImportance(this.rawValue);

  /// A notification with no importance: does not show in the shade.
  static const NotificationChannelImportance importanceNone = NotificationChannelImportance(0);

  /// Min notification importance: only shows in the shade, below the fold.
  static const NotificationChannelImportance importanceMin = NotificationChannelImportance(1);

  /// Low notification importance: shows in the shade, and potentially in the status bar (see shouldHideSilentStatusBarIcons()), but is not audibly intrusive.
  static const NotificationChannelImportance importanceLow = NotificationChannelImportance(2);

  /// Default notification importance: shows everywhere, makes noise, but does not visually intrude.
  static const NotificationChannelImportance importanceDefault = NotificationChannelImportance(3);

  /// Higher notification importance: shows everywhere, makes noise and peeks. May use full screen intents.
  static const NotificationChannelImportance importanceHigh = NotificationChannelImportance(4);

  /// Max notification importance: same as HIGH, but generally not used.
  static const NotificationChannelImportance importanceMax = NotificationChannelImportance(5);

  /// The raw value of [NotificationChannelImportance].
  final int rawValue;
}