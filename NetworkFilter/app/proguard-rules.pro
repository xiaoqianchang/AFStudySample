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

-ignorewarnings
-dontoptimize
-optimizationpasses 5 # 指定代码的压缩级别
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify # 不预校验
-verbose # 是否记录日志

# android default
-keep public class * extends android.os.Binder
-keep public class * extends android.os.IInterface
-keep public class * extends android.appwidget.AppWidgetProvider
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.DialogFragment
-dontwarn java.lang.invoke.**
-keep class android.app.** { *; }
-keep class androidx.fragment.app.** { *; }

-keepclasseswithmembernames class * { # 保持 native 方法不被混淆
   native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

-keep class * implements android.os.Parcelable { # 保持Parcelable不被混淆
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable { # 保持Serializable不被混淆
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep class **.R$* {*;}

##---------------Begin: proguard configuration for Gson  ----------
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
##---------------End: proguard configuration for Gson  ----------

#### start proguard configurations for NetworkFilter ####
-keep class * extends com.abupdate.common.interfaces.NamedCallable
-keep class * extends com.abupdate.common.interfaces.NamedRunnable
-keep class com.chang.ns.android.data.** {*;}
-keep class com.chang.ns.android.engine.** {*;}
-keep class com.chang.ns.android.ui.** {*;}
-keep class com.chang.ns.android.utils.** {*;}
-keep class com.chang.ns.android.view.** {*;}
#### end proguard configurations for NetworkFilter ####