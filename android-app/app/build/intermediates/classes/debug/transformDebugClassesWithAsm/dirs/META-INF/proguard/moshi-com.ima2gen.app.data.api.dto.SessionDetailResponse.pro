-keepnames class com.ima2gen.app.data.api.dto.SessionDetailResponse
-if class com.ima2gen.app.data.api.dto.SessionDetailResponse
-keep class com.ima2gen.app.data.api.dto.SessionDetailResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
