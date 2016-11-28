#uncomment if you don't want to obfuscate
#-dontobfuscate

#PANDROID
-keep public class **.PandroidConfigMapper{ *; }

-keep class * implements java.io.Serializable { *; }

-keep class * implements android.os.Parcelable { *; }

-keep @com.leroymerlin.pandroid.security.KeepClass class * {
    *;
}
-keepclassmembers class * extends com.leroymerlin.pandroid.event.AbstractReceiver{
    void onReceive(...);
}

-keep class **ReceiversProvider{
    public <init>(...);
}


-keepclassmembers class * implements com.leroymerlin.pandroid.dagger.BaseComponent{
    void inject(...);
}

-keepclassmembers class * extends com.leroymerlin.pandroid.net.mock.ServiceMock{
    public <init>(...);
}

-keepnames class * {
      @com.pandroid.annotations.EventReceiver <methods>;
}
-keep class **ReceiversProvider{
      public <init>(...);
}
-dontwarn com.leroymerlin.pandroid.**

#ICEPICK
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}

#BUTTERKNIFE
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#RETROFIT
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#GLIDE
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#OKHTTP
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-dontwarn okio.*

#CRASHLYTICS
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable,*Annotation*
