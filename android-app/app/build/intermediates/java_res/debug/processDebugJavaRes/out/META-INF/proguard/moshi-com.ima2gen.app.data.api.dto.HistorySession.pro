-keepnames class com.ima2gen.app.data.api.dto.HistorySession
-if class com.ima2gen.app.data.api.dto.HistorySession
-keep class com.ima2gen.app.data.api.dto.HistorySessionJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.HistorySession
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.HistorySession {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.util.List,long,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
