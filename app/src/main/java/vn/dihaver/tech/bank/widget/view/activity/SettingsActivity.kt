package vn.dihaver.tech.bank.widget.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.storage.QrStorage
import vn.dihaver.tech.bank.widget.databinding.ActivitySettingsBinding
import vn.dihaver.tech.bank.widget.view.bottomsheet.BackupRestoreBottomSheet
import vn.dihaver.tech.bank.widget.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var qrStorage: QrStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInsets()
        initComponents()
        initView()
        obverseViewModel()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initComponents() {
        /** Init QrStorage */
        qrStorage = QrStorage(this)
    }

    private fun initView() {

        /** View
         */
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        @SuppressLint("SetTextI18n")
        @Suppress("DEPRECATION")
        binding.textVersion.text =
            "${packageInfo.versionName} (${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode})"

        /** Listener View
         */

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonBackupRestore.setOnClickListener {
            backupRestoreBottomSheet.show()
        }

        binding.buttonRequest.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.info_help_mail)))
            }
            startActivity(intent)
        }

        binding.buttonFaq.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_fab)))
            startActivity(intent)
        }

        binding.buttonTerms.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_terms_conditions)))
            startActivity(intent)
        }

        binding.buttonPrivacyPolicy.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy_policy)))
            startActivity(intent)
        }
    }

    private fun obverseViewModel() {

    }

    /** Activity Result */
    private val backupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val success = qrStorage.backupToUri(uri)
                    if (success) {
                        snackbar.setText("Sao lưu thành công").show()
                    } else {
                        snackbar.setText("Sao lưu thất bại").show()
                    }
                }
            }
        }

    private val restoreLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val documentFile = DocumentFile.fromSingleUri(this, uri)
                    val fileName = documentFile?.name
                    if (fileName == null || !fileName.endsWith(".qrw", ignoreCase = true)) {
                        snackbar.setText("Tệp không đúng định dạng (*.qrw)")
                        return@let
                    }

                    if (qrStorage.getAll().isNotEmpty()) {
                        AlertDialog.Builder(this)
                            .setTitle("Khôi phục dữ liệu")
                            .setMessage("Dữ liệu QR hiện tại đã tồn tại. Bạn muốn ghi đè hay nối dữ liệu?")
                            .setPositiveButton("Ghi đè") { _, _ ->
                                performRestore(uri, overrideCurrent = true)
                            }
                            .setNegativeButton("Nối") { _, _ ->
                                performRestore(uri, overrideCurrent = false)
                            }
                            .setNeutralButton("Hủy", null)
                            .show()
                    } else {
                        performRestore(uri, overrideCurrent = true)
                    }
                }
            }
        }

    /** BottomSheet */
    private val backupRestoreBottomSheet: BackupRestoreBottomSheet by lazy {
        BackupRestoreBottomSheet(this, object :
            BackupRestoreBottomSheet.BackupRestoreBottomSheetListener {
            override fun onBackup() {
                if (qrStorage.getAll().isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/octet-stream"
                        putExtra(Intent.EXTRA_TITLE, getString(R.string.label_file_name_backup))
                    }
                    backupLauncher.launch(intent)
                } else {
                    snackbar.setText("Không có dữ liệu để khôi phục").show()
                }
            }

            override fun onRestore() {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/octet-stream"
                }
                restoreLauncher.launch(intent)
            }
        })
    }

    /** Snackbar */
    private val snackbar: Snackbar by lazy {
        Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.neutral_light_lightest))
            .setAction("OK") {}
            .setActionTextColor(getColor(R.color.highlight_darkest))
            .setTextColor(getColor(R.color.neutral_dark_darkest))
    }

    /** Function */
    private fun performRestore(uri: Uri, overrideCurrent: Boolean) {
        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                qrStorage.restoreFromUri(uri, overrideCurrent)
            }
            if (success) {
                snackbar.setText("Khôi phục thành công").show()
                delay(1000)
                val restartIntent = packageManager.getLaunchIntentForPackage(packageName)
                restartIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(restartIntent)
                finishAffinity()
            } else {
                snackbar.setText("Khôi phục thất bại").show()
            }
        }
    }
}