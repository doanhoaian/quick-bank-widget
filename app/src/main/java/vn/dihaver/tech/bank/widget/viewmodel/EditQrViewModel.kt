package vn.dihaver.tech.bank.widget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.dihaver.tech.bank.widget.data.model.CustomQrEntity
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeDiacritics
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeExtraSpaces

class EditQrViewModel : ViewModel() {

    /** LiveData - QrEntity(main)
     */
    private val _qrEntity = MutableLiveData<QrEntity>()
    val qrEntity: LiveData<QrEntity> = _qrEntity

    fun setQrEntity(qrEntity: QrEntity) {
        _qrEntity.value = qrEntity
        _accHolderName.value = qrEntity.accHolderName
        _accAlias.value = qrEntity.accAlias
        _cusQrEntity.value = qrEntity.cusQrEntity
        _cusThemePath.value = qrEntity.cusThemePath
    }

    fun getQrEntity(): QrEntity? {
        return _qrEntity.value?.copy(
            accHolderName = _accHolderName.value!!.removeDiacritics().removeExtraSpaces().uppercase(),
            accAlias = _accAlias.value!!,
            cusQrEntity = _cusQrEntity.value!!,
            cusThemePath = _cusThemePath.value!!
        )
    }

    /** LiveData - Account Holder Name
     */
    private val _accHolderName = MutableLiveData<String>()
    val accHolderName: LiveData<String> = _accHolderName

    fun updateAccHolderName(string: String) {
        _accHolderName.value = string
    }

    /** LiveData - Account Alias
     */
    private val _accAlias = MutableLiveData<String>()
    val accAlias: LiveData<String> = _accAlias

    fun updateAccAlias(string: String) {
        _accAlias.value = string
    }

    /** LiveData - Custom QrEntity
     */
    private val _cusQrEntity = MutableLiveData<CustomQrEntity>()
    val cusQrEntity: LiveData<CustomQrEntity> = _cusQrEntity

    fun updateCusQrEntity(update: (CustomQrEntity) -> CustomQrEntity) {
        _cusQrEntity.value = _cusQrEntity.value?.let(update)
//        _qrEntity.value = _qrEntity.value?.copy(cusQrEntity = _cusQrEntity.value!!)
    }

    /** LiveData - Custom ThemePath
     */
    private val _cusThemePath = MutableLiveData<String>()
    val cusThemePath: LiveData<String> = _cusThemePath

    fun updateCusThemePath(string: String) {
        _cusThemePath.value = string
    }

    /** LiveData - Select Tab Index
     */
    private val _selectedTabIndex = MutableLiveData<Int>().apply { value = 0 }
    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex

    fun updateSelectTabIndex(position: Int?) {
        _selectedTabIndex.value = position
    }

}