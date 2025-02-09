package vn.dihaver.tech.bank.widget.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import vn.dihaver.tech.bank.widget.utils.BitmapUtils

@Parcelize
data class QrEntity(
    @SerializedName("id")
    val id: String,

    /** Trường Bank */
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

    /** Trường Account */
    @SerializedName("acc_number")
    val accNumber: String,
    @SerializedName("acc_name")
    var accHolderName: String,
    @SerializedName("acc_alias")
    var accAlias: String,

    /** Trường QR */
    @SerializedName("qr_content")
    val qrContent: String,

    /** Trường Custom */
    @SerializedName("custom_qr")
    var cusQrEntity: CustomQrEntity = CustomQrEntity(),
    @SerializedName("cus_theme_path")
    var cusThemePath: String = BitmapUtils.convertNameToPath("bg_not_have", BitmapUtils.PathType.RES)
) : Parcelable
