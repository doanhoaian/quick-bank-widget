package vn.dihaver.tech.bank.widget.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

const val TAG = "Utils"


/** Hỗ trợ hệ thống
 */
object SystemUtils {
    @Suppress("DEPRECATION")
    fun Activity.translucentSystemBars(isStatus: Boolean, isNavigation: Boolean) {
        if (isStatus) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }
        if (isNavigation) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
        }
    }

    fun Activity.hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Context.showKeyboard(view: View) {
        view.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun Context.copyToClipboard(text: String, isShowToast: Boolean = true) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        if (isShowToast) {
            Toast.makeText(this, "Đã sao chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}


/** Xử lí ảnh
 */
object ImageUtils {
    fun takeScreenshot(context: Context, view: View, bitmapBG: Bitmap?, isSave: Boolean) {
        var bitmap = getBitmapFromView(view)
        bitmapBG?.let {
            bitmap = mergeBitmapFitTop(it, bitmap)
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Screenshot_$timeStamp.jpg"
        if (isSave) {
            saveImageToPublicStorage(context, bitmap, fileName)
        } else {
            shareImage(context, bitmap, fileName)
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawARGB(0, 0, 0, 0)
        view.draw(canvas)
        return bitmap
    }

    private fun mergeBitmapFitTop(background: Bitmap, overlay: Bitmap): Bitmap {
        val resultBitmap = Bitmap.createBitmap(background.width, background.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val srcRect = Rect(0, 0, background.width, background.height)
        val destRect = Rect(0, 0, resultBitmap.width, resultBitmap.height)
        canvas.drawBitmap(background, srcRect, destRect, null)
        val scale = background.width.toFloat() / overlay.width.toFloat()
        val scaledOverlayHeight = (overlay.height * scale).toInt()
        val scaledOverlay = Bitmap.createScaledBitmap(overlay, background.width, scaledOverlayHeight, true)
        canvas.drawBitmap(scaledOverlay, 0f, 0f, null)
        return resultBitmap
    }

    private fun saveImageToPublicStorage(context: Context, bitmap: Bitmap, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream: OutputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }

    fun saveImageToAppStorage(context: Context, uri: Uri, quality: Int = 100): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileName = "${UUID.randomUUID()}.png"
        val appStorageDir = File(context.getExternalFilesDir(null), "user-image")
        if (!appStorageDir.exists()) {
            appStorageDir.mkdirs()
        }
        val file = File(appStorageDir, fileName)
        inputStream?.use { input ->
            val bitmap = BitmapFactory.decodeStream(input)
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
            }
        }
        return file.absolutePath
    }

    private fun shareImage(context: Context, bitmap: Bitmap, fileName: String) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, fileName)
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Chia sẻ ảnh qua"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Lỗi khi chia sẻ ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    fun Drawable.drawableQrToBitmap(bgColor: Int, size: Int = 512, drawableSize: Int = 452): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = bgColor
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
        val dx = ((size - drawableSize) / 2).toFloat()
        val dy = ((size - drawableSize) / 2).toFloat()
        canvas.save()
        canvas.translate(dx, dy)
        this.setBounds(0, 0, drawableSize, drawableSize)
        this.draw(canvas)
        canvas.restore()
        return bitmap
    }
}


/** Định dạng dữ liệu
 */
object FormatUtils {
    fun String.formatAccNumber(): String {
        if (this.length < 3) {
            return this
        }
        val result = StringBuilder()
        for (i in this.indices) {
            if (i > 0 && (i % 4 == 0) && i < this.length) {
                result.append(" ")
            }
            result.append(this[i])
        }
        return result.toString()
    }

    fun Long.formatMoneyDong(): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        val formattedString = numberFormat.format(this)
        return "${formattedString}đ"
    }

    fun String.removeDiacritics(): String {
        val diacriticsMap = mapOf(
            'à' to 'a', 'á' to 'a', 'ả' to 'a', 'ã' to 'a', 'ạ' to 'a',
            'ă' to 'a', 'ằ' to 'a', 'ắ' to 'a', 'ẳ' to 'a', 'ẵ' to 'a', 'ặ' to 'a',
            'â' to 'a', 'ầ' to 'a', 'ấ' to 'a', 'ẩ' to 'a', 'ẫ' to 'a', 'ậ' to 'a',
            'è' to 'e', 'é' to 'e', 'ẻ' to 'e', 'ẽ' to 'e', 'ẹ' to 'e',
            'ê' to 'e', 'ề' to 'e', 'ế' to 'e', 'ể' to 'e', 'ễ' to 'e', 'ệ' to 'e',
            'ì' to 'i', 'í' to 'i', 'ỉ' to 'i', 'ĩ' to 'i', 'ị' to 'i',
            'ò' to 'o', 'ó' to 'o', 'ỏ' to 'o', 'õ' to 'o', 'ọ' to 'o',
            'ô' to 'o', 'ồ' to 'o', 'ố' to 'o', 'ổ' to 'o', 'ỗ' to 'o', 'ộ' to 'o',
            'ơ' to 'o', 'ờ' to 'o', 'ớ' to 'o', 'ở' to 'o', 'ỡ' to 'o', 'ợ' to 'o',
            'ù' to 'u', 'ú' to 'u', 'ủ' to 'u', 'ũ' to 'u', 'ụ' to 'u',
            'ư' to 'u', 'ừ' to 'u', 'ứ' to 'u', 'ử' to 'u', 'ữ' to 'u', 'ự' to 'u',
            'ỳ' to 'y', 'ý' to 'y', 'ỷ' to 'y', 'ỹ' to 'y', 'ỵ' to 'y',
            'đ' to 'd',

            'À' to 'A', 'Á' to 'A', 'Ả' to 'A', 'Ã' to 'A', 'Ạ' to 'A',
            'Ă' to 'A', 'Ằ' to 'A', 'Ắ' to 'A', 'Ẳ' to 'A', 'Ẵ' to 'A', 'Ặ' to 'A',
            'Â' to 'A', 'Ầ' to 'A', 'Ấ' to 'A', 'Ẩ' to 'A', 'Ẫ' to 'A', 'Ậ' to 'A',
            'È' to 'E', 'É' to 'E', 'Ẻ' to 'E', 'Ẽ' to 'E', 'Ẹ' to 'E',
            'Ê' to 'E', 'Ề' to 'E', 'Ế' to 'E', 'Ể' to 'E', 'Ễ' to 'E', 'Ệ' to 'E',
            'Ì' to 'I', 'Í' to 'I', 'Ỉ' to 'I', 'Ĩ' to 'I', 'Ị' to 'I',
            'Ò' to 'O', 'Ó' to 'O', 'Ỏ' to 'O', 'Õ' to 'O', 'Ọ' to 'O',
            'Ô' to 'O', 'Ồ' to 'O', 'Ố' to 'O', 'Ổ' to 'O', 'Ỗ' to 'O', 'Ộ' to 'O',
            'Ơ' to 'O', 'Ờ' to 'O', 'Ớ' to 'O', 'Ở' to 'O', 'Ỡ' to 'O', 'Ợ' to 'O',
            'Ù' to 'U', 'Ú' to 'U', 'Ủ' to 'U', 'Ũ' to 'U', 'Ụ' to 'U',
            'Ư' to 'U', 'Ừ' to 'U', 'Ứ' to 'U', 'Ử' to 'U', 'Ữ' to 'U', 'Ự' to 'U',
            'Ỳ' to 'Y', 'Ý' to 'Y', 'Ỷ' to 'Y', 'Ỹ' to 'Y', 'Ỵ' to 'Y',
            'Đ' to 'D'
        )
        return this.map { diacriticsMap[it] ?: it }.joinToString("")
    }

    fun String.removeExtraSpaces(): String {
        return this.replace("\\s+".toRegex(), " ").trim()
    }

    fun String.removeSpecialChars(): String {
        val allowedChars = "[a-zA-Z0-9 .,()/\\-]".toRegex()
        return this.filter {
            it.toString().matches(allowedChars)
        }
    }
}


/** Xử lý Intent và Parcelable
 */
object IntentUtils {
    /** Lấy Parcelable an toàn, hỗ trợ cả API 33 trở lên và API cũ hơn.
     */
    inline fun <reified T : Parcelable> Intent.getParcelableSafe(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
        }
    }
}


