package vn.dihaver.tech.bank.widget.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.BodyShape
import vn.dihaver.tech.bank.widget.data.model.EyeBallShape
import vn.dihaver.tech.bank.widget.data.model.EyeFrameShape
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.databinding.ActivityEditQrBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.CalculateUtils
import vn.dihaver.tech.bank.widget.utils.FormatUtils.formatAccNumber
import vn.dihaver.tech.bank.widget.utils.ImagePickerHelper
import vn.dihaver.tech.bank.widget.utils.ImageUtils
import vn.dihaver.tech.bank.widget.utils.IntentUtils.getParcelableSafe
import vn.dihaver.tech.bank.widget.utils.QrCreator
import vn.dihaver.tech.bank.widget.utils.QrCustomizationData
import vn.dihaver.tech.bank.widget.utils.QrProcess
import vn.dihaver.tech.bank.widget.utils.SystemUtils.translucentSystemBars
import vn.dihaver.tech.bank.widget.view.adapter.ShapeQrAdapter
import vn.dihaver.tech.bank.widget.view.adapter.ThemeAdapter
import vn.dihaver.tech.bank.widget.view.bottomsheet.SelectColorBottomSheet
import vn.dihaver.tech.bank.widget.viewmodel.EditQrViewModel


class EditQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditQrBinding

    private val viewModel: EditQrViewModel by viewModels()

    private lateinit var qrCreator: QrCreator
    private lateinit var imagePickerHelper: ImagePickerHelper
    private lateinit var bodyShapeAdapter: ShapeQrAdapter
    private lateinit var eyeFrameShapeAdapter: ShapeQrAdapter
    private lateinit var eyeBallShapeAdapter: ShapeQrAdapter
    private lateinit var themeAdapter: ThemeAdapter

    /** Thông số tùy chỉnh Ratio giữa Preview & Edit
     */
    private val maxRatio = 0.7F
    private val minRatio = 0.3F

    private var initialHeightEdit = 0
    private var initialHeightPreview = 0
    private var initialTouchY = 0

    /**
     */

    private var codeEdit = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.translucentSystemBars(isStatus = true, isNavigation = true)

        binding = ActivityEditQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupInsets()
        handlerQrEntity()
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

        /** Init QrCreator
         */
        qrCreator = QrCreator(this)

        /** Init ImagePickerHelper
         */
        imagePickerHelper = ImagePickerHelper(activity = this, allowMultiple = false) { uris ->
            val uri = uris.firstOrNull()
            uri?.let {
                ImageUtils.saveImageToAppStorage(this, uri, 5)?.let {
                    binding.buttonLogoUserPhoto.strokeColor =
                        getColor(R.color.neutral_light_darkest)
                    viewModel.updateQrIconPath(it)
                }
            }
        }

        /** Init AdapterTheme
         */
        themeAdapter = ThemeAdapter(this, QrCustomizationData.themes) {
            viewModel.updateCusThemePath(it)
        }

        /** Init AdapterShapeQr */
        bodyShapeAdapter = ShapeQrAdapter(this, QrCustomizationData.bodyShapes) { item ->
            viewModel.updateCusQrEntity {
                it.copy(qrBodyDarkShape = item.bodyShape as BodyShape)
            }
        }

        eyeFrameShapeAdapter = ShapeQrAdapter(this, QrCustomizationData.eyeFrameShapes) { item ->
            viewModel.updateCusQrEntity {
                it.copy(qrEyeFrameShape = item.eyeFrameShape as EyeFrameShape)
            }
        }

        eyeBallShapeAdapter = ShapeQrAdapter(this, QrCustomizationData.eyeBallShapes) { item ->
            viewModel.updateCusQrEntity {
                it.copy(qrEyeBallShape = item.eyeBallShape as EyeBallShape)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.editTextAlias.textCursorDrawable?.setTint(getColor(R.color.highlight_darkest))
        }

        /** Listener View
         */
        binding.buttonChangeRatio.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialTouchY = event.rawY.toInt()
                    initialHeightPreview = binding.containerPreview.height
                    initialHeightEdit = binding.containerEdit.height
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.rawY.toInt() - initialTouchY
                    val newHeightPreview = initialHeightPreview + deltaY
                    val newHeightEdit = initialHeightEdit - deltaY
                    val totalHeight = newHeightPreview + newHeightEdit
                    val minHeightPreview = (totalHeight * minRatio).toInt()
                    val maxHeightPreview = (totalHeight * maxRatio).toInt()
                    if (newHeightPreview in minHeightPreview..maxHeightPreview && newHeightEdit >= 0) {
                        val layoutParamsPreview = binding.containerPreview.layoutParams
                        layoutParamsPreview.height = newHeightPreview
                        binding.containerPreview.layoutParams = layoutParamsPreview
                        val layoutParamsEdit = binding.containerEdit.layoutParams
                        layoutParamsEdit.height = newHeightEdit
                        binding.containerEdit.layoutParams = layoutParamsEdit
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    true
                }

                else -> false
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.updateSelectTabIndex(tab?.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            if (binding.editTextAccHolderName.text.toString().isEmpty()) {
                if (binding.tabLayout.selectedTabPosition != 0) {
                    val tab = binding.tabLayout.getTabAt(0)
                    tab?.select()
                }
                binding.editTextAccHolderName.apply {
                    requestFocus()
                    error = "Nhập tên chủ tài khoản"
                }
                return@setOnClickListener
            }

            viewModel.getQrEntity()?.let {
                val intent = Intent().apply {
                    putExtra("code", codeEdit)
                    putExtra("qr_entity", it)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        /** Tab Account
         */
        binding.editTextAccHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.updateAccHolderName(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.editTextAlias.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.updateAccAlias(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        /** Tab QR Code
         */
        binding.buttonCusColorBody.setOnClickListener {
            showSelectColorBottomSheet(0, binding.textCusColorBody, binding.cardCusColorBody)
        }

        binding.buttonCusColorEyeFrame.setOnClickListener {
            showSelectColorBottomSheet(
                1,
                binding.textCusColorEyeFrame,
                binding.cardCusColorEyeFrame
            )
        }

        binding.buttonCusColorEyeBall.setOnClickListener {
            showSelectColorBottomSheet(2, binding.textCusColorEyeBall, binding.cardCusColorEyeBall)
        }

//        binding.buttonLogoEmpty.setOnClickListener {
//            viewModel.updateCusQrIconPath(
//                BitmapUtils.convertNameToPath(
//                    "bg_not_have",
//                    BitmapUtils.PathType.RES
//                )
//            )
//        }
//
//        binding.buttonLogoNormal.setOnClickListener {
//            viewModel.qrEntity.value?.bankIconRes?.let {
//                viewModel.updateCusQrIconPath(
//                    BitmapUtils.convertNameToPath(
//                        it,
//                        BitmapUtils.PathType.RES
//                    )
//                )
//            }
//        }
//
//        binding.buttonLogoUserPhoto.setOnClickListener {
//            viewModel.qrIconPath.value?.let {
//                viewModel.updateCusQrIconPath(it)
//            }
//        }

        binding.buttonLogoAddPhoto.setOnClickListener {
            imagePickerHelper.pickImages()
        }
    }

    /**
     * Xử lí dữ liệu QrEntity khởi tạo dữ liệu Activity
     */
    private fun handlerQrEntity() {
        val qrEntity = intent.getParcelableSafe<QrEntity>("qr_entity")
        val qrContent = intent.getStringExtra("qr_content")

        when {
            qrEntity != null -> {
                codeEdit = 1
                viewModel.setQrEntity(qrEntity)
            }

            qrContent != null -> {
                codeEdit = 0
                viewModel.setQrEntity(QrProcess.extraQrBank(qrContent))
            }

            else -> {
                Toast.makeText(
                    this,
                    getString(R.string.message_error_try_again),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun observeViewModel() {
        viewModel.qrEntity.observe(this) {

            binding.imageLogoBank.setImageBitmap(
                BitmapUtils.getBitmapFromResource(
                    this,
                    it.bankLogoRes
                )
            )

            BitmapUtils.getBitmapFromResource(this, it.bankIconRes)?.let { bitmapBI ->
                binding.imageIconBank.setImageBitmap(bitmapBI)
                binding.imageLogoNormal.setImageBitmap(bitmapBI)
                Palette.from(bitmapBI).generate { palette ->
                    palette?.let {
                        val color = palette.getVibrantColor(getColor(R.color.neutral_dark_darkest))
                        binding.containerBankInfo.apply {
                            setCardBackgroundColor(color)
                            strokeColor = color
                        }
                    }
                }
            }

            binding.textTitleBank.text = it.bankShortName
            binding.textSubtitleBank.text = it.bankName
            binding.textAccountNumber.text = it.accNumber.formatAccNumber()
            binding.editTextAccHolderName.setText(it.accHolderName)
            binding.editTextAlias.setText(it.accAlias)

            binding.cardCusColorBody.setCardBackgroundColor(Color.parseColor(it.cusQrEntity.qrBodyDarkColor))
            binding.cardCusColorEyeFrame.setCardBackgroundColor(Color.parseColor(it.cusQrEntity.qrEyeFrameColor))
            binding.cardCusColorEyeBall.setCardBackgroundColor(Color.parseColor(it.cusQrEntity.qrEyeBallColor))

            bodyShapeAdapter.setShapeQrSelect(it.cusQrEntity.qrBodyDarkShape)
            eyeFrameShapeAdapter.setShapeQrSelect(it.cusQrEntity.qrEyeFrameShape)
            eyeBallShapeAdapter.setShapeQrSelect(it.cusQrEntity.qrEyeBallShape)
            themeAdapter.setThemeSelect(it.cusThemePath)
        }

        viewModel.cusQrEntity.observe(this) {
            val dataQr = QrData.Text(viewModel.qrEntity.value?.qrContent!!)
            val drawableQr = qrCreator.createDrawable(dataQr, it)
            binding.imageQr.setImageDrawable(drawableQr)
        }

        viewModel.cusThemePath.observe(this) {
            val bitmap = BitmapUtils.getBitmapFromPath(this, it)
            binding.imageBackground.setImageBitmap(bitmap)
        }

        viewModel.selectedTabIndex.observe(this) { position ->
            if (position == 1 && binding.recyclerShapeBody.adapter == null) {
                binding.recyclerShapeBody.apply {
                    layoutManager = GridLayoutManager(
                        this@EditQrActivity,
                        CalculateUtils.calculateNoOfColumns(this@EditQrActivity, 70, 20)
                    )
                    adapter = bodyShapeAdapter
                }

                binding.recyclerShapeEyeFrame.apply {
                    layoutManager = GridLayoutManager(
                        this@EditQrActivity,
                        CalculateUtils.calculateNoOfColumns(this@EditQrActivity, 70, 20)
                    )
                    adapter = eyeFrameShapeAdapter
                }

                binding.recyclerShapeEyeBall.apply {
                    layoutManager = GridLayoutManager(
                        this@EditQrActivity,
                        CalculateUtils.calculateNoOfColumns(this@EditQrActivity, 70, 20)
                    )
                    adapter = eyeBallShapeAdapter
                }
            }
            if (position == 2 && binding.recyclerTheme.adapter == null) {
                binding.recyclerTheme.apply {
                    layoutManager = GridLayoutManager(this@EditQrActivity, 3)
                    adapter = themeAdapter
                }
            }
        }
    }

    /** Bottom Sheet
     */
    private val selectColorBottomSheet by lazy {
        SelectColorBottomSheet(this@EditQrActivity) { id, color ->
            when (id) {
                0 -> {
                    binding.cardCusColorBody.setCardBackgroundColor(Color.parseColor(color))
                    viewModel.updateCusQrEntity { it.copy(qrBodyDarkColor = color) }
                }

                1 -> {
                    binding.cardCusColorEyeFrame.setCardBackgroundColor(Color.parseColor(color))
                    viewModel.updateCusQrEntity { it.copy(qrEyeFrameColor = color) }
                }

                2 -> {
                    binding.cardCusColorEyeBall.setCardBackgroundColor(Color.parseColor(color))
                    viewModel.updateCusQrEntity { it.copy(qrEyeBallColor = color) }
                }
            }
        }
    }

    /** Function
     */
    private fun showSelectColorBottomSheet(
        id: Int,
        textView: TextView,
        cardView: MaterialCardView
    ) {
        selectColorBottomSheet.show()
        val colorInt = cardView.cardBackgroundColor.defaultColor
        val hexColor = String.format("#%08X", colorInt)
        selectColorBottomSheet.setStatus(id, textView.text.toString(), hexColor)
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