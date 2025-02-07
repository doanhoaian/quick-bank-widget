package vn.dihaver.tech.bank.widget.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.data.model.ThemeEntity
import vn.dihaver.tech.bank.widget.databinding.ActivityEditQrBinding
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.CalculateUtils
import vn.dihaver.tech.bank.widget.utils.FormatUtils.formatAccNumber
import vn.dihaver.tech.bank.widget.utils.ImagePickerHelper
import vn.dihaver.tech.bank.widget.utils.ImageUtils
import vn.dihaver.tech.bank.widget.utils.IntentUtils.getParcelableSafe
import vn.dihaver.tech.bank.widget.utils.QrProcess
import vn.dihaver.tech.bank.widget.utils.SystemUtils.translucentSystemBars
import vn.dihaver.tech.bank.widget.view.adapter.ColorAdapter
import vn.dihaver.tech.bank.widget.view.adapter.ThemeAdapter
import vn.dihaver.tech.bank.widget.viewmodel.EditQrViewModel


class EditQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditQrBinding

    private val viewModel: EditQrViewModel by viewModels()

    private lateinit var imagePickerHelper: ImagePickerHelper
    private lateinit var themeAdapter: ThemeAdapter
    private lateinit var colorAdapter: ColorAdapter

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

        /** Adapter Color
         */
        val colors = listOf(
            "#FF000000",
            "#FFC40002",
            "#FFC04301",
            "#FFC87C02",
            "#FF00B30A",
            "#FF00ACB6",
            "#FF0039C8",
            "#FF6400D7",
            "#FFB100C0",
            "#FF8E019B",
            "#FF002B9B",
            "#FF029199",
            "#FF009909",
            "#FF998002",
            "#FF996102",
            "#FF993503",
            "#FF990100"
        )
        colorAdapter = ColorAdapter(this, colors) { color: String ->
            viewModel.updateCusQrColor(color)
        }

        /** Adapter Theme
         */
        val themes = listOf(
            ThemeEntity("bg_not_have", "Không có", R.drawable.bg_not_have),

            ThemeEntity("bg_plain_yellow", "Vàng trơn", R.drawable.bg_plain_yellow),
            ThemeEntity("bg_plain_green", "Xanh lục trơn", R.drawable.bg_plain_green),
            ThemeEntity("bg_plain_blue", "Xanh dương trơn", R.drawable.bg_plain_blue),
            ThemeEntity("bg_plain_pink", "Hồng trơn", R.drawable.bg_plain_pink),
            ThemeEntity("bg_plain_brown", "Nâu trơn", R.drawable.bg_plain_brown),

            ThemeEntity("bg_rect_line_yellow", "Vàng đường thẳng", R.drawable.bg_rect_line_yellow),
            ThemeEntity(
                "bg_rect_line_green",
                "Xanh lục đường thẳng",
                R.drawable.bg_rect_line_green
            ),
            ThemeEntity(
                "bg_rect_line_blue",
                "Xanh dương đường thẳng",
                R.drawable.bg_rect_line_blue
            ),
            ThemeEntity("bg_rect_line_pink", "Hồng đường thẳng", R.drawable.bg_rect_line_pink),
            ThemeEntity("bg_rect_line_brown", "Nâu đường thẳng", R.drawable.bg_rect_line_brown),

            ThemeEntity(
                "bg_contour_line_yellow",
                "Vàng đường đồng mức",
                R.drawable.bg_contour_line_yellow
            ),
            ThemeEntity(
                "bg_contour_line_green",
                "Xanh lục đường đồng mức",
                R.drawable.bg_contour_line_green
            ),
            ThemeEntity(
                "bg_contour_line_blue",
                "Xanh dương đường đồng mức",
                R.drawable.bg_contour_line_blue
            ),
            ThemeEntity(
                "bg_contour_line_pink",
                "Hồng đường đồng mức",
                R.drawable.bg_contour_line_pink
            ),
            ThemeEntity(
                "bg_contour_line_brown",
                "Nâu đường đồng mức",
                R.drawable.bg_contour_line_brown
            ),

            ThemeEntity("bg_hexagon_yellow", "Vàng lục giác", R.drawable.bg_hexagon_yellow),
            ThemeEntity("bg_hexagon_green", "Xanh lục lục giác", R.drawable.bg_hexagon_green),
            ThemeEntity("bg_hexagon_blue", "Xanh dương lục giác", R.drawable.bg_hexagon_blue),
            ThemeEntity("bg_hexagon_pink", "Hồng lục giác", R.drawable.bg_hexagon_pink),
            ThemeEntity("bg_hexagon_brown", "Nâu lục giác", R.drawable.bg_hexagon_brown)
        )
        themeAdapter = ThemeAdapter(this, themes) { path: String ->
            viewModel.updateCusThemePath(path)
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

        binding.buttonLogoEmpty.setOnClickListener {
            viewModel.updateCusQrIconPath(
                BitmapUtils.convertNameToPath(
                    "bg_not_have",
                    BitmapUtils.PathType.RES
                )
            )
        }

        binding.buttonLogoNormal.setOnClickListener {
            viewModel.qrEntity.value?.bankIconRes?.let {
                viewModel.updateCusQrIconPath(
                    BitmapUtils.convertNameToPath(
                        it,
                        BitmapUtils.PathType.RES
                    )
                )
            }
        }

        binding.buttonLogoUserPhoto.setOnClickListener {
            viewModel.qrIconPath.value?.let {
                viewModel.updateCusQrIconPath(it)
            }
        }

        binding.buttonLogoAddPhoto.setOnClickListener {
            imagePickerHelper.pickImages()
        }

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

        /**
         */
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
        viewModel.qrEntity.observe(this) { qrEntity ->

            binding.imageLogoBank.setImageBitmap(
                BitmapUtils.getBitmapFromResource(
                    this,
                    qrEntity.bankLogoRes
                )
            )

            BitmapUtils.getBitmapFromResource(this, qrEntity.bankIconRes)?.let {
                binding.imageIconBank.setImageBitmap(it)
                binding.imageLogoNormal.setImageBitmap(it)
                Palette.from(it).generate { palette ->
                    palette?.let {
                        val color = palette.getVibrantColor(getColor(R.color.neutral_dark_darkest))
                        binding.containerBankInfo.apply {
                            setCardBackgroundColor(color)
                            strokeColor = color
                        }
                    }
                }
            }

            if (qrEntity.cusQrIconPath.contains("/Android/data/")) {
                viewModel.updateQrIconPath(qrEntity.cusQrIconPath)
            }

            binding.textTitleBank.text = qrEntity.bankShortName
            binding.textSubtitleBank.text = qrEntity.bankName
            binding.textAccountNumber.text = qrEntity.accNumber.formatAccNumber()
            binding.editTextAccHolderName.setText(qrEntity.accHolderName)
            binding.editTextAlias.setText(qrEntity.accAlias)

            themeAdapter.setThemeSelect(qrEntity.cusThemePath)
            colorAdapter.setColorSelect(qrEntity.cusQrColor)
        }

        viewModel.qrBitmap.observe(this) { qrBitmap ->
            binding.imageQr.setImageBitmap(qrBitmap)
        }

        viewModel.cusQrColor.observe(this) { _ ->
            val qrEntity = viewModel.getQrEntity()!!
            viewModel.updateQrBitmap(
                context = this,
                qrContent = qrEntity.qrContent,
                cusQrColor = qrEntity.cusQrColor,
                cusQrIconPath = qrEntity.cusQrIconPath
            )
        }

        viewModel.cusQrIconPath.observe(this) { _ ->
            val qrEntity = viewModel.getQrEntity()!!
            viewModel.updateQrBitmap(
                context = this,
                qrContent = qrEntity.qrContent,
                cusQrColor = qrEntity.cusQrColor,
                cusQrIconPath = qrEntity.cusQrIconPath
            )
            val (emptyColor, normalColor, userPhotoColor) = when {
                qrEntity.cusQrIconPath.contains("bg_not_have") ->
                    Triple(
                        R.color.highlight_darkest,
                        R.color.neutral_light_darkest,
                        R.color.neutral_light_darkest
                    )

                qrEntity.cusQrIconPath.startsWith("res://") ->
                    Triple(
                        R.color.neutral_light_darkest,
                        R.color.highlight_darkest,
                        R.color.neutral_light_darkest
                    )

                qrEntity.cusQrIconPath.contains("/Android/data/") ->
                    Triple(
                        R.color.neutral_light_darkest,
                        R.color.neutral_light_darkest,
                        R.color.highlight_darkest
                    )

                else -> return@observe
            }
            binding.buttonLogoEmpty.strokeColor = getColor(emptyColor)
            binding.buttonLogoNormal.strokeColor = getColor(normalColor)
            binding.buttonLogoUserPhoto.strokeColor = getColor(userPhotoColor)
        }

        viewModel.cusThemePath.observe(this) { _ ->
            val qrEntity = viewModel.getQrEntity()!!
            val bitmap = BitmapUtils.getBitmapFromPath(this, qrEntity.cusThemePath)
            binding.imageBackground.setImageBitmap(bitmap)
        }

        viewModel.qrIconPath.observe(this) { path ->
            binding.imageLogoUserPhoto.setImageBitmap(BitmapUtils.getBitmapFromPath(this, path))
        }

        viewModel.selectedTabIndex.observe(this) { position ->
            if (position == 1 && binding.recyclerTheme.adapter == null) {
                binding.recyclerTheme.apply {
                    layoutManager = GridLayoutManager(this@EditQrActivity, 3)
                    adapter = themeAdapter
                }
            } else if (position == 2 && binding.recyclerColor.adapter == null) {
                binding.recyclerColor.apply {
                    layoutManager = GridLayoutManager(
                        this@EditQrActivity,
                        CalculateUtils.calculateNoOfColumns(this@EditQrActivity, 60, 20)
                    )
                    adapter = colorAdapter
                }
            }
        }
    }

    /** Activity Result
     */


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