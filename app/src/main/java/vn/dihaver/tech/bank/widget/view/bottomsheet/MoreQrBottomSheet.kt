package vn.dihaver.tech.bank.widget.view.bottomsheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.dihaver.tech.bank.widget.databinding.BottomSheetMoreQrBinding

class MoreQrBottomSheet(context: Context, private val listener: MoreQrBottomSheetListener): BottomSheetDialog(context) {

    private lateinit var binding: BottomSheetMoreQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BottomSheetMoreQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.buttonWidget.setOnClickListener {
            listener.onWidget()
            dismiss()
        }

        binding.buttonEdit.setOnClickListener {
            listener.onEdit()
            dismiss()
        }

        binding.buttonShare.setOnClickListener {
            listener.onShare()
            dismiss()
        }

        binding.buttonDownload.setOnClickListener {
            listener.onDownload()
            dismiss()
        }

        binding.buttonDelete.setOnClickListener {
            listener.onDelete()
            dismiss()
        }
    }

    interface MoreQrBottomSheetListener {
        fun onWidget()
        fun onEdit()
        fun onShare()
        fun onDownload()
        fun onDelete()
    }

}