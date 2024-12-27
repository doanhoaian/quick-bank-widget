package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.AdapterSingleImageBinding
import vn.dihaver.tech.bank.widget.utils.generateQrBitmap

class SingleImageAdapter(private val activity: Activity, private var items: List<QrEntity>) :
    Adapter<SingleImageAdapter.SingleImageViewHolder>() {

    class SingleImageViewHolder(val binding: AdapterSingleImageBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleImageViewHolder {
        val binding =
            AdapterSingleImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SingleImageViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        Glide.with(activity as Context)
            .load("https://i.pinimg.com/736x/27/aa/ef/27aaef7780ae0ea1672e8e610aa932b2.jpg")
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageBackground)

        Glide.with(activity as Context)
            .load(item.bankLogoFull)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageIcon)
        binding.textTitle.text = item.accountName
        binding.textSubTitle.text = item.accountNumber
        binding.imageQr.setImageBitmap(generateQrBitmap(item.qrData))

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