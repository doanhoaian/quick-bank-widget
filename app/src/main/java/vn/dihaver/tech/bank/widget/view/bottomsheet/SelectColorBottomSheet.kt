package vn.dihaver.tech.bank.widget.view.bottomsheet

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import vn.dihaver.tech.bank.widget.databinding.BottomSheetSelectColorBinding
import vn.dihaver.tech.bank.widget.utils.CalculateUtils
import vn.dihaver.tech.bank.widget.utils.QrCustomizationData
import vn.dihaver.tech.bank.widget.view.adapter.ColorAdapter

class SelectColorBottomSheet(
    context: Context,
    private val listener: SelectColorBottomSheetListener
) : BottomSheetDialog(context) {

    private lateinit var binding: BottomSheetSelectColorBinding
    private lateinit var colorAdapter: ColorAdapter
    private var idView = -1
    private var selectedColor: String = "#FFFFFF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomSheetSelectColorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        colorAdapter = ColorAdapter(context, QrCustomizationData.colors) {
            selectedColor = it
            dismiss()
        }

        binding.recyclerView.apply {
            layoutManager =
                GridLayoutManager(context, CalculateUtils.calculateNoOfColumns(context, 60, 20))
            adapter = colorAdapter
        }

        setupEditTextColor()
    }

    private fun setupEditTextColor() {
        val editTextColor: EditText = binding.editTextColor
        val cardColor: MaterialCardView = binding.cardColor

        editTextColor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                val input = s.toString().uppercase()
                if (!input.startsWith("#")) {
                    editTextColor.setText("#")
                    editTextColor.setSelection(1)
                    return
                }
                try {
                    val parsedColor = Color.parseColor(input)
                    cardColor.setCardBackgroundColor(parsedColor)
                    selectedColor = input
                    colorAdapter.setColorSelect(input.substring(1))
                    editTextColor.error = null
                } catch (e: IllegalArgumentException) {
                    editTextColor.error = "Màu không hợp lệ"
                }
            }
        })
    }

    override fun dismiss() {
        listener.onClickItem(idView, selectedColor)
        binding.editTextColor.clearFocus()
        super.dismiss()
    }

    fun setStatus(id: Int, title: String, color: String) {
        idView = id
        selectedColor = color

        binding.editTextColor.setText(
            if (color.startsWith("#FF") && color.length == 9) {
                "#${color.substring(3)}"
            } else color
        )

        binding.textTitle.text = "Chọn màu cho $title"
        binding.cardColor.setCardBackgroundColor(Color.parseColor(color))
        colorAdapter.setColorSelect(color)
    }

    fun interface SelectColorBottomSheetListener {
        fun onClickItem(id: Int, color: String)
    }
}