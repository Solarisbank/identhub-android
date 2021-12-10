-keep class com.fourthline.vision.internal.** { *; }

-keep class kotlin.* {
    public protected *;
}

-keepclassmembers @kotlin.Metadata class * {
    <methods>;
}