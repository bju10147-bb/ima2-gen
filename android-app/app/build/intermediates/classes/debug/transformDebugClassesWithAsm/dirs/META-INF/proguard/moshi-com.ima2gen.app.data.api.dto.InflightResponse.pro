-keepnames class com.ima2gen.app.data.api.dto.InflightResponse
-if class com.ima2gen.app.data.api.dto.InflightResponse
-keep class com.ima2gen.app.data.api.dto.InflightResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.InflightResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.InflightResponse {
    public synthetic <init>(java.util.List,java.util.List,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
