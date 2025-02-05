package vn.dihaver.tech.bank.widget.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeDiacritics
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeExtraSpaces
import vn.dihaver.tech.bank.widget.utils.QrUtils

class EditQrViewModel: ViewModel() {

    private val _qrEntity = MutableLiveData<QrEntity>()
    val qrEntity: LiveData<QrEntity> = _qrEntity

    fun setQrEntity(qrEntity: QrEntity) {
        _qrEntity.value = qrEntity
        _accHolderName.value = qrEntity.accHolderName
        _accAlias.value = qrEntity.accAlias
        _cusQrColor.value = qrEntity.cusQrColor
        _cusQrIconPath.value = qrEntity.cusQrIconPath
        _cusThemePath.value = qrEntity.cusThemePath
    }

    fun getQrEntity(): QrEntity? {
        return _qrEntity.value?.copy(
            accHolderName = _accHolderName.value!!.removeDiacritics().removeExtraSpaces().uppercase(),
            accAlias = _accAlias.value!!,
            cusQrColor = _cusQrColor.value!!,
            cusQrIconPath = _cusQrIconPath.value!!,
            cusThemePath = _cusThemePath.value!!
        )
    }

    /**
     */
    private val _qrBitmap = MutableLiveData<Bitmap>()
    val qrBitmap: LiveData<Bitmap> = _qrBitmap

    fun updateQrBitmap(context: Context, qrContent: String, cusQrColor: String, cusQrIconPath: String) {
        val qrBitmap = QrUtils.generateQrBitmap(qrContent, Color.parseColor(cusQrColor), Color.WHITE)
        if (!cusQrIconPath.contains("bg_not_have")) {
            val iconBitmap = BitmapUtils.getBitmapFromPath(context, cusQrIconPath)
            QrUtils.addLogoToQr(qrBitmap!!, iconBitmap, if (cusQrIconPath.startsWith("res://")) 4 else 0)
        }

        _qrBitmap.value = qrBitmap
    }

    /**
     */

    private val _accHolderName = MutableLiveData<String>()
    val accHolderName: LiveData<String> = _accHolderName

    fun updateAccHolderName(string: String) {
        _accHolderName.value = string
    }

    /**
     */

    private val _accAlias = MutableLiveData<String>()
    val accAlias: LiveData<String> = _accAlias

    fun updateAccAlias(string: String) {
        _accAlias.value = string
    }

    /**
     */

    private val _cusQrColor = MutableLiveData<String>()
    val cusQrColor: LiveData<String> = _cusQrColor

    fun updateCusQrColor(color: String) {
        _cusQrColor.value = color
    }

    /**
     */

    private val _cusQrIconPath = MutableLiveData<String>()
    val cusQrIconPath: LiveData<String> = _cusQrIconPath

    fun updateCusQrIconPath(string: String) {
        _cusQrIconPath.value = string
    }

    /**
     */

    private val _cusThemePath = MutableLiveData<String>()
    val cusThemePath: LiveData<String> = _cusThemePath

    fun updateCusThemePath(string: String) {
        _cusThemePath.value = string
    }

    /**
     */

    private val _selectedTabIndex = MutableLiveData<Int>().apply { value = 0 }
    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex

    fun updateSelectTabIndex(position: Int?) {
        _selectedTabIndex.value = position
    }

    /**
     */
    private val _qrIconPath = MutableLiveData("")
    val qrIconPath: LiveData<String> = _qrIconPath
    val isQrIconPathEmpty: LiveData<Boolean> = _qrIconPath.map { it.isNullOrEmpty() }

    fun getQrIconPath(): String? {
        return _qrIconPath.value
    }

    fun updateQrIconPath(string: String) {
        _qrIconPath.value = string
    }

}