-keepnames class com.ima2gen.app.data.api.dto.GeneratedImage
-if class com.ima2gen.app.data.api.dto.GeneratedImage
-keep class com.ima2gen.app.data.api.dto.GeneratedImageJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.GeneratedImage
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.GeneratedImage {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
