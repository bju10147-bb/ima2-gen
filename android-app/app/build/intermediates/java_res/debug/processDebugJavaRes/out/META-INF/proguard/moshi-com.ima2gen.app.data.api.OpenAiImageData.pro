-keepnames class com.ima2gen.app.data.api.OpenAiImageData
-if class com.ima2gen.app.data.api.OpenAiImageData
-keep class com.ima2gen.app.data.api.OpenAiImageDataJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.OpenAiImageData
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.OpenAiImageData {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
