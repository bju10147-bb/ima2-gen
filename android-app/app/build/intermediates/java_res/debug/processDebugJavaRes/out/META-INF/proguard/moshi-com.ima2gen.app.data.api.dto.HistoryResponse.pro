-keepnames class com.ima2gen.app.data.api.dto.HistoryResponse
-if class com.ima2gen.app.data.api.dto.HistoryResponse
-keep class com.ima2gen.app.data.api.dto.HistoryResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.HistoryResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.HistoryResponse {
    public synthetic <init>(java.util.List,java.util.List,java.util.List,int,com.ima2gen.app.data.api.dto.HistoryCursor,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
