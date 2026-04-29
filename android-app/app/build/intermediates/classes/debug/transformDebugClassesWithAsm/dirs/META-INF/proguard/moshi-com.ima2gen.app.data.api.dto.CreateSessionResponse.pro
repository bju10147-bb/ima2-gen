-keepnames class com.ima2gen.app.data.api.dto.CreateSessionResponse
-if class com.ima2gen.app.data.api.dto.CreateSessionResponse
-keep class com.ima2gen.app.data.api.dto.CreateSessionResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
