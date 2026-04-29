-keepnames class com.ima2gen.app.data.api.dto.CreateSessionRequest
-if class com.ima2gen.app.data.api.dto.CreateSessionRequest
-keep class com.ima2gen.app.data.api.dto.CreateSessionRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.CreateSessionRequest
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.CreateSessionRequest {
    public synthetic <init>(java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
