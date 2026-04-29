-keepnames class com.ima2gen.app.data.api.dto.FavoriteResponse
-if class com.ima2gen.app.data.api.dto.FavoriteResponse
-keep class com.ima2gen.app.data.api.dto.FavoriteResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
