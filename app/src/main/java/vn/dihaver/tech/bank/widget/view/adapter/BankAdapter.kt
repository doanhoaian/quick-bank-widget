package vn.dihaver.tech.bank.widget.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.view.adapter.BankAdapter.BankViewHolder
import vn.dihaver.tech.bank.widget.databinding.AdapterBankBinding
import vn.dihaver.tech.bank.widget.utils.Bank
import vn.dihaver.tech.bank.widget.utils.BankBinVN
import vn.dihaver.tech.bank.widget.utils.BitmapUtils

class BankAdapter(private val activity: Activity, private val items: List<Bank>, private val listener: OnBankAdapterListener) : RecyclerView.Adapter<BankViewHolder>() {

    class BankViewHolder(val binding: AdapterBankBinding) : ViewHolder(binding.root)

    private var filteredItems: List<Bank> = items
    private var queryString = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding = AdapterBankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val binding = holder.binding
        val item = filteredItems[position]

        val nameIcon = BankBinVN.getBankIcon(item.code)
        binding.imageIcon.setImageBitmap(BitmapUtils.getBitmapFromResource(activity as Context, nameIcon))

        binding.textTitle.text = highlightText(item.shortName, queryString)
        binding.textSubtitle.text = highlightText(item.name, queryString)

        binding.main.setOnClickListener {
            listener.onClickItem(item)
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        queryString = query
        filteredItems = if (query.isEmpty()) {
            items
        } else {
            items.filter {
                it.shortName.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    private fun highlightText(text: String, query: String): Spanned {
        val spannableString = SpannableString(text)
        if (query.isEmpty()) return spannableString

        val color = activity.getColor(R.color.highlight_darkest)
        val startIndex = text.indexOf(query, ignoreCase = true)

        if (startIndex >= 0) {
            val endIndex = startIndex + query.length
            spannableString.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    fun interface OnBankAdapterListener {
        fun onClickItem(item: Bank)
    }
}