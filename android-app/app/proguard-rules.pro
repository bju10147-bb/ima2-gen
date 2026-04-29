# Add project specific ProGuard rules here.
# Keep Moshi adapters
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
-keep class com.ima2gen.app.data.api.dto.** { *; }
-keep class com.ima2gen.app.domain.model.** { *; }

# Keep Retrofit interfaces
-keep,allowobfuscation interface com.ima2gen.app.data.api.Ima2GenApi

# OkHttp
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
