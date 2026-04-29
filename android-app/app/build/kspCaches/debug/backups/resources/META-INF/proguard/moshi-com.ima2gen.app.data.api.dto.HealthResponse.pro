-keepnames class com.ima2gen.app.data.api.dto.HealthResponse
-if class com.ima2gen.app.data.api.dto.HealthResponse
-keep class com.ima2gen.app.data.api.dto.HealthResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.HealthResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.HealthResponse {
    public synthetic <init>(boolean,java.lang.String,java.lang.String,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Long,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
