package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.AdapterAccountBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.FormatUtils.formatAccNumber
import vn.dihaver.tech.bank.widget.view.adapter.AccountAdapter.AccountViewHolder

class AccountAdapter(
    private val context: Context,
    private var itemSelect: QrEntity?,
    private var items: List<QrEntity>,
    private val listener: AccountAdapterListener
) : RecyclerView.Adapter<AccountViewHolder>() {

    class AccountViewHolder(val binding: AdapterAccountBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding =
            AdapterAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        binding.imageIconBank.setImageBitmap(
            BitmapUtils.getBitmapFromResource(
                context,
                item.bankIconRes
            )
        )

        if (item.accAlias.isNotEmpty()) {
            binding.textAccAlias.apply {
                text = item.accAlias
                visibility = View.VISIBLE
            }
        } else {
            binding.textAccAlias.visibility = View.GONE
        }

        binding.textAccHolderName.text = item.accHolderName
        binding.textAccNumber.text = item.accNumber.formatAccNumber()
        binding.imageSelect.isSelected = itemSelect?.id == item.id

        binding.main.setOnClickListener {
            listener.onClickItem(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<QrEntity>) {
        items = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedQr(newSelected: QrEntity) {
        itemSelect = newSelected
        notifyDataSetChanged()
    }

    fun interface AccountAdapterListener {
        fun onClickItem(qrEntity: QrEntity)
    }
}