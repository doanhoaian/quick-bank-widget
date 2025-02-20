package vn.dihaver.tech.bank.widget.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.data.storage.QrStorage
import vn.dihaver.tech.bank.widget.databinding.ActivityMainBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.CalculateUtils.dpToPixel
import vn.dihaver.tech.bank.widget.utils.FormatUtils.formatAccNumber
import vn.dihaver.tech.bank.widget.utils.FormatUtils.formatMoneyDong
import vn.dihaver.tech.bank.widget.utils.ImagePickerHelper
import vn.dihaver.tech.bank.widget.utils.ImageUtils
import vn.dihaver.tech.bank.widget.utils.InAppReviewHelper
import vn.dihaver.tech.bank.widget.utils.IntentUtils.getParcelableSafe
import vn.dihaver.tech.bank.widget.utils.QrCreator
import vn.dihaver.tech.bank.widget.utils.QrDecoder
import vn.dihaver.tech.bank.widget.utils.QrProcess
import vn.dihaver.tech.bank.widget.utils.SystemUtils.applyInsets
import vn.dihaver.tech.bank.widget.view.bottomsheet.AddMoneyBottomSheet
import vn.dihaver.tech.bank.widget.view.bottomsheet.MoreQrBottomSheet
import vn.dihaver.tech.bank.widget.view.bottomsheet.SelectQrBottomSheet
import vn.dihaver.tech.bank.widget.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var qrStorage: QrStorage
    private lateinit var qrCreator: QrCreator
    private lateinit var qrDecoder: QrDecoder
    private lateinit var imagePickerHelper: ImagePickerHelper

    private var adBanner: AdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInsets()
        initComponent()
        initView()
        initAds()
        observeViewModel()

        handleWidgetIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        adBanner?.resume()
    }

    override fun onPause() {
        super.onPause()
        adBanner?.pause()
    }

    private fun setupInsets() {
        binding.main.applyInsets(topPadding = 0, bottomPadding = 0)
        binding.overlay.applyInsets()
        binding.content.applyInsets()
    }

    private fun initAds() {
        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        adBanner = binding.adView
        adBanner?.loadAd(adRequest)

    }

    private fun initComponent() {

        /** Init QrStorage */
        qrStorage = QrStorage(this)
        val listQr = qrStorage.getAll()
        viewModel.updateQrList(listQr)

        if (listQr.isNotEmpty()) {
            qrStorage.getItemCurrent()?.let {
                viewModel.updateQrEntityCurrent(it)
            } ?: run {
                viewModel.updateQrEntityCurrent(listQr[listQr.size - 1])
            }
        }

        /** Init QrCreator */
        qrCreator = QrCreator(this)

        /** Init QrDecoder */
        qrDecoder = QrDecoder(this)

        /** Init ImagePickerHelper */
        imagePickerHelper = ImagePickerHelper(activity = this, allowMultiple = false) { uris ->
            val uri = uris.firstOrNull()
            uri?.let {
                qrDecoder.decodeToString(uri) { qrContent ->
                    if (qrContent == null) {
                        showDialogErrorInputQr()
                        return@decodeToString
                    }

                    if (!QrProcess.isQrBank(qrContent)) {
                        showDialogErrorInputQr()
                        return@decodeToString
                    }

                    val intent = Intent(this, EditQrActivity::class.java).apply {
                        putExtra("qr_content", qrContent)
                    }
                    editQrLauncher.launch(intent)
                }
            }
        }

        /** Init InAppReviewHelper */
        InAppReviewHelper(this).launchReviewFlow(this) { result ->
            when(result) {
                is InAppReviewHelper.ReviewResult.AlreadyReviewed -> {
                    // Người dùng đã review trước đó
                }
                is InAppReviewHelper.ReviewResult.Launched -> {
                    // Review flow đã được khởi chạy thành công
                }
                is InAppReviewHelper.ReviewResult.Failed -> {
                    // Không thể khởi chạy review flow (có thể hiển thị fallback)
                }
                is InAppReviewHelper.ReviewResult.NotEligible -> {
                    // Số lần mở ứng dụng không nằm trong khoảng 3 đến 5 nên không hiển thị review
                }
            }
        }

    }

    private fun initView() {

        /** Listener View */
        binding.buttonMoreQr.setOnClickListener {
            moreQrBottomSheet.show()
        }

        binding.containerQr.setOnLongClickListener {
            moreQrBottomSheet.show()
            true
        }

        binding.buttonEditQr.setOnClickListener {
            openEditQrActivity()
        }

        binding.buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.buttonAddMoney.setOnClickListener {
            addMoneyBottomSheet.show()
        }

        binding.buttonChangeProfile.setOnClickListener {
            selectQrBottomSheet.show()
        }

        binding.buttonCreateWidget.setOnClickListener {
            openCreateWidget()
        }

        binding.containerEmpty.setOnClickListener {
            if (!binding.fabExpandMenuButton.isExpanded) {
                binding.fabExpandMenuButton.expand()
            } else {
                binding.fabExpandMenuButton.collapse()
            }
        }

        binding.fabButtonAddImage.setOnClickListener {
            imagePickerHelper.pickImages()
            binding.fabExpandMenuButton.collapse()
        }

        binding.fabButtonAddScan.setOnClickListener {
            val intent = Intent(this, ScanQrActivity::class.java)
            scanQrLauncher.launch(intent)
            binding.fabExpandMenuButton.collapse()
        }

        binding.fabButtonAddCreate.setOnClickListener {
            val intent = Intent(this, CreateQrActivity::class.java)
            createQrLauncher.launch(intent)
            binding.fabExpandMenuButton.collapse()
        }
    }

    private fun observeViewModel() {

        viewModel.qrList.observe(this) { qrList ->

            @SuppressLint("SetTextI18n")
            binding.textCountProfile.text = qrList.size.toString()
        }

        viewModel.qrEntityCurrent.observe(this) {

            qrStorage.updateItemCurrent(it.id)
            /** Text */
            if (it.accAlias.isNotEmpty()) {
                binding.textAlias.apply {
                    visibility = View.VISIBLE
                    text = it.accAlias
                }
            } else {
                binding.textAlias.visibility = View.GONE
            }
            binding.textAccountName.text = it.accHolderName
            binding.textAccountNumber.text = it.accNumber.formatAccNumber()

            formatTextMoney(0, "")

            /** Bitmap */
            val dataQr = QrData.Text(it.qrContent)
            val drawableQr = qrCreator.createDrawable(dataQr, it.cusQrEntity)
            binding.imageQr.setImageDrawable(drawableQr)
            binding.containerQr.setCardBackgroundColor(Color.parseColor(it.cusQrEntity.qrBackgroundColor))

            binding.imageBackground.setImageBitmap(
                BitmapUtils.getBitmapFromPath(
                    this,
                    it.cusThemePath
                )
            )
            binding.imageLogoBank.setImageBitmap(
                BitmapUtils.getBitmapFromResource(
                    this,
                    it.bankLogoRes
                )
            )

        }
    }


    /** Activity Result */
    private val editQrLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    handlerQrEntityResult(
                        it.getIntExtra("code", -1),
                        it.getParcelableSafe<QrEntity>("qr_entity")
                    )
                }
            }
        }

    private val scanQrLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    handlerQrEntityResult(
                        it.getIntExtra("code", -1),
                        it.getParcelableSafe<QrEntity>("qr_entity")
                    )
                }
            }
        }

    private val createQrLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    handlerQrEntityResult(
                        it.getIntExtra("code", -1),
                        it.getParcelableSafe<QrEntity>("qr_entity")
                    )
                }
            }
        }

    /** BottomSheet */
    private val moreQrBottomSheet: MoreQrBottomSheet by lazy {
        MoreQrBottomSheet(this, object : MoreQrBottomSheet.MoreQrBottomSheetListener {
            override fun onWidget() {
                openCreateWidget()
            }

            override fun onEdit() {
                openEditQrActivity()
            }

            override fun onShare() {
                handlerTakeScreenShot(false)
            }

            override fun onDownload() {
                handlerTakeScreenShot(true)
            }

            override fun onDelete() {
                viewModel.getQrEntityCurrent()?.let {
                    if (viewModel.deleteQrEntity(it.id, qrStorage)) {
                        val qrList = qrStorage.getAll()
                        if (qrList.isNotEmpty()) {
                            viewModel.updateQrEntityCurrent(qrList[qrList.size - 1])
                        }
                        snackbar.setText("Đã xóa QR: ${it.bankShortName} - ${it.accNumber}").show()
                    }
                }
            }
        })
    }

    private val selectQrBottomSheet: SelectQrBottomSheet by lazy {
        SelectQrBottomSheet(this, viewModel) { item ->
            if (item.id != viewModel.qrEntityCurrent.value!!.id) {
                viewModel.updateQrEntityCurrent(item)
            }
        }
    }

    private val addMoneyBottomSheet: AddMoneyBottomSheet by lazy {
        AddMoneyBottomSheet(
            this,
            viewModel,
            object : AddMoneyBottomSheet.AddMoneyBottomSheetListener {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onSuccess(
                    qrContent: String,
                    qrEntity: QrEntity,
                    money: Long,
                    content: String
                ) {
                    val dataQr = QrData.Text(qrContent)
                    val drawableQr = qrCreator.createDrawable(dataQr, qrEntity.cusQrEntity)
                    binding.imageQr.setImageDrawable(drawableQr)
                    formatTextMoney(money, content)
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onError(qrEntity: QrEntity) {
                    val dataQr = QrData.Text(qrEntity.qrContent)
                    val drawableQr = qrCreator.createDrawable(dataQr, qrEntity.cusQrEntity)
                    binding.imageQr.setImageDrawable(drawableQr)
                    formatTextMoney(0, "")
                }
            })
    }

    /** Dialog */
    private fun showDialogErrorInputQr() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Thông báo")
            .setMessage("Mã QR không đúng định dạng. Vui lòng thử lại.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDialogErrorDuplicateQr(qrEntity: QrEntity?) {
        qrEntity?.let {
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Đã tồn tại tài khoản")
                .setMessage("- NH: ${it.bankShortName}\n- STK: ${it.accNumber}\nBạn có muốn thêm nữa không?")
                .setNeutralButton("Vẫn thêm") { _, _ ->
                    if (viewModel.addQrEntity(it, qrStorage, true)) {
                        viewModel.updateQrEntityCurrent(it)
                        snackbar.setText("Đã thêm QR mới").show()
                    }
                }
                .setPositiveButton("Hủy") { _, _ -> }
                .show()
        }
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
    private fun openCreateWidget() {
        val intent = Intent(this, CreateWidgetActivity::class.java).apply {
            putExtra("qr_entity", viewModel.getQrEntityCurrent())
        }
        startActivity(intent)
    }

    private fun formatTextMoney(money: Long, content: String) {
        binding.buttonAddMoney.apply {
            text = if (money > 0) money.formatMoneyDong() else "Thêm số tiền"
            setTextColor(if (money > 0) getColor(R.color.highlight_darkest) else getColor(R.color.neutral_dark_medium))
            if (money > 0) setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(this@MainActivity, R.drawable.svg_fonts_edit),
                null
            )
            else setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this@MainActivity, R.drawable.svg_fonts_add),
                null,
                null,
                null
            )
        }
        binding.textBankContent.apply {
            visibility = if (content.isNotEmpty()) View.VISIBLE else View.GONE
            text = content
        }
    }

    private fun handlerQrEntityResult(code: Int?, qrEntity: QrEntity?) {
        qrEntity?.let {
            when (code) {
                0 -> {
                    if (viewModel.addQrEntity(it, qrStorage)) {
                        viewModel.updateQrEntityCurrent(it)
                        snackbar.setText("Đã thêm QR mới").show()
                    } else {
                        showDialogErrorDuplicateQr(it)
                    }
                }

                1 -> {
                    if (viewModel.editQrEntity(it, qrStorage)) {
                        viewModel.updateQrEntityCurrent(it)
                        snackbar.setText("Đã sửa QR").show()
                    }
                }
            }
        }
    }

    private fun openEditQrActivity() {
        val intent = Intent(this, EditQrActivity::class.java).apply {
            putExtra("qr_entity", viewModel.getQrEntityCurrent())
        }
        editQrLauncher.launch(intent)
    }

    private fun handleWidgetIntent(intent: Intent) {
        intent.getStringExtra("qr_entity_id")?.let {
            val qrEntity = qrStorage.getById(it)
            if (qrEntity != null) {
                viewModel.updateQrEntityCurrent(qrEntity)
            }
        }
    }

    private fun handlerTakeScreenShot(isSave: Boolean) {
        viewModel.getQrEntityCurrent()?.cusThemePath?.let { pathBG ->
            val bitmapBG =
                BitmapUtils.getBitmapFromPath(this, pathBG) ?: BitmapUtils.getBitmapFromResource(
                    this,
                    "bg_not_have"
                )
            val isAddMoneyEmpty = binding.buttonAddMoney.text.contains("Thêm số tiền")
            val isBankContentNotEmpty = binding.textBankContent.text.isNotEmpty()
            binding.containerQr.strokeWidth = 1.dpToPixel(this@MainActivity)
            if (isAddMoneyEmpty && !isBankContentNotEmpty) {
                binding.containerAddMoney.visibility = View.GONE
            } else {
                binding.buttonAddMoney.apply {
                    visibility = if (isAddMoneyEmpty) View.GONE else View.VISIBLE
                    if (!isAddMoneyEmpty) {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                }
            }
            binding.containerQr.post {
                ImageUtils.takeScreenshot(
                    context = this@MainActivity,
                    view = binding.contentQr,
                    bitmapBG = bitmapBG,
                    isSave = isSave
                )
                binding.containerQr.strokeWidth = 0
                if (isAddMoneyEmpty && !isBankContentNotEmpty) {
                    binding.containerAddMoney.visibility = View.VISIBLE
                } else {
                    binding.buttonAddMoney.apply {
                        visibility = View.VISIBLE
                        if (!isAddMoneyEmpty) {
                            setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.svg_fonts_edit),
                                null
                            )
                        }
                    }
                }
                if (isSave) {
                    snackbar.setText("Đã lưu ảnh QR vào thư viện").show()
                }
            }
        }
    }

}