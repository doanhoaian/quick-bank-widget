package vn.dihaver.tech.bank.widget.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.ActivityCreateQrBinding
import vn.dihaver.tech.bank.widget.utils.BankBinVN
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.IntentUtils.getParcelableSafe
import vn.dihaver.tech.bank.widget.utils.ListUtils.sortList
import vn.dihaver.tech.bank.widget.utils.SystemUtils.hideKeyboard
import vn.dihaver.tech.bank.widget.utils.SystemUtils.showKeyboard
import vn.dihaver.tech.bank.widget.view.adapter.BankAdapter
import vn.dihaver.tech.bank.widget.view.dialog.LoadingDialog
import vn.dihaver.tech.bank.widget.viewmodel.CreateQrViewModel

class CreateQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQrBinding

    private val viewModel: CreateQrViewModel by viewModels()

    private lateinit var bankAdapter: BankAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        binding = ActivityCreateQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInsets()
        initComponents()
        initView()
        observeViewModel()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initComponents() {

        /** Adapter
         */
        val bankList = BankBinVN.getBankList().sortList({ it.shortName }, true)
        bankAdapter = BankAdapter(this, bankList) { item ->
            viewModel.updateBankBin(item.bin)
        }
    }

    private fun initView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.editTextBank.textCursorDrawable?.setTint(getColor(R.color.neutral_dark_dark))
        }

        /** RecyclerView
         */
        binding.recyclerBank.apply {
            layoutManager =
                LinearLayoutManager(this@CreateQrActivity, LinearLayoutManager.VERTICAL, false)
            adapter = bankAdapter
        }

        /** ListenerView
         */
        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.editTextBank.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bankAdapter.filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.textBank.setOnClickListener {
            viewModel.updateBankBin("")
            viewModel.updateBankAccountNumber("")
            viewModel.updateQrData("")
            binding.editTextBank.setText("")
            binding.editTextAccountNumber.setText("")
        }

        binding.editTextAccountNumber.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                val accountNumber = view.text.toString().trim()
                if (accountNumber.isNotEmpty()) {
                    viewModel.updateBankAccountNumber(accountNumber)
                }
                true
            } else {
                false
            }
        }

        binding.editTextAccountNumber.setOnFocusChangeListener { _, b ->
            if (b) {
                viewModel.updateBankAccountNumber("")
                viewModel.updateQrData("")
                binding.editTextAccountName.setText("")
            }
        }

        binding.editTextAccountName.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                LoadingDialog.show(this)

                view.clearFocus()
                this.hideKeyboard()

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    viewModel.createQrData(
                        viewModel.getBankBin()!!,
                        binding.editTextAccountNumber.text.toString(),
                        view.text.toString()
                    )
                }
                true
            } else {
                false
            }
        }

        binding.editTextAccountName.setOnFocusChangeListener { _, b ->
            if (b) {
                viewModel.updateQrData("")
            }
        }

        binding.buttonConfirm.setOnClickListener {
            val intent = Intent(this, EditQrActivity::class.java).apply {
                putExtra("qr_content", viewModel.getQrData())
            }
            editQrLauncher.launch(intent)
        }

    }

    private fun observeViewModel() {
        viewModel.bankBin.observe(this) { bankBin ->
            if (bankBin.isNotEmpty()) {
                binding.textBank.text = BankBinVN.getShortName(bankBin)
                binding.imageIcon.setImageBitmap(
                    BitmapUtils.getBitmapFromResource(
                        this@CreateQrActivity,
                        BankBinVN.getBankIcon(BankBinVN.getCode(bankBin)!!)
                    )
                )
                binding.editTextAccountNumber.post {
                    this.showKeyboard(binding.editTextAccountNumber)
                }
            } else {
                binding.imageIcon.setImageResource(R.drawable.svg_fonts_fill_account_balance)
            }
        }

        viewModel.bankAccountNumber.observe(this) { s ->
            if (s.isNotEmpty()) {
                binding.editTextAccountNumber.clearFocus()
                binding.editTextAccountName.post {
                    this.showKeyboard(binding.editTextAccountName)
                }
            }
        }

        viewModel.qrData.observe(this) { qrData ->
            if (qrData.isNotEmpty()) {
                if (LoadingDialog.isShow()) {
                    LoadingDialog.dismiss()
                }
            }
        }
    }

    /** Activity Result
     */

    private val editQrLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    data.getParcelableSafe<QrEntity>("qr_entity")?.let {
                        val intent = Intent().apply {
                            putExtra("code", data.getIntExtra("code", -1))
                            putExtra("qr_entity", it)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }

    /**
     * Loại bỏ Focus EditText khi nhấn vào vị trí bất kì
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}