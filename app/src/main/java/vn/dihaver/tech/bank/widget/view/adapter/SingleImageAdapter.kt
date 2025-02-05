package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.LruCache
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.AdapterSingleImageBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.QrUtils
import vn.dihaver.tech.bank.widget.utils.SystemUtils.copyToClipboard

class SingleImageAdapter(private val activity: Activity, private var items: List<QrEntity>) :
    Adapter<SingleImageAdapter.SingleImageViewHolder>() {

    class SingleImageViewHolder(val binding: AdapterSingleImageBinding) : ViewHolder(binding.root)

    private val glide = Glide.with(activity as Context)
    private val bitmapCache = LruCache<String, Bitmap>(10)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleImageViewHolder {
        val binding = AdapterSingleImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SingleImageViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        val bitmapBackground = BitmapUtils.getBitmapFromResource(activity as Context, item.cusThemePath)!!

        binding.imageBackground.setImageBitmap(bitmapBackground)

        Palette.from(bitmapBackground).generate { palette ->
            palette?.let {
                val darkVibrantColor = it.getDarkVibrantColor(activity.getColor(R.color.neutral_dark_medium))

                binding.containerQr.setCardBackgroundColor(darkVibrantColor)
                binding.imageQrTitleBack.setColorFilter(darkVibrantColor)
                binding.imageQrTitleFore.setColorFilter(darkVibrantColor)
            }
        }

        binding.textTitle.text = item.accHolderName

        val qrBitmap = getQrBitmapFromCacheOrGenerate(item.qrContent, position)
        glide.load(qrBitmap).into(binding.imageQr)

        setAccountNumber(binding.textSubTitle, false, item.accNumber)

        /**
         * Listener View
          */
        binding.textSubTitle.setOnClickListener {
            val currentState = binding.textSubTitle.isSelected
            setAccountNumber(binding.textSubTitle, !currentState, item.accNumber)
        }

        binding.textSubTitle.setOnLongClickListener {
            activity.copyToClipboard(item.accNumber)
            true
        }
    }

    private fun getQrBitmapFromCacheOrGenerate(qrData: String, position: Int): Bitmap {
        val item = items[position]
        var bitmap = bitmapCache[qrData]
        if (bitmap == null) {
            bitmap = QrUtils.generateQrBitmap(qrData, Color.parseColor(item.cusQrColor), Color.WHITE)
            QrUtils.addLogoToQr(
                bitmap!!, BitmapUtils.getBitmapFromResource(activity as Context, item.bankIconRes)!!, 4
            )
            bitmapCache.put(qrData, bitmap)
        }
        return bitmap
    }

    private fun setAccountNumber(textView: TextView, isSelect: Boolean, originalText: String) {
        textView.isSelected = isSelect
        if (!isSelect) {
            textView.text = "*".repeat(originalText.length)
        } else {
            textView.text = originalText
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<QrEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getCurrentItem(position: Int): QrEntity {
        return items[position]
    }
}