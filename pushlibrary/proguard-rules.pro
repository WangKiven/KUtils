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



# 小米推送 https://dev.mi.com/console/doc/detail?pId=41#_1_3
#-keep class com.xiaomi.mipush.sdk.DemoMessageReceiver {*;} #这里com.xiaomi.mipushdemo.DemoMessageRreceiver改成app中定义的完整类名
-dontwarn com.xiaomi.push.**

# 华为推送 https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/Preparations
#-ignorewarning
#-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
#-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

# oppo推送
-keep public class * extends android.app.Service
-keep class com.heytap.msp.** { *;}

# vivo推送
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
#-keep class   xxx.xxx.xxx.PushMessageReceiverImpl{*;}