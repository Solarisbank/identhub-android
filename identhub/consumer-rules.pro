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