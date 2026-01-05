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

# If you keep the line number information, uncomment this to
# hide the original source file name.
# ===== LOG =====
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
# ===== GSON =====
-keep class com.CT060104.socialmedia.models.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# ===== CORE =====
-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
# ===== ANDROID ENTRY =====
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
# ===== STACKTRACE =====
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes !LocalVariableTable
# ===== UTILS (SELECTIVE) =====
-keep class com.CT060104.socialmedia.utils.TokenManager
# ===== ADAPTER (SAFE) =====
-keep class * extends androidx.recyclerview.widget.RecyclerView$Adapter
# ===== API =====
-keep interface com.CT060104.socialmedia.api.SocialMediaApi
# ===== OKHTTP OPTIONAL TLS PROVIDERS =====
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

