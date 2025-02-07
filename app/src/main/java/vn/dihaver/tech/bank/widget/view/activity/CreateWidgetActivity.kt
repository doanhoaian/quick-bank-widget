package vn.dihaver.tech.bank.widget.view.activity

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.data.model.WidgetEntity
import vn.dihaver.tech.bank.widget.data.model.WidgetStyle
import vn.dihaver.tech.bank.widget.data.storage.WidgetStorage
import vn.dihaver.tech.bank.widget.databinding.ActivityCreateWidgetBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.CalculateUtils.dpToPixel
import vn.dihaver.tech.bank.widget.utils.IntentUtils.getParcelableSafe
import vn.dihaver.tech.bank.widget.utils.QrUtils
import vn.dihaver.tech.bank.widget.utils.SystemUtils
import vn.dihaver.tech.bank.widget.view.adapter.WidgetAdapter
import vn.dihaver.tech.bank.widget.viewmodel.CreateWidgetViewModel

class CreateWidgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateWidgetBinding

    private val viewModel: CreateWidgetViewModel by viewModels()

    private lateinit var widgetStorage: WidgetStorage
    private lateinit var widgetAdapter: WidgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCreateWidgetBinding.inflate(layoutInflater)
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

        /** Init Widget Storage
         */
        widgetStorage = WidgetStorage(this)
        widgetStorage.getAll().let {
            viewModel.updateWidgetList(it)
        }

        /** Init QrEntity Current
         */
        intent.getParcelableSafe<QrEntity>("qr_entity")?.let {
            viewModel.updateQrEntityCurrent(it)
        }

        /** Init Adapter
         */
        widgetAdapter =
            WidgetAdapter(this, emptyList(), object : WidgetAdapter.OnWidgetAdapterListener {
                override fun onClick(item: WidgetEntity) {
                    val intent =
                        Intent(this@CreateWidgetActivity, EditWidgetActivity::class.java).apply {
                            putExtra("code", 1)
                            putExtra("data", item)
                        }
                    editWidgetLauncher.launch(intent)
                }

                override fun onApply(item: WidgetEntity) {
                    snackbar.setText("Đã thêm Widget vào màn hình chính").show()
                }

                override fun onDelete(item: WidgetEntity) {
                    if (viewModel.deleteWidget(widgetStorage, item)) {
                        snackbar.setText("Đã xóa Widget: ${item.bankName} - ${item.bankNumber}").show()
                    }
                }
            })

    }

    private fun initView() {
        /** Recycler
         */
        binding.recyclerWidget.apply {
            layoutManager =
                LinearLayoutManager(this@CreateWidgetActivity, LinearLayoutManager.VERTICAL, true)
            adapter = widgetAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    val divider = ColorDrawable(getColor(R.color.neutral_light_darkest))
                    val dividerHeight = 1.dpToPixel(this@CreateWidgetActivity)
                    val childCount = parent.childCount
                    for (i in 0 until childCount - 1) {
                        val child = parent.getChildAt(i)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        val left = parent.paddingLeft
                        val right = parent.width - parent.paddingRight
                        val top = child.bottom + params.bottomMargin
                        val bottom = top + dividerHeight
                        divider.setBounds(left, top, right, bottom)
                        divider.draw(canvas)
                    }
                }
            })
        }

        /** Listener View
         */
        binding.includeWgS1.root.setOnClickListener {
            val qrEntity = viewModel.qrEntityCurrent.value!!
            val data = WidgetEntity(
                id = SystemUtils.generateId(),
                bankLogo = qrEntity.bankLogoRes,
                bankName = qrEntity.bankShortName,
                bankNumber = qrEntity.accNumber,
                cusWgStyle = WidgetStyle.BASIC_2X2,
                cusIsWgStroke = false,
                cusWgStrokeColor = "#FF000000",
                qrColor = qrEntity.cusQrColor,
                qrIcon = qrEntity.cusQrIconPath,
                qrContent = qrEntity.qrContent
            )
            val intent = Intent(this, EditWidgetActivity::class.java).apply {
                putExtra("code", 0)
                putExtra("data", data)
            }
            editWidgetLauncher.launch(intent)
        }
    }

    private fun obverseViewModel() {
        viewModel.qrEntityCurrent.observe(this) {
            val bitmapQr = QrUtils.createQrBitmap(
                context = this,
                qrContent = it.qrContent,
                cusQrColor = it.cusQrColor,
                cusQrIconPath = it.cusQrIconPath
            )

            binding.includeWgS1.imageQr.setImageBitmap(bitmapQr)
            binding.imageS2Qr.setImageBitmap(bitmapQr)
            binding.imageS3Qr.setImageBitmap(bitmapQr)

            val bitmapLogo = BitmapUtils.getBitmapFromResource(this, it.bankLogoRes)
            binding.imageS2Logo.setImageBitmap(bitmapLogo)
            binding.imageS3Logo.setImageBitmap(bitmapLogo)

            binding.textS3AccHolderName.text = it.accHolderName
            binding.textS3AccNumber.text = it.accNumber
        }

        viewModel.widgetList.observe(this) {
            widgetAdapter.update(it)
        }
    }

    /** Activity Result
     */
    private val editWidgetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val code = it.getIntExtra("code", -1)
                    it.getParcelableSafe<WidgetEntity>("data")?.let { data ->
                        when (code) {
                            0 -> {
                                if (viewModel.addWidget(widgetStorage, data)) {
                                    snackbar.setText("Đã thêm Widget vào màn hình chính").show()
                                }
                            }

                            1 -> {
                                if (viewModel.editWidget(widgetStorage, data)) {
                                    snackbar.setText("Đã sửa Widget").show()
                                }
                            }
                        }
                    }
                }
            }
        }

    /** Snackbar
     */
    private val snackbar: Snackbar by lazy {
        Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.neutral_light_lightest))
            .setAction("OK") {}
            .setActionTextColor(getColor(R.color.highlight_darkest))
            .setTextColor(getColor(R.color.neutral_dark_darkest))
    }

}