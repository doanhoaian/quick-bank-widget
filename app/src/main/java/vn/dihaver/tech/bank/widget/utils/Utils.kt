package vn.dihaver.tech.bank.widget.utils

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.WriterException
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeEncoder

const val TAG = "Utils"

fun generateQrBitmap(data: String): Bitmap? {
    return try {
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400)
    } catch (e: WriterException) {
        Log.e(TAG, "Đã xảy ra lỗi khi tạo QR: " + e.message)
        null
    }
}

@Suppress("DEPRECATION")
fun decodeQrFromImage(uri: Uri, activity: AppCompatActivity, onResult: (String?) -> Unit) {
    try {
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)

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
        e.printStackTrace()
        onResult(null)
    }
}