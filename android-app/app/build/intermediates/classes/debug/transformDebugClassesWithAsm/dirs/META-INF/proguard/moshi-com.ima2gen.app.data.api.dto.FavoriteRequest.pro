-keepnames class com.ima2gen.app.data.api.dto.FavoriteRequest
-if class com.ima2gen.app.data.api.dto.FavoriteRequest
-keep class com.ima2gen.app.data.api.dto.FavoriteRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
