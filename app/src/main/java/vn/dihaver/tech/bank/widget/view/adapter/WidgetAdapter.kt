package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.alexzhirkevich.customqrgenerator.QrData
import vn.dihaver.tech.bank.widget.data.model.WidgetEntity
import vn.dihaver.tech.bank.widget.data.model.WidgetStyle
import vn.dihaver.tech.bank.widget.databinding.AdapterWidgetBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.CalculateUtils.dpToPixel
import vn.dihaver.tech.bank.widget.utils.QrCreator
import vn.dihaver.tech.bank.widget.view.adapter.WidgetAdapter.WidgetViewHolder

class WidgetAdapter(
    private val context: Context,
    private var items: List<WidgetEntity>,
    private val qrCreator: QrCreator,
    private val listener: OnWidgetAdapterListener
) : RecyclerView.Adapter<WidgetViewHolder>() {

    class WidgetViewHolder(val binding: AdapterWidgetBinding) : ViewHolder(binding.root)

    private val elevation = 4.dpToPixel(context)
    private val padding = 10.dpToPixel(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val binding =
            AdapterWidgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WidgetViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        val dataQr = QrData.Text(item.qrEntity.qrContent)
        val drawableQr = qrCreator.createDrawable(dataQr, item.qrEntity.cusQrEntity)
        val bgColor = ColorStateList.valueOf(Color.parseColor(item.qrEntity.cusQrEntity.qrBackgroundColor))
        val textAlias = item.qrEntity.accAlias
        val textHolderName = item.qrEntity.accHolderName
        val textNumber = item.qrEntity.accNumber
        val bitmapLogo = BitmapUtils.getBitmapFromResource(context, item.qrEntity.bankLogoRes)

        binding.textSubtitle.text = "${item.qrEntity.bankShortName} - ${item.qrEntity.accNumber}"


        when (item.widgetStyle) {
            WidgetStyle.SIMPLE_2X2 -> {
                binding.textTitle.text = "QR đơn giản (2x2)"

                binding.includeWgSimple.root.visibility = View.VISIBLE
                binding.includeWgSimple.root.elevation = elevation.toFloat()
                binding.includeWgSimple.main.backgroundTintList = bgColor
                binding.includeWgSimple.imageQr.setImageDrawable(drawableQr)
                binding.includeWgSimple.imageQr.setPadding(padding, padding, padding, padding)
            }

            WidgetStyle.ADVANCED_4X2 -> {
                updateLayoutParams(binding.containerWidget, dimensionRatio = "4:2", widthPercent = 0.6f)
                updateLayoutParams(binding.containerMore, widthPercent = 0.4f)

                binding.textTitle.text = "QR nâng cao (4x2)"

                binding.includeWgAdvanced.imageLogoBank.visibility = View.VISIBLE
                binding.includeWgAdvanced.textHolderName.visibility = View.VISIBLE
                binding.includeWgAdvanced.textNumber.visibility = View.VISIBLE
                if (textAlias.isNotEmpty()) {
                    binding.includeWgAdvanced.textAlias.text = textAlias
                    binding.includeWgAdvanced.textAlias.visibility = View.VISIBLE
                }
                binding.includeWgAdvanced.textHolderName.text = textHolderName
                binding.includeWgAdvanced.textNumber.text = textNumber
                binding.includeWgAdvanced.imageLogoBank.setImageBitmap(bitmapLogo)

                binding.includeWgAdvanced.root.visibility = View.VISIBLE
                binding.includeWgAdvanced.root.elevation = elevation.toFloat()
                binding.includeWgAdvanced.main.backgroundTintList = bgColor
                binding.includeWgAdvanced.imageQr.setImageDrawable(drawableQr)
                binding.includeWgAdvanced.imageQr.setPadding(padding, padding, padding, padding)
            }
        }

        /** Listener View
         */
        binding.main.setOnClickListener {
            listener.onClick(item)
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
        items = newItems
        notifyDataSetChanged()
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
        fun onDelete(item: WidgetEntity)
    }
}