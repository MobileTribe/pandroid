#uncomment if you don't want to obfuscate
#-dontobfuscate

#PANDROID
-keep public class com.leroymerlin.pandroid.PandroidMapperImpl{ *; }

-keepclassmembers class * implements java.io.Serializable { *; }

-keepclassmembers class * implements android.os.Parcelable { *; }

-keepclassmembers class * extends com.leroymerlin.pandroid.net.mock.ServiceMock{
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

#BUTTERKNIFE
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }


#RETROFIT
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

#GLIDE
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#Dagger
-dontwarn com.google.errorprone.annotations.*

#OKHTTP
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-dontwarn okio.*

#CRASHLYTICS
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable,*Annotation*
-dontwarn com.crashlytics.**

#DATA BINDING
-keep class android.databinding.** { *; }
-dontwarn android.databinding.**

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
