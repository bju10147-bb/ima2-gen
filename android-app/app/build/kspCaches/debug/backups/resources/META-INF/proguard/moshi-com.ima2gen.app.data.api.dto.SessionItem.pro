-keepnames class com.ima2gen.app.data.api.dto.SessionItem
-if class com.ima2gen.app.data.api.dto.SessionItem
-keep class com.ima2gen.app.data.api.dto.SessionItemJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.SessionItem
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.SessionItem {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.Long,java.lang.Long,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
