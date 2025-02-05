package vn.dihaver.tech.bank.widget.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.caverock.androidsvg.SVG

object BitmapUtils {

    private const val TAG = "BitmapUtils"
    private const val RES_PREFIX = "res://drawable/"
    private const val ASSET_PREFIX = "asset://"

    enum class PathType {
        RES, ASSET
    }

    /**
     * Chuyển từ tên file thành đường dẫn (resource hoặc asset)
     * - type: RES (resource) hoặc ASSET (asset)
     */
    fun convertNameToPath(name: String, type: PathType): String {
        return when (type) {
            PathType.RES -> "$RES_PREFIX$name"
            PathType.ASSET -> "$ASSET_PREFIX$name"
        }
    }

    /**
     * Chuyển từ đường dẫn thành tên file (có thể là resource hoặc asset)
     * - Tự động xác định loại và trả về tên file
     */
    fun convertPathToName(path: String): String {
        return when {
            path.startsWith(RES_PREFIX) -> {
                path.removePrefix(RES_PREFIX)
            }
            path.startsWith(ASSET_PREFIX) -> {
                path.removePrefix(ASSET_PREFIX)
            }
            else -> {
                path
            }
        }
    }

    /**
     * Lấy Bitmap từ đường dẫn file, URI, resource hoặc asset
     */
    fun getBitmapFromPath(context: Context, path: String): Bitmap? {
        return try {
            when {
                path.startsWith("content://") -> {
                    val uri = Uri.parse(path)
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                path.startsWith("storage/") || path.contains("/Android/data/") -> {
                    BitmapFactory.decodeFile(path)
                }

                path.startsWith(ASSET_PREFIX) -> {
                    val assetName = convertPathToName(path)
                    getBitmapFromAsset(context, assetName)
                }

                path.startsWith(RES_PREFIX) -> {
                    val resourceName = convertPathToName(path)
                    getBitmapFromResource(context, resourceName)
                }

                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi tải Bitmap từ Path: $path", e)
            null
        }
    }

    /**
     * Lấy Bitmap từ file trong thư mục asset (Ví dụ: "my_image.png" -> asset://my_image.png)
     */
    private fun getBitmapFromAsset(context: Context, assetName: String): Bitmap? {
        return try {
            context.assets.open(assetName).use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi tải Bitmap từ Asset: $assetName", e)
            null
        }
    }

    /**
     * Lấy Bitmap từ tên resource (Ví dụ: "bg_not_have" -> R.drawable.bg_not_have)
     */
    fun getBitmapFromResource(context: Context, resourceName: String): Bitmap? {
        @SuppressLint("DiscouragedApi")
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        return if (resourceId != 0) {
            @SuppressLint("UseCompatLoadingForDrawables")
            when (val drawable = context.resources.getDrawable(resourceId, null)) {
                is BitmapDrawable -> drawable.bitmap
                is VectorDrawable -> drawable.toBitmap()
                else -> try {
                    val svg = SVG.getFromResource(context.resources, resourceId)
                    val picture: Picture = svg.renderToPicture()
                    val bitmap = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawPicture(picture)
                    bitmap
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi khi tải Bitmap SVG từ Drawable: $drawable", e)
                    null
                }
            }
        } else {
            Log.e(TAG, "Không tìm thấy Resource: $resourceName")
            null
        }
    }
}