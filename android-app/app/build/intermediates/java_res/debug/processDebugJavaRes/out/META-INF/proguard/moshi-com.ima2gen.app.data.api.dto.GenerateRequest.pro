-keepnames class com.ima2gen.app.data.api.dto.GenerateRequest
-if class com.ima2gen.app.data.api.dto.GenerateRequest
-keep class com.ima2gen.app.data.api.dto.GenerateRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.GenerateRequest
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.GenerateRequest {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,java.util.List,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,boolean,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
