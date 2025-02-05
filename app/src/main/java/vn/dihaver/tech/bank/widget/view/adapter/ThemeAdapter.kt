package vn.dihaver.tech.bank.widget.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.ThemeEntity
import vn.dihaver.tech.bank.widget.databinding.AdapterThemeBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils

class ThemeAdapter(val context: Context, private val items: List<ThemeEntity>, private val listener: OnImageBackgroundListener): RecyclerView.Adapter<ThemeAdapter.ImageBackgroundViewHolder>() {

    class ImageBackgroundViewHolder(val binding: AdapterThemeBinding): RecyclerView.ViewHolder(binding.root)

    private val colorSelected = context.getColor(R.color.highlight_dark)
    private val colorUnSelected = context.getColor(R.color.neutral_dark_light)

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageBackgroundViewHolder {
        val binding = AdapterThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageBackgroundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageBackgroundViewHolder, @Suppress("RecyclerView") position: Int) {
        val binding = holder.binding
        val item = items[position]

        binding.textTitle.text = item.name
        binding.imageBackground.setImageResource(item.res)

        if (position == selectedPosition) {
            binding.cardImage.strokeColor = colorSelected
            binding.imageSelect.visibility = View.VISIBLE
            binding.textTitle.setTextColor(colorSelected)
        } else {
            binding.cardImage.strokeColor = colorUnSelected
            binding.imageSelect.visibility = View.GONE
            binding.textTitle.setTextColor(colorUnSelected)
        }

        binding.main.setOnClickListener {
            if (position != selectedPosition) {
                val previousPosition = selectedPosition
                selectedPosition = position
                listener.onClickItem(BitmapUtils.convertNameToPath(item.id, BitmapUtils.PathType.RES))
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setThemeSelect(image: String) {
        val item = items.find { it.id == BitmapUtils.convertPathToName(image) }
        val position = items.indexOf(item)
        if (position != -1) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    fun interface OnImageBackgroundListener {
        fun onClickItem(item: String)
    }
}