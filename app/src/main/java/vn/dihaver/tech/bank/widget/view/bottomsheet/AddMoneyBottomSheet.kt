package vn.dihaver.tech.bank.widget.view.bottomsheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.BottomSheetAddMoneyBinding
import vn.dihaver.tech.bank.widget.utils.BankBinVN
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeDiacritics
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeExtraSpaces
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeSpecialChars
import vn.dihaver.tech.bank.widget.utils.SystemUtils.showKeyboard
import vn.dihaver.tech.bank.widget.viewmodel.MainViewModel

class AddMoneyBottomSheet(context: Context, private val viewModel: MainViewModel, private val listener: AddMoneyBottomSheetListener): BottomSheetDialog(context) {

    private lateinit var binding: BottomSheetAddMoneyBinding
    private var qrEntity: QrEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BottomSheetAddMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /** View
         */
        updateButtonState()
        context.showKeyboard(binding.editTextMoney)

        /** Listener View
         */
        binding.editTextMoney.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.editTextMoney.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("\\D".toRegex(), "")
                    if (cleanString.isNotEmpty() && cleanString.startsWith("0")) {
                        binding.editTextMoney.setText("")
                        binding.editTextMoney.setSelection(0)
                        binding.editTextMoney.addTextChangedListener(this)
                        return
                    }
                    val formatted = formatNumber(cleanString)
                    current = formatted
                    binding.editTextMoney.setText(formatted)
                    binding.editTextMoney.setSelection(formatted.length)
                    binding.editTextMoney.addTextChangedListener(this)
                }
                updateButtonState()
            }
            private fun formatNumber(number: String): String {
                return number.reversed().chunked(3).joinToString(",").reversed()
            }
        })

        binding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                updateButtonState()
            }
        })


        binding.buttonSave.setOnClickListener {
            val money = parseMoneyStringToLong(binding.editTextMoney.text.toString())
            val content = binding.editTextContent.text.toString().removeDiacritics().removeSpecialChars().removeExtraSpaces()
            binding.editTextContent.setText(content)
            qrEntity?.let {
                listener.onSuccess(
                    qrContent = BankBinVN.generateQrCode(
                        bin = qrEntity!!.bankBin,
                        accNumber = qrEntity!!.accNumber,
                        accHolderName = qrEntity!!.accHolderName,
                        amount = money,
                        content = content
                    ),
                    qrEntity!!,
                    money = money,
                    content = content
                )
            }
            dismiss()
        }

        binding.buttonRemove.setOnClickListener {
            binding.editTextMoney.setText("")
            binding.editTextContent.setText("")
            qrEntity?.let {
                listener.onError(qrEntity!!)
            }
            dismiss()
        }

        /** ObserveViewModel
         */
        viewModel.qrEntityCurrent.observeForever {
            if (this.qrEntity != it) {
                this.qrEntity = it
                binding.editTextMoney.setText("")
                binding.editTextContent.setText("")
            }
        }
    }

    private fun updateButtonState() {
        val isMoneyNotEmpty = binding.editTextMoney.text.toString().isNotEmpty()
        val isContentNotEmpty = binding.editTextContent.text.toString().isNotEmpty()
        val shouldEnableButtons = isMoneyNotEmpty || isContentNotEmpty

        binding.buttonRemove.apply {
            isEnabled = shouldEnableButtons
            alpha = if (shouldEnableButtons) 1f else 0.4f
        }
        binding.buttonSave.apply {
            isEnabled = shouldEnableButtons
            alpha = if (shouldEnableButtons) 1f else 0.4f
        }
    }

    private fun parseMoneyStringToLong(moneyString: String): Long {
        return moneyString.replace(",", "").toLongOrNull() ?: 0L
    }

    interface AddMoneyBottomSheetListener {
        fun onSuccess(qrContent: String, qrEntity: QrEntity, money: Long, content: String)
        fun onError(qrEntity: QrEntity)
    }
}