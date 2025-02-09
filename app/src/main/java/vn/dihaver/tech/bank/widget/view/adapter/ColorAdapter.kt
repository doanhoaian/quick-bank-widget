package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.dihaver.tech.bank.widget.databinding.AdapterColorBinding
import vn.dihaver.tech.bank.widget.utils.CalculateUtils.dpToPixel


class ColorAdapter(val context: Context, private val items: List<String>, private val listener: OnColorListener) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    class ColorViewHolder(val binding: AdapterColorBinding) : ViewHolder(binding.root)

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = AdapterColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = holder.binding
        val item = items[position]

        binding.colorView.setCardBackgroundColor(Color.parseColor(item))

        if (position == selectedPosition) {
            binding.colorView.strokeWidth = 2.dpToPixel(context)
            binding.colorViewSelect.visibility = View.VISIBLE
        } else {
            binding.colorView.strokeWidth = 0
            binding.colorViewSelect.visibility = View.GONE
        }

        binding.colorView.setOnClickListener {
            if (position != selectedPosition) {
                val previousPosition = selectedPosition
                selectedPosition = position
                listener.onColorClick(item)
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setColorSelect(color: String) {
        val item = items.find { it.contains(color) }
        val position = items.indexOf(item)
        if (position != -1) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        } else {
            val previousPosition = selectedPosition
            selectedPosition = -1
            notifyItemChanged(previousPosition)
        }
    }

    fun interface OnColorListener {
        fun onColorClick(color: String)
    }
}