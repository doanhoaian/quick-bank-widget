package vn.dihaver.tech.bank.widget.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetEntity(
    @SerializedName("id")
    val id: String,

    /** Trường Custom Widget */
    @SerializedName("widget_style")
    var widgetStyle: WidgetStyle,

    /** Trường QR */
    @SerializedName("qr_entity")
    val qrEntity: QrEntity
): Parcelable

enum class WidgetStyle {
    SIMPLE_2X2,
    ADVANCED_4X2
}
