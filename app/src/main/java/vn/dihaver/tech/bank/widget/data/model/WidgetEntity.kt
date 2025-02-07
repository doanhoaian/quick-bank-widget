package vn.dihaver.tech.bank.widget.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetEntity(
    @SerializedName("id")
    val id: String,

    @SerializedName("bank_logo")
    val bankLogo: String,
    @SerializedName("bank_name")
    val bankName: String,
    @SerializedName("bank_number")
    val bankNumber: String,

    @SerializedName("cus_wg_style")
    val cusWgStyle: Enum<WidgetStyle>,
    @SerializedName("cus_is_wg_stroke")
    val cusIsWgStroke: Boolean,
    @SerializedName("cus_wg_stroke_color")
    val cusWgStrokeColor: String,

    @SerializedName("qr_color")
    val qrColor: String,
    @SerializedName("qr_content")
    val qrContent: String,
    @SerializedName("qr_icon")
    val qrIcon: String
): Parcelable

enum class WidgetStyle {
    BASIC_2X2,
    LOGO_2X2,
    FULL_4X2
}
