package vn.dihaver.tech.bank.widget.view.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.dihaver.tech.bank.widget.databinding.ActivityMainBinding
import vn.dihaver.tech.bank.widget.utils.QrProcess
import vn.dihaver.tech.bank.widget.utils.QrStorage
import vn.dihaver.tech.bank.widget.utils.decodeQrFromImage
import vn.dihaver.tech.bank.widget.view.adapter.SingleImageAdapter
import vn.dihaver.tech.bank.widget.view.bottomsheet.MoreQrBottomSheet
import vn.dihaver.tech.bank.widget.view.bottomsheet.MoreQrBottomSheet.MoreQrBottomSheetListener
import vn.dihaver.tech.bank.widget.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var qrStorage: QrStorage
    private lateinit var singleImageAdapter: SingleImageAdapter

    private val moreQrBottomSheet: MoreQrBottomSheet by lazy {
        MoreQrBottomSheet(this, object:MoreQrBottomSheetListener {
            override fun delete() {
                val id = singleImageAdapter.getCurrentItem(binding.viewPager.currentItem).id
                if (!viewModel.deleteQrById(id, qrStorage)) {
                    Log.e(TAG, "Không thể xóa item: $id")
                }
            }
        }).apply {
            onDismissListener = {
                viewModel.updateIsMoreQr(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInsets()
        initComponent()
        initView()
        observeViewModel()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initComponent() {
        qrStorage = QrStorage(this@MainActivity)

        val initialQrList = qrStorage.getAllQr()
        viewModel.updateQrList(initialQrList)

        singleImageAdapter = SingleImageAdapter(this, initialQrList)
    }

    private fun initView() {
        binding.viewPager.apply {
            adapter = singleImageAdapter
        }

        // Listener View
        binding.buttonAddQr.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.containerListEmpty.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun observeViewModel() {

        viewModel.qrList.observe(this) { qrList ->
            singleImageAdapter.updateData(qrList)
        }

        viewModel.isMoreQr.observe(this) { isMoreQr ->
            if (isMoreQr) {
                moreQrBottomSheet.show()
            }
        }

    }

    private fun showDialogErrorInputQr() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Thông báo")
        builder.setMessage("Mã QR không đúng định dạng. Vui lòng thử lại.")
        builder.setPositiveButton("OK")  { dialog, _ ->
            dialog.cancel()
        }
        builder.create().show()
    }

    private fun showDialogErrorDuplicateQr(bankName: String, accountNumber: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Thông báo")
        builder.setMessage("Đã tồn tại tài khoản:\n$accountNumber - $bankName \nVui lòng thử lại.")
        builder.setPositiveButton("OK")  { dialog, _ ->
            dialog.cancel()
        }
        builder.create().show()
    }

    // Activity Result
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            decodeQrFromImage(it, this) { qrResult ->
                handleQrResult(qrResult)
            }
        }
    }

    private fun handleQrResult(qrResult: String?) {
        if (qrResult == null) {
            showDialogErrorInputQr()
            return
        }
        val qrEntity = QrProcess.processQrData(qrResult)
        if (qrEntity == null) {
            showDialogErrorInputQr()
            return
        }
        if (!viewModel.addQr(qrEntity, qrStorage)) {
            showDialogErrorDuplicateQr(qrEntity.bankName, qrEntity.accountNumber)
        }
    }

}