package vn.dihaver.tech.bank.widget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.utils.QrStorage

class MainViewModel : ViewModel() {

    /** Danh sách QR
     */
    private val _qrList = MutableLiveData<List<QrEntity>>()
    val qrList: LiveData<List<QrEntity>> = _qrList
    val isQrListEmpty: LiveData<Boolean> = _qrList.map { it.isNullOrEmpty() }

    fun updateQrList(newList: List<QrEntity>) {
        _qrList.value = newList
    }

    fun addQrEntity(qrEntity: QrEntity, qrStorage: QrStorage, isDuplicate: Boolean = false): Boolean {
        return if (qrStorage.addQr(qrEntity, isDuplicate)) {
            updateQrList(qrStorage.getAllQr())
            true
        } else {
            false
        }
    }

    fun editQrEntity(qrEntity: QrEntity, qrStorage: QrStorage): Boolean {
        return if (qrStorage.updateQr(qrEntity)) {
            updateQrList(qrStorage.getAllQr())
            true
        } else {
            false
        }
    }

    fun deleteQrEntity(id: String, qrStorage: QrStorage): Boolean {
        return if (qrStorage.deleteQrById(id)) {
            updateQrList(qrStorage.getAllQr())
            true
        } else {
            false
        }
    }

    /** Components QrEntity
     */

    private val _qrEntityCurrent = MutableLiveData<QrEntity>()
    val qrEntityCurrent: LiveData<QrEntity> = _qrEntityCurrent

    fun getQrEntityCurrent(): QrEntity? {
        return _qrEntityCurrent.value
    }

    fun updateQrEntityCurrent(qrEntity: QrEntity) {
        _qrEntityCurrent.value = qrEntity
    }



}
