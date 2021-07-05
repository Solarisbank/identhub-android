# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keep public class de.solarisbank.identhub.*.** { *;}

# Remove log calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
}
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** e(...);
}

-keep public interface de.solarisbank.identhub.data.initialization.InitializeIdentificationApi

-keep public class * {
    public protected *;
}

-keep class *.com.google.inject.** { *; }
-keep class *.org.apache.http.** { *; }
-keep class *.org.apache.james.mime4j.** { *; }
-keep class *.com.google.appengine.** { *; }

-keep class *.retrofit.** { *; }
-keep class okhttp.*

-keep class *.javax.inject.** { *; }
-keep class *.javax.xml.stream.** { *; }
-keepattributes *Annotation*
-keepattributes Signature