/** Tính toán và chuyển đổi
 */
object CalculateUtils {
    fun calculateNoOfColumns(context: Context, dpItem: Int, dpPaddingContainer: Int = 0): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpContainer =
            displayMetrics.widthPixels / displayMetrics.density - dpPaddingContainer * displayMetrics.density
        return (dpContainer / dpItem).toInt()
    }

    fun Int.dpToPixel(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }

    /** Tính toán tổng CRC16
     */
    fun ByteArray.generateCheckSumCRC16(): Int {
        var crc = 0xFFFF
        var temp: Int
        var crcByte: Int
        for (byteIndex in this.indices) {
            crcByte = this[byteIndex].toInt() and 0xFF
            for (bitIndex in 0..7) {
                temp = (crc shr 15) xor (crcByte shr 7)
                crc = crc shl 1
                crc = crc and 0xFFFF
                if (temp > 0) {
                    crc = crc xor 0x1021
                    crc = crc and 0xFFFF
                }
                crcByte = crcByte shl 1
                crcByte = crcByte and 0xFF
            }
        }
        return crc
    }
}


/** Xử lí List
 */
object ListUtils {
    /** Sắp xếp List Data Class
     */
    fun <T> List<T>.sortList(propertySelector: (T) -> String, ascending: Boolean = true): List<T> {
        return if (ascending) {
            this.sortedBy(propertySelector)
        } else {
            this.sortedByDescending(propertySelector)
        }
    }
}