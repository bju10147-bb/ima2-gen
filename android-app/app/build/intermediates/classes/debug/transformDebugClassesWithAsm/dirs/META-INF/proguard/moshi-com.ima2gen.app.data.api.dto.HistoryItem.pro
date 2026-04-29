-keepnames class com.ima2gen.app.data.api.dto.HistoryItem
-if class com.ima2gen.app.data.api.dto.HistoryItem
-keep class com.ima2gen.app.data.api.dto.HistoryItemJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.HistoryItem
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.HistoryItem {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,long,java.lang.String,boolean,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
