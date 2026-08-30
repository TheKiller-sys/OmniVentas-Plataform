-keep class com.omniventas.app.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.google.gson.** { *; }
-keep class androidx.room.** { *; }
-keep class androidx.work.** { *; }

# ✅ NUEVO: Reglas para Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.resource.bitmap.** { *; }
-keep class com.bumptech.glide.load.resource.drawable.** { *; }

-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.bumptech.glide.**
