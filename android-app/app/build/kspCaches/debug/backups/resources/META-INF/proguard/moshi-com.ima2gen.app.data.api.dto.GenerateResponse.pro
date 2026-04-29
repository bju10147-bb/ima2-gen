-keepnames class com.ima2gen.app.data.api.dto.GenerateResponse
-if class com.ima2gen.app.data.api.dto.GenerateResponse
-keep class com.ima2gen.app.data.api.dto.GenerateResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.GenerateResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.GenerateResponse {
    public synthetic <init>(java.lang.String,java.util.List,java.lang.String,java.lang.String,java.lang.String,java.util.Map,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
