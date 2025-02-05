package vn.dihaver.tech.bank.widget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanQrViewModel : ViewModel() {

    private val _qrContent = MutableLiveData<String>()
    val qrContent: LiveData<String> get() = _qrContent

    fun setQrContent(content: String) {
        _qrContent.value = content
    }
}