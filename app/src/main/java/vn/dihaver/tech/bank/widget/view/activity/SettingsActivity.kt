package vn.dihaver.tech.bank.widget.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.databinding.ActivitySettingsBinding
import vn.dihaver.tech.bank.widget.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_terms_conditions)))
            startActivity(intent)
        }

        binding.buttonPrivacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy_policy)))
            startActivity(intent)
        }
    }

    private fun obverseViewModel() {

    }
}