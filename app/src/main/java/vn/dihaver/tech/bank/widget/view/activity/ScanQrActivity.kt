package vn.dihaver.tech.bank.widget.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.ActivityScanQrBinding
import vn.dihaver.tech.bank.widget.utils.IntentUtils.getParcelableSafe
import vn.dihaver.tech.bank.widget.utils.QrProcess
import vn.dihaver.tech.bank.widget.utils.SystemUtils.translucentSystemBars
import vn.dihaver.tech.bank.widget.viewmodel.ScanQrViewModel

class ScanQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding

    private val viewModel: ScanQrViewModel by viewModels()

    private lateinit var barcodeView: DecoratedBarcodeView

    private val callback = BarcodeCallback { result ->
        result?.let {
            viewModel.setQrContent(it.text)
            barcodeView.pause()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.translucentSystemBars(isStatus = true, isNavigation = true)

        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInsets()
        checkCameraPermission()
        initView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::barcodeView.isInitialized) {
            barcodeView.pause()
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        barcodeView = binding.cameraPreview
        barcodeView.setStatusText("")
        barcodeView.decodeContinuous(callback)
    }

    private fun initView() {

        /** Listener View
         */
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonFlash.setOnClickListener {
            if (binding.buttonFlash.isSelected) {
                binding.cameraPreview.setTorchOff()
            } else {
                binding.cameraPreview.setTorchOn()
            }
            binding.buttonFlash.isSelected = !binding.buttonFlash.isSelected
        }


    }

    private fun observeViewModel() {
        viewModel.qrContent.observe(this) { content ->
            content?.let {
                if (!QrProcess.isQrBank(content)) {
                    showDialogErrorInputQr()
                    return@let
                }

                val intent = Intent(this, EditQrActivity::class.java)
                intent.putExtra("qr_content", content)
                editQrLauncher.launch(intent)
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Quyền camera bị từ chối")
            .setMessage("Bạn cần cấp quyền camera để quét mã QR.")
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }

    private fun showDialogErrorInputQr() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Thông báo")
            .setMessage("Mã QR không đúng định dạng. Vui lòng thử lại.")
            .setPositiveButton("OK") { dialog, _ ->
                barcodeView.resume()
                dialog.dismiss()
            }
            .show()
    }

    /** Activity Result
     */
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                showPermissionDeniedDialog()
            }
        }

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
}