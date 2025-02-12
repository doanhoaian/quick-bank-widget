package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.databinding.AdapterColorBinding


class ColorAdapter(val context: Context, private var items: List<String>, private val listener: OnColorListener) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    class ColorViewHolder(val binding: AdapterColorBinding) : ViewHolder(binding.root)

    private val colorSelected = context.getColor(R.color.highlight_dark)
    private val colorUnSelected = context.getColor(R.color.neutral_light_darkest)
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = AdapterColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = holder.binding
        val item = items[position]

        if (item != "null") {
            binding.imageNull.visibility = View.GONE
            binding.cardView.setCardBackgroundColor(Color.parseColor(item))
        } else {
            binding.imageNull.visibility = View.VISIBLE
            binding.cardView.setCardBackgroundColor(context.getColor(R.color.neutral_light_medium))
        }

        if (position == selectedPosition) {
            binding.cardView.strokeColor = colorSelected
            binding.imageSelect.visibility = View.VISIBLE
        } else {
            binding.cardView.strokeColor = colorUnSelected
            binding.imageSelect.visibility = View.GONE
        }

        binding.cardView.setOnClickListener {
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

    @SuppressLint("NotifyDataSetChanged")
    fun setColors(newItems: List<String>) {
        if (items != newItems) {
            items = newItems
            notifyDataSetChanged()
        }
    }

    fun interface OnColorListener {
        fun onColorClick(color: String)
    }
}