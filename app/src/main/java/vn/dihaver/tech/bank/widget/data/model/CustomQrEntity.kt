package vn.dihaver.tech.bank.widget.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import vn.dihaver.tech.bank.widget.utils.BitmapUtils

@Parcelize
data class CustomQrEntity(
    @SerializedName("qr_eye_ball_color")
    var qrEyeBallColor: String = "#FF000000",

    @SerializedName("qr_eye_frame_color")
    var qrEyeFrameColor: String = "#FF000000",

    @SerializedName("qr_body_dark_color")
    var qrBodyDarkColor: String = "#FF000000",

    @SerializedName("qr_eye_ball_shape")
    var qrEyeBallShape: EyeBallShape = EyeBallShape.Default,

    @SerializedName("qr_eye_frame_shape")
    var qrEyeFrameShape: EyeFrameShape = EyeFrameShape.Default,

    @SerializedName("qr_body_dark_shape")
    var qrBodyDarkShape: BodyShape = BodyShape.Default,

    @SerializedName("qr_logo_path")
    var qrLogoPath: String = BitmapUtils.convertNameToPath("bg_not_have", BitmapUtils.PathType.RES),
) : Parcelable

enum class BodyShape {
    Circle,
    Default,
    Rect,
    Rhombus,
    RoundCorners,
    RoundCornersHorizontal,
    RoundCornersVertical,
    Star
}

enum class EyeBallShape {
    Circle,
    Default,
    Rect,
    Rhombus,
    RoundCorners
}

enum class EyeFrameShape {
    Circle,
    Default,
    Rect,
    RoundCorners
}

