-keepnames class com.ima2gen.app.data.api.OpenAiImageResponse
-if class com.ima2gen.app.data.api.OpenAiImageResponse
-keep class com.ima2gen.app.data.api.OpenAiImageResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
