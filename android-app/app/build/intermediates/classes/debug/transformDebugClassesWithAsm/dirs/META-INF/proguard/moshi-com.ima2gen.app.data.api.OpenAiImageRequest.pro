-keepnames class com.ima2gen.app.data.api.OpenAiImageRequest
-if class com.ima2gen.app.data.api.OpenAiImageRequest
-keep class com.ima2gen.app.data.api.OpenAiImageRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.OpenAiImageRequest
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.OpenAiImageRequest {
    public synthetic <init>(java.lang.String,java.lang.String,int,java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
