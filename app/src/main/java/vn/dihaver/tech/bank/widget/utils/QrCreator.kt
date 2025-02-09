package vn.dihaver.tech.bank.widget.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrErrorCorrectionLevel
import com.github.alexzhirkevich.customqrgenerator.style.Color
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.QrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBackground
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorShapes
import vn.dihaver.tech.bank.widget.data.model.BodyShape
import vn.dihaver.tech.bank.widget.data.model.CustomQrEntity
import vn.dihaver.tech.bank.widget.data.model.EyeBallShape
import vn.dihaver.tech.bank.widget.data.model.EyeFrameShape

class QrCreator(private val context: Context) {

    fun createDrawable(qrData: QrData, customQrEntity: CustomQrEntity): Drawable {
        val optionsQr = QrVectorOptions.Builder()
            .setPadding(0f)
            .setErrorCorrectionLevel(QrErrorCorrectionLevel.Low)
            .setBackground(QrVectorBackground(color = QrVectorColor.Solid(Color(0xFFFFFFFF))))
            .setColors(
                QrVectorColors(
                    dark = QrVectorColor.Solid(Color.parseColor(customQrEntity.qrBodyDarkColor)),
                    ball = QrVectorColor.Solid(Color.parseColor(customQrEntity.qrEyeBallColor)),
                    frame = QrVectorColor.Solid(Color.parseColor(customQrEntity.qrEyeFrameColor))
                )
            )
            .setShapes(
                QrVectorShapes(
                    darkPixel = customQrEntity.qrBodyDarkShape.toQrVectorPixelShape(),
                    ball = customQrEntity.qrEyeBallShape.toQrVectorBallShape(),
                    frame = customQrEntity.qrEyeFrameShape.toQrVectorFrameShape()
                )
            )
            .build()

        return QrCodeDrawable(qrData, optionsQr)
    }

    // Chuyển đổi enum thành đối tượng của thư viện
    private fun BodyShape.toQrVectorPixelShape(): QrVectorPixelShape = when (this) {
        BodyShape.Circle -> QrVectorPixelShape.Circle()
        BodyShape.Default -> QrVectorPixelShape.Default
        BodyShape.Rect -> QrVectorPixelShape.Rect()
        BodyShape.Rhombus -> QrVectorPixelShape.Rhombus()
        BodyShape.RoundCorners -> QrVectorPixelShape.RoundCorners(.5f)
        BodyShape.RoundCornersHorizontal -> QrVectorPixelShape.RoundCornersHorizontal()
        BodyShape.RoundCornersVertical -> QrVectorPixelShape.RoundCornersVertical()
        BodyShape.Star -> QrVectorPixelShape.Star
    }

    private fun EyeBallShape.toQrVectorBallShape(): QrVectorBallShape = when (this) {
        EyeBallShape.Circle -> QrVectorBallShape.Circle()
        EyeBallShape.Default -> QrVectorBallShape.Default
        EyeBallShape.Rect -> QrVectorBallShape.Rect()
        EyeBallShape.Rhombus -> QrVectorBallShape.Rhombus()
        EyeBallShape.RoundCorners -> QrVectorBallShape.RoundCorners(.25f)
    }

    private fun EyeFrameShape.toQrVectorFrameShape(): QrVectorFrameShape = when (this) {
        EyeFrameShape.Circle -> QrVectorFrameShape.Circle()
        EyeFrameShape.Default -> QrVectorFrameShape.Default
        EyeFrameShape.Rect -> QrVectorFrameShape.Rect()
        EyeFrameShape.RoundCorners -> QrVectorFrameShape.RoundCorners(.25f)
    }
}