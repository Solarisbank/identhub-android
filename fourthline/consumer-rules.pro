-keepnames class de.solarisbank.sdk.fourthline.FourthlineModule
-keep class de.solarisbank.sdk.fourthline.data.dto.** { *; }

# Fourthline
-keep class com.fourthline.vision.internal.** { *; }
-keepclassmembers @kotlin.Metadata class com.fourthline.vision.internal.** {
  <methods>;
}
-keep class kotlin.coroutines.Continuation {
  public protected *;
}