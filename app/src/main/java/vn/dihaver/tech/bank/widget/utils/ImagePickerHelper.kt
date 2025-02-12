package vn.dihaver.tech.bank.widget.utils

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

class ImagePickerHelper(
    activity: ComponentActivity,
    private val allowMultiple: Boolean = false,
    private val onImagesPicked: (List<Uri>) -> Unit
) {

    private val pickMultipleVisualMediaLauncher =
        activity.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                onImagesPicked(uris)
            }
        }

    private val pickSingleVisualMediaLauncher =
        activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onImagesPicked(listOf(it))
            }
        }

    fun pickImages() {
        if (allowMultiple) {
            pickMultipleVisualMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            pickSingleVisualMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}