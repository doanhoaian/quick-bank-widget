package vn.dihaver.tech.bank.widget.utils

import android.content.Context
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

class QrDecoder(private val context: Context) {

    fun decodeToString(uri: Uri, onResult: (String?) -> Unit) {
        try {
            val bitmap = BitmapUtils.getBitmapFromPath(context, uri.toString()) ?: throw Exception("Bitmap is null")
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
        }
    }
}