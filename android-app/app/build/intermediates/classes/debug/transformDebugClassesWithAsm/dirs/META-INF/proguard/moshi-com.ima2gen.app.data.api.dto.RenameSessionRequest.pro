-keepnames class com.ima2gen.app.data.api.dto.RenameSessionRequest
-if class com.ima2gen.app.data.api.dto.RenameSessionRequest
-keep class com.ima2gen.app.data.api.dto.RenameSessionRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
