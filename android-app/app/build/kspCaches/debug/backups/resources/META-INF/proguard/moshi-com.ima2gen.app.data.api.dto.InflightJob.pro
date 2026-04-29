-keepnames class com.ima2gen.app.data.api.dto.InflightJob
-if class com.ima2gen.app.data.api.dto.InflightJob
-keep class com.ima2gen.app.data.api.dto.InflightJobJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.InflightJob
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.InflightJob {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.Long,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
