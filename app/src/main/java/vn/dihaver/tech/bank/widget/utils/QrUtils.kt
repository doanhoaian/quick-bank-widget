package vn.dihaver.tech.bank.widget.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.WriterException
import com.google.zxing.common.HybridBinarizer

object QrUtils {

    private const val QR_SIZE = 400
    private const val LOGO_SIZE_RATIO = 6
    private const val LOGO_BORDER_SIZE = 2 // Độ dày viền logo

    /**
     * Tạo QR bitmap
     * Hỗ trợ tạo QR full size
     * Hỗ trợ sửa nền trước & sau
     */
    fun generateQrBitmap(data: String, colorForeground: Int, colorBackground: Int): Bitmap? {
        return try {
            val bitMatrix =
                MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE)

            var left = QR_SIZE
            var top = QR_SIZE
            var right = 0
            var bottom = 0

            for (x in 0 until QR_SIZE) {
                for (y in 0 until QR_SIZE) {
                    if (bitMatrix[x, y]) {
                        if (x < left) left = x
                        if (x > right) right = x
                        if (y < top) top = y
                        if (y > bottom) bottom = y
                    }
                }
            }

            val contentWidth = right - left + 1
            val contentHeight = bottom - top + 1

            val scaleX = QR_SIZE.toFloat() / contentWidth
            val scaleY = QR_SIZE.toFloat() / contentHeight
            val scale = minOf(scaleX, scaleY)

            Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.ARGB_8888).apply {
                for (x in 0 until QR_SIZE) {
                    for (y in 0 until QR_SIZE) {
                        val srcX = ((x / scale) + left).toInt()
                        val srcY = ((y / scale) + top).toInt()
                        val color =
                            if (srcX in 0 until QR_SIZE && srcY in 0 until QR_SIZE && bitMatrix[srcX, srcY]) {
                                colorForeground
                            } else {
                                colorBackground
                            }
                        setPixel(x, y, color)
                    }
                }
            }
        } catch (e: WriterException) {
            Log.e(TAG, "Error generating full-size QR: ${e.message}")
            null
        }
    }

    /**
     * Thêm logo (hình tròn, có viền trắng) vào giữa mã QR với tùy chọn padding cho logo.
     */
    fun addLogoToQr(qrBitmap: Bitmap, logo: Bitmap?, padding: Int = 0): Bitmap {
        if (logo != null) {
            val croppedLogo = cropToSquare(logo)
            val logoSize = QR_SIZE / LOGO_SIZE_RATIO
            val scaledLogo = Bitmap.createScaledBitmap(croppedLogo, logoSize, logoSize, true)
            val circularLogoWithBorder = createCircularLogoWithBorder(scaledLogo, padding)

            val canvas = Canvas(qrBitmap)
            val centerX = (QR_SIZE - circularLogoWithBorder.width) / 2
            val centerY = (QR_SIZE - circularLogoWithBorder.height) / 2
            canvas.drawBitmap(circularLogoWithBorder, centerX.toFloat(), centerY.toFloat(), null)
        }
        return qrBitmap
    }

    /**
     * Tạo logo hình tròn với viền trắng và padding.
     */
    private fun createCircularLogoWithBorder(logo: Bitmap, padding: Int): Bitmap {
        val diameter = logo.width + 2 * LOGO_BORDER_SIZE + 2 * padding // Thêm padding vào kích thước

        val output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.WHITE
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint)

        val circularLogo = cropToCircle(logo)
        val rect = Rect(
            LOGO_BORDER_SIZE + padding,
            LOGO_BORDER_SIZE + padding,
            diameter - LOGO_BORDER_SIZE - padding,
            diameter - LOGO_BORDER_SIZE - padding
        )
        canvas.drawBitmap(circularLogo, null, rect, null)

        return output
    }

    /**
     * Cắt ảnh thành hình vuông, giữ phần trung tâm.
     */
    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val xOffset = (bitmap.width - size) / 2
        val yOffset = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    /**
     * Tạo logo hình tròn với viền trắng.
     */
    private fun createCircularLogoWithBorder(logo: Bitmap): Bitmap {
        val diameter = logo.width + 2 * LOGO_BORDER_SIZE

        val output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.WHITE
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint)

        val circularLogo = cropToCircle(logo)
        val rect = Rect(
            LOGO_BORDER_SIZE,
            LOGO_BORDER_SIZE,
            diameter - LOGO_BORDER_SIZE,
            diameter - LOGO_BORDER_SIZE
        )
        canvas.drawBitmap(circularLogo, null, rect, null)

        return output
    }

    /**
     * Cắt ảnh thành hình tròn.
     */
    private fun cropToCircle(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val rect = Rect(0, 0, size, size)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    /**
     * Decode QR từ URI ảnh.
     */
    fun decodeQrFromImage(uri: Uri, activity: AppCompatActivity, onResult: (String?) -> Unit) {
        try {
            val bitmap = uriToBitmap(uri, activity) ?: throw Exception("Bitmap is null")
            val source = RGBLuminanceSource(
                bitmap.width,
                bitmap.height,
                IntArray(bitmap.width * bitmap.height).also { pixels ->
                    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                }
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val hints = mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE))
            val result = MultiFormatReader().apply { setHints(hints) }.decode(binaryBitmap)

            onResult(result.text)
        } catch (e: Exception) {
            onResult(null)
            Log.e(TAG, "Error decoding QR: ${e.message}")
        }
    }

    /**
     * Chuyển URI thành Bitmap.
     */
    private fun uriToBitmap(uri: Uri, activity: AppCompatActivity): Bitmap? {
        return try {
            activity.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to Bitmap: ${e.message}")
            null
        }
    }
}