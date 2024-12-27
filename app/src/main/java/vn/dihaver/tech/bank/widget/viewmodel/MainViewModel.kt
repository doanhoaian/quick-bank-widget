package vn.dihaver.tech.bank.widget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.utils.QrStorage

class MainViewModel : ViewModel() {

    private val _qrList = MutableLiveData<List<QrEntity>>(emptyList())
    val qrList: LiveData<List<QrEntity>> = _qrList
    val isQrListEmpty: LiveData<Boolean> = _qrList.map { it.isEmpty() }

    fun updateQrList(newList: List<QrEntity>) {
        _qrList.value = newList
    }

    private val _isViewTypeMultiple = MutableLiveData(false)
    val isViewTypeMultiple: LiveData<Boolean> = _isViewTypeMultiple

    fun onClickViewTypeQr() {
        _isViewTypeMultiple.value = !_isViewTypeMultiple.value!!
    }

    private val _isMoreQr = MutableLiveData(false)
    val isMoreQr: LiveData<Boolean> = _isMoreQr

    fun onClickMoreQr() {
        _isMoreQr.value = !_isMoreQr.value!!
    }

    fun updateIsMoreQr(boolean: Boolean) {
        _isMoreQr.value = boolean
    }




    fun addQr(qrEntity: QrEntity, qrStorage: QrStorage): Boolean {
        return if (qrStorage.addQr(qrEntity)) {
            updateQrList(qrStorage.getAllQr())
            true
        } else {
            false
        }
    }

    fun deleteQrById(id: String, qrStorage: QrStorage): Boolean {
        return if (qrStorage.deleteQrById(id)) {
            updateQrList(qrStorage.getAllQr())
            true
        } else {
            false
        }
    }
}
