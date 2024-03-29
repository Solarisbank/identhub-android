-keepnames class de.solarisbank.sdk.fourthline.FourthlineFlow
-keep class de.solarisbank.sdk.fourthline.data.dto.** { *; }

# Fourthline
-keep class com.fourthline.vision.internal.** { *; }
-keepclassmembers @kotlin.Metadata class com.fourthline.vision.internal.** {
  <methods>;
}
-keep class kotlin.coroutines.Continuation {
  public protected *;
}
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontobfuscate