package vn.dihaver.tech.bank.widget.view.bottomsheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.dihaver.tech.bank.widget.databinding.BottomSheetBackupRestoreBinding

class BackupRestoreBottomSheet(
    context: Context,
    private val listener: BackupRestoreBottomSheetListener
) : BottomSheetDialog(context) {

    private lateinit var binding: BottomSheetBackupRestoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = BottomSheetBackupRestoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Listener View */
        binding.buttonBackup.setOnClickListener {
            listener.onBackup()
            dismiss()
        }

        binding.buttonRestore.setOnClickListener {
            listener.onRestore()
            dismiss()
        }
    }

    interface BackupRestoreBottomSheetListener {
        fun onBackup()
        fun onRestore()
    }
}