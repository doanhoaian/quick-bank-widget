package vn.dihaver.tech.bank.widget.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.alexzhirkevich.customqrgenerator.QrData
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
import vn.dihaver.tech.bank.widget.utils.QrCreator
import vn.dihaver.tech.bank.widget.view.adapter.WidgetAdapter
import vn.dihaver.tech.bank.widget.view.widget.QrAdvancedWidget
import vn.dihaver.tech.bank.widget.view.widget.QrSimpleWidget
import vn.dihaver.tech.bank.widget.viewmodel.CreateWidgetViewModel


class CreateWidgetActivity : AppCompatActivity() {

    companion object {
        const val ACTION_WIDGET_PINNED = "vn.dihaver.tech.bank.widget.ACTION_WIDGET_PINNED"
    }

    private lateinit var binding: ActivityCreateWidgetBinding

    private val viewModel: CreateWidgetViewModel by viewModels()

    private lateinit var widgetStorage: WidgetStorage
    private lateinit var widgetAdapter: WidgetAdapter
    private lateinit var qrCreator: QrCreator

    private var appWidgetIds: IntArray? = null

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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

        /** Đăng ký Receiver */
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(ACTION_WIDGET_PINNED))
    }

    override fun onDestroy() {
        super.onDestroy()
        /** Hủy đăng ký Receiver */
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(broadcastReceiver)
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

        /** Init QrCreator
         */
        qrCreator = QrCreator(this)

        /** Init QrEntity Current
         */
        intent.getParcelableSafe<QrEntity>("qr_entity")?.let {
            viewModel.updateQrEntityCurrent(it)
        }

        /** Init Adapter
         */
        widgetAdapter =
            WidgetAdapter(
                this,
                emptyList(),
                qrCreator,
                object : WidgetAdapter.OnWidgetAdapterListener {
                    override fun onClick(item: WidgetEntity) {

                    }

                    override fun onDelete(item: WidgetEntity) {
                        if (viewModel.deleteWidget(widgetStorage, item)) {
                            snackbar.setText("Đã xóa Widget: ${item.qrEntity.bankShortName} - ${item.qrEntity.accNumber}")
                                .show()
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
                override fun onDraw(
                    canvas: Canvas,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val divider = ColorDrawable(getColor(R.color.neutral_light_darkest))
                    val dividerHeight = 1.dpToPixel(this@CreateWidgetActivity)
                    val childCount = parent.childCount
                    for (i in 1 until childCount) {
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
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonFaq.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_fab)))
            startActivity(intent)
        }

        binding.includeWgSimple.root.setOnClickListener {
            showDialogConfirm(QrSimpleWidget::class.java)
        }

        binding.includeWgAdvanced.root.setOnClickListener {
            showDialogConfirm(QrAdvancedWidget::class.java)
        }

    }

    private fun obverseViewModel() {
        viewModel.qrEntityCurrent.observe(this) {

            val padding = 10.dpToPixel(this)
            val dataQr = QrData.Text(it.qrContent)
            val drawableQr = qrCreator.createDrawable(dataQr, it.cusQrEntity)
            val bgColor = ColorStateList.valueOf(Color.parseColor(it.cusQrEntity.qrBackgroundColor))
            val bitmapLogo = BitmapUtils.getBitmapFromResource(this, it.bankLogoRes)
            val textAlias = it.accAlias

            /** Widget Style Simple */
            binding.includeWgSimple.main.backgroundTintList = bgColor
            binding.includeWgSimple.imageQr.setImageDrawable(drawableQr)
            binding.includeWgSimple.imageQr.setPadding(padding, padding, padding, padding)

            /** Widget Style Advanced */
            binding.includeWgAdvanced.main.backgroundTintList = bgColor
            binding.includeWgAdvanced.imageQr.setImageDrawable(drawableQr)
            binding.includeWgAdvanced.imageQr.setPadding(padding, padding, padding, padding)

            binding.includeWgAdvanced.imageLogoBank.visibility = View.VISIBLE
            binding.includeWgAdvanced.textHolderName.visibility = View.VISIBLE
            binding.includeWgAdvanced.textNumber.visibility = View.VISIBLE
            if (textAlias.isNotEmpty()) {
                binding.includeWgAdvanced.textAlias.text = textAlias
                binding.includeWgAdvanced.textAlias.visibility = View.VISIBLE
            }
            binding.includeWgAdvanced.textHolderName.text = it.accHolderName
            binding.includeWgAdvanced.textNumber.text = it.accNumber
            binding.includeWgAdvanced.imageLogoBank.setImageBitmap(bitmapLogo)
        }

        viewModel.widgetList.observe(this) {
            widgetAdapter.update(it)
        }
    }

    /** Broadcast Receiver */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context == null) return
            if (intent.action == ACTION_WIDGET_PINNED) {
                intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)?.let { appWidgetIds ->
                    intent.getStringExtra("style")?.let { style ->
                        if (!this@CreateWidgetActivity.appWidgetIds.contentEquals(appWidgetIds)) {
                            val appWidgetId = appWidgetIds.last()
                            when (style) {
                                "simple" -> saveWidgetAndNotify(appWidgetId, WidgetStyle.SIMPLE_2X2, QrSimpleWidget::class.java)
                                "advanced" -> saveWidgetAndNotify(appWidgetId, WidgetStyle.ADVANCED_4X2, QrAdvancedWidget::class.java)
                            }
                        }
                    }
                }
            }
        }
    }

    /** Dialog */
    private fun showDialogConfirm(widgetClass: Class<out AppWidgetProvider>) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có muốn tạo Widget này không?")
            .setPositiveButton("Tiếp tục") { _, _ ->
                requestWidgetCreation(widgetClass)
            }
            .setNegativeButton("Hủy", null)
            .show()
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
    private fun saveWidgetAndNotify(appWidgetId: Int, widgetStyle: WidgetStyle, widgetClass: Class<out AppWidgetProvider>) {
        val qrEntity = viewModel.qrEntityCurrent.value ?: return
        val widgetEntity = WidgetEntity(
            id = appWidgetId.toString(),
            widgetStyle = widgetStyle,
            qrEntity = qrEntity
        )

        if (viewModel.addWidget(widgetStorage, widgetEntity)) {
            val updateIntent = Intent(this, widgetClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
            }
            sendBroadcast(updateIntent)

            snackbar.setText("Widget đã được tạo thành công!").show()
        }
    }

    private fun requestWidgetCreation(widgetClass: Class<out AppWidgetProvider>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val provider = ComponentName(this, widgetClass)

            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                appWidgetManager.requestPinAppWidget(provider, null, null)
            } else {
                snackbar.setText("Thiết bị không hỗ trợ ghim Widget!").show()
            }
        } else {
            snackbar.setText("Thiết bị không hỗ trợ ghim Widget!").show()
        }
    }
}