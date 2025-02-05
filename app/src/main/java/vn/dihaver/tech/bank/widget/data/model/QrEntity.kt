package vn.dihaver.tech.bank.widget.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QrEntity(
    @SerializedName("id")
    val id: String,

    /** Trường Bank (Ngân hàng)
     */
    @SerializedName("bank_name")
    val bankName: String,
    @SerializedName("bank_bin")
    val bankBin: String,
    @SerializedName("bank_code")
    val bankCode: String,
    @SerializedName("bank_short_name")
    val bankShortName: String,
    @SerializedName("bank_icon_res")
    val bankIconRes: String,
    @SerializedName("bank_logo_res")
    val bankLogoRes: String,

    /** Trường Account (Tài khoản)
     */
    @SerializedName("acc_number")
    val accNumber: String,
    @SerializedName("acc_name")
    var accHolderName: String,
    @SerializedName("acc_alias")
    var accAlias: String,

    /** Trường QR (Quick response)
     */
    @SerializedName("qr_content")
    val qrContent: String,

    /** Trường Custom (Tuỳ chỉnh)
     */
    @SerializedName("cus_qr_color")
    var cusQrColor: String,
    @SerializedName("cus_qr_icon_path")
    var cusQrIconPath: String,
    @SerializedName("cus_theme_path")
    var cusThemePath: String
) : Parcelable
