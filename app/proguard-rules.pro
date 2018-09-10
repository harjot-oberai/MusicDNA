# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:/Users/$USER/AppData/Local/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   https://developer.android.com/studio/build/shrink-code.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-keep class android.support.v8.renderscript.** { *; }
-keep class okhttp3.** { *; }

-keeppackagenames org.jsoup.nodes
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-dontwarn retrofit2.**
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions