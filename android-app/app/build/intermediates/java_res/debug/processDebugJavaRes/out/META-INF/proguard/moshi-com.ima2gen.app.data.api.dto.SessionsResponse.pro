-keepnames class com.ima2gen.app.data.api.dto.SessionsResponse
-if class com.ima2gen.app.data.api.dto.SessionsResponse
-keep class com.ima2gen.app.data.api.dto.SessionsResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.SessionsResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.SessionsResponse {
    public synthetic <init>(java.util.List,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
