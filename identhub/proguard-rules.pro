# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

#
# This ProGuard configuration file illustrates how to process a program
# library, such that it remains usable as a library.
# Usage:
#     java -jar proguard.jar @library.pro
#

# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-printmapping out.map
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all annotations.

-keepattributes *Annotation*

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
}

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class de.solarisbank.identhub.main.AndroidJSInterface {
   public *;
}