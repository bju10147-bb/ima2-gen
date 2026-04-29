-keepnames class com.ima2gen.app.data.api.dto.EditResponse
-if class com.ima2gen.app.data.api.dto.EditResponse
-keep class com.ima2gen.app.data.api.dto.EditResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.EditResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.EditResponse {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.util.Map,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
