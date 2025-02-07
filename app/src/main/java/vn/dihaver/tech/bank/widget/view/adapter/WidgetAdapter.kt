package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.dihaver.tech.bank.widget.data.model.WidgetEntity
import vn.dihaver.tech.bank.widget.data.model.WidgetStyle
import vn.dihaver.tech.bank.widget.databinding.AdapterWidgetBinding
import vn.dihaver.tech.bank.widget.utils.QrUtils
import vn.dihaver.tech.bank.widget.view.adapter.WidgetAdapter.WidgetViewHolder

class WidgetAdapter(
    private val context: Context,
    private var items: List<WidgetEntity>,
    private val listener: OnWidgetAdapterListener
) : RecyclerView.Adapter<WidgetViewHolder>() {

    class WidgetViewHolder(val binding: AdapterWidgetBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val binding =
            AdapterWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WidgetViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        val bitmapQr = QrUtils.createQrBitmap(
            context = context,
            qrContent = item.qrContent,
            cusQrColor = item.qrColor,
            cusQrIconPath = item.qrIcon
        )

        binding.textSubtitle.text = "${item.bankName} - ${item.bankNumber}"

        when (item.cusWgStyle) {
            WidgetStyle.BASIC_2X2 -> {
                binding.textTitle.text = "QR đơn giản (2x2)"
                binding.includeWgS1.root.visibility = View.VISIBLE
                binding.includeWgS1.imageQr.setImageBitmap(bitmapQr)
            }

            WidgetStyle.LOGO_2X2 -> {
                binding.textTitle.text = "QR logo (2x2)"
            }

            WidgetStyle.FULL_4X2 -> {
                updateLayoutParams(binding.containerWidget, dimensionRatio = "4:2", widthPercent = 0.6f)
                updateLayoutParams(binding.containerMore, widthPercent = 0.4f)
                binding.textTitle.text = "QR đầy đủ (4x2)"
            }
        }

        /** Listener View
         */
        binding.main.setOnClickListener {
            listener.onClick(item)
        }

        binding.buttonApply.setOnClickListener {
            listener.onApply(item)
        }

        binding.buttonRemove.setOnClickListener {
            listener.onDelete(item)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     */
    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<WidgetEntity>) {
        if (items != newItems) {
            items = newItems
            notifyDataSetChanged()
        }
    }

    /** Function
     */
    private fun updateLayoutParams(view: View, dimensionRatio: String? = null, widthPercent: Float? = null) {
        (view.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
            dimensionRatio?.let { this.dimensionRatio = it }
            widthPercent?.let { this.matchConstraintPercentWidth = it }
            view.layoutParams = this
        }
    }

    interface OnWidgetAdapterListener {
        fun onClick(item: WidgetEntity)
        fun onApply(item: WidgetEntity)
        fun onDelete(item: WidgetEntity)
    }
}