package vn.dihaver.tech.bank.widget.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class ImagePickerHelper(
    private val activity: ComponentActivity,
    private val allowMultiple: Boolean = false,
    private val onImagesPicked: (List<Uri>) -> Unit
) {
    private val galleryPickerLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.clipData?.let { clipData ->
                val uris = (0 until clipData.itemCount).map { clipData.getItemAt(it).uri }
                onImagesPicked(uris)
            } ?: result.data?.data?.let { uri ->
                onImagesPicked(listOf(uri))
            }
        }

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGalleryPicker()
            } else {
                showPermissionDeniedDialog()
            }
        }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Quyền truy cập bị từ chối")
            .setMessage("Ứng dụng cần quyền truy cập ảnh để tiếp tục. Hãy cấp quyền trong cài đặt.")
            .setPositiveButton("Mở cài đặt") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }


    fun pickImages() {
        requestStoragePermission()
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            openGalleryPicker()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            if (allowMultiple) putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        galleryPickerLauncher.launch(intent)
    }
}