package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.databinding.AdapterImageLogoBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.ImagePickerHelper
import java.io.File

class ImageLogoAdapter(
    private val activity: ComponentActivity,
    private var items: List<String>,
    private val cropLauncher: ActivityResultLauncher<Intent>,
    private val listener: ImageLogoAdapterListener
) : RecyclerView.Adapter<ImageLogoAdapter.ImageLogoViewHolder>() {

    class ImageLogoViewHolder(val binding: AdapterImageLogoBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val imagePickerHelper = ImagePickerHelper(activity, false) { uris ->
        uris.firstOrNull()?.let { uri ->
            val destinationUri = Uri.fromFile(
                File(activity.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            )
            val uCropIntent = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1000, 1000)
                .getIntent(activity)
            cropLauncher.launch(uCropIntent)
        }
    }

    private val colorSelected = activity.getColor(R.color.highlight_dark)
    private val colorUnSelected = activity.getColor(R.color.neutral_light_darkest)
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageLogoViewHolder {
        val binding = AdapterImageLogoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageLogoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageLogoViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = holder.binding
        val item = items[position]

        // Hiển thị view theo loại item
        binding.imageNull.visibility = if (item == "null") View.VISIBLE else View.GONE

        if (item.contains("res://")) {
            binding.imageLogoBank.visibility = View.VISIBLE
            binding.imageLogoBank.setImageBitmap(BitmapUtils.getBitmapFromPath(activity, item))
        } else {
            binding.imageLogoBank.visibility = View.GONE
        }

        if (item.contains("/Android/data/")) {
            binding.imageFile.visibility = View.VISIBLE
            binding.imageFile.setImageBitmap(BitmapUtils.getBitmapFromPath(activity, item))
        } else {
            binding.imageFile.visibility = View.GONE
        }

        binding.imageAdd.visibility = if (item == "add") View.VISIBLE else View.GONE

        // Cập nhật màu stroke và hiển thị imageSelect nếu item đang được chọn
        if (position == selectedPosition) {
            binding.main.strokeColor = colorSelected
            binding.imageSelect.visibility = View.VISIBLE
        } else {
            binding.main.strokeColor = colorUnSelected
            binding.imageSelect.visibility = View.GONE
        }

        // Xử lý sự kiện click cho item
        binding.main.setOnClickListener {
            if (item != "add") {
                if (position != selectedPosition) {
                    val prevPosition = selectedPosition
                    selectedPosition = position
                    listener.onClickItem(item)
                    notifyItemChanged(prevPosition)
                    notifyItemChanged(selectedPosition)
                }
            } else {
                imagePickerHelper.pickImages()
            }
        }

        // Xử lý sự kiện long click để xóa ảnh nếu item chứa "/Android/data/"
        binding.main.setOnLongClickListener {
            if (item.contains("/Android/data/")) {
                AlertDialog.Builder(activity)
                    .setTitle("Xác nhận xóa hình")
                    .setMessage("Bạn có chắc muốn xóa hình này không?")
                    .setPositiveButton("Xóa") { _, _ ->
                        val file = File(item)
                        if (file.exists() && file.delete()) {
                            // Xử lý cập nhật danh sách sau khi xóa file
                            val pos = holder.adapterPosition
                            val mutableList = items.toMutableList()
                            mutableList.removeAt(pos)
                            items = mutableList
                            notifyItemRemoved(pos)
                            notifyItemRangeRemoved(pos, items.size)

                            // Nếu item bị xóa đang được chọn, chọn lại items[0]
                            if (selectedPosition == pos) {
                                selectedPosition = 0
                                listener.onClickItem(items[0])
                                notifyItemChanged(0)
                            } else if (selectedPosition > pos) {
                                selectedPosition--
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.message_error_try_again), Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setLogoSelect(value: String) {
        val position = items.indexOf(value)
        if (position != -1) {
            val prevPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(prevPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLogos(newList: List<String>) {
        if (items != newList) {
            items = newList
            notifyDataSetChanged()
        }
    }

    /**
     * Thêm ảnh mới vào danh sách.
     * Loại bỏ item "add", thêm ảnh mới vào vị trí đó, sau đó chèn lại item "add" ở cuối.
     */
    fun addNewLogo(newLogo: String) {
        val mutableList = items.toMutableList()
        val addIndex = mutableList.indexOf("add")
        if (addIndex != -1) {
            mutableList.removeAt(addIndex)
            notifyItemRemoved(addIndex)
            mutableList.add(addIndex, newLogo)
            notifyItemInserted(addIndex)
            val newAddIndex = mutableList.size
            mutableList.add("add")
            notifyItemInserted(newAddIndex)
        }
        items = mutableList
    }

    fun interface ImageLogoAdapterListener {
        fun onClickItem(item: String)
    }
}