-keepnames class com.ima2gen.app.data.api.dto.ProvidersResponse
-if class com.ima2gen.app.data.api.dto.ProvidersResponse
-keep class com.ima2gen.app.data.api.dto.ProvidersResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.ProvidersResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.ProvidersResponse {
    public synthetic <init>(boolean,boolean,boolean,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
