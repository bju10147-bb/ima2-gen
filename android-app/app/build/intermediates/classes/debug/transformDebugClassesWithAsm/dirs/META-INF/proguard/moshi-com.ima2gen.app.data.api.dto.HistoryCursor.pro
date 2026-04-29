-keepnames class com.ima2gen.app.data.api.dto.HistoryCursor
-if class com.ima2gen.app.data.api.dto.HistoryCursor
-keep class com.ima2gen.app.data.api.dto.HistoryCursorJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.HistoryCursor
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.HistoryCursor {
    public synthetic <init>(java.lang.Long,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
