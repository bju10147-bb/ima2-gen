-keepnames class com.ima2gen.app.data.api.dto.BillingResponse
-if class com.ima2gen.app.data.api.dto.BillingResponse
-keep class com.ima2gen.app.data.api.dto.BillingResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.ima2gen.app.data.api.dto.BillingResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.ima2gen.app.data.api.dto.BillingResponse {
    public synthetic <init>(boolean,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
