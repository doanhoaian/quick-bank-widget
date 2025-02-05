package vn.dihaver.tech.bank.widget.view.bottomsheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.BottomSheetSelectQrBinding
import vn.dihaver.tech.bank.widget.view.adapter.AccountAdapter
import vn.dihaver.tech.bank.widget.viewmodel.MainViewModel

class SelectQrBottomSheet(
    context: Context,
    private val viewModel: MainViewModel,
    private val listener: SelectQrBottomSheetListener
) : BottomSheetDialog(context) {

    private lateinit var binding: BottomSheetSelectQrBinding
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomSheetSelectQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        accountAdapter = AccountAdapter(context, null, emptyList()) { item ->
            listener.onClickItem(item)
            dismiss()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            adapter = accountAdapter
        }

        viewModel.qrEntityCurrent.observeForever { newEntity ->
            accountAdapter.updateSelectedQr(newEntity)
        }

        viewModel.qrList.observeForever { newList ->
            accountAdapter.updateList(newList)
        }
    }

    fun interface SelectQrBottomSheetListener {
        fun onClickItem(qrEntity: QrEntity)
    }
}