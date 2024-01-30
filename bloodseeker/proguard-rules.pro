# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# classes to external purposes
-keep class com.am1goo.bloodseeker.Report {public *;}
-keep class com.am1goo.bloodseeker.Async {public *;}
-keep class com.am1goo.bloodseeker.AsyncReport {public *;}
-keep class com.am1goo.bloodseeker.update.LocalUpdateConfig {public *;}
-keep class com.am1goo.bloodseeker.update.RemoteUpdateConfig {public *;}
-keep class com.am1goo.bloodseeker.update.RemoteUpdateFile {public *;}
-keep class com.am1goo.bloodseeker.update.RemoteUpdateFiles {public *;}
-keep class com.am1goo.bloodseeker.trails.* {public *;}
-keep class com.am1goo.bloodseeker.android.trails.* {public *;}
-keep class com.am1goo.bloodseeker.android.AndroidBloodseeker {public *;}

-dontwarn org.slf4j.event.Level
-dontwarn org.slf4j.event.LoggingEvent