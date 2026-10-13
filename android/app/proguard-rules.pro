# Proguard rules for Mildy by PALIMSON Group
# Release optimization and obfuscation

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Media3 ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Retrofit & Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.palimson.mildy.data.remote.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
