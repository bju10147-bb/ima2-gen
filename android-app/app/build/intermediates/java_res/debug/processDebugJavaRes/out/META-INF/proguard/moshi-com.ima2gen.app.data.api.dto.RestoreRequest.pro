-keepnames class com.ima2gen.app.data.api.dto.RestoreRequest
-if class com.ima2gen.app.data.api.dto.RestoreRequest
-keep class com.ima2gen.app.data.api.dto.RestoreRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
