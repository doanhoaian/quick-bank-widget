package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.ShapeQrEntity
import vn.dihaver.tech.bank.widget.databinding.AdapterShapeQrBinding

class ShapeQrAdapter(
    context: Context,
    private val items: List<ShapeQrEntity>,
    private val listener: ShapeQrAdapterListener
) : RecyclerView.Adapter<ShapeQrAdapter.ShapeQrViewHolder>() {

    class ShapeQrViewHolder(val binding: AdapterShapeQrBinding) : ViewHolder(binding.root)

    private val colorSelected = context.getColor(R.color.highlight_dark)
    private val colorUnSelected = context.getColor(R.color.neutral_light_darkest)

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShapeQrViewHolder {
        val binding = AdapterShapeQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShapeQrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShapeQrViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = holder.binding
        val item = items[position]

        binding.imageShape.setImageResource(item.resPreview)

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
                listener.onClickItem(item)
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setShapeQrSelect(shape: Any) {
        val item = items.find {
            it.bodyShape == shape || it.eyeFrameShape == shape || it.eyeBallShape == shape
        }
        val position = items.indexOf(item)
        if (position != -1) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    fun interface ShapeQrAdapterListener {
        fun onClickItem(item: ShapeQrEntity)
    }
}