package vn.dihaver.tech.bank.widget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import vn.dihaver.tech.bank.widget.utils.BankBinVN
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeDiacritics
import vn.dihaver.tech.bank.widget.utils.FormatUtils.removeExtraSpaces

class CreateQrViewModel : ViewModel() {

    /**
     */
    private val _bankBin = MutableLiveData("")
    val bankBin: LiveData<String> = _bankBin
    val isBankBinEmpty: LiveData<Boolean> = _bankBin.map { it.isNullOrEmpty() }

    fun getBankBin(): String? {
        return _bankBin.value
    }

    fun updateBankBin(bankBin: String) {
        _bankBin.value = bankBin
    }

    /**
     */
    private val _bankAccountNumber = MutableLiveData("")
    val bankAccountNumber: LiveData<String> = _bankAccountNumber
    val isBankAccountNumberEmpty: LiveData<Boolean> = _bankAccountNumber.map { it.isNullOrEmpty() }

    fun updateBankAccountNumber(string: String) {
        _bankAccountNumber.value = string
    }

    /**
     */
    private val _qrData = MutableLiveData("")
    val qrData: LiveData<String> = _qrData
    val isQrDataEmpty: LiveData<Boolean> = _qrData.map { it.isNullOrEmpty() }

    fun getQrData(): String? {
        return _qrData.value
    }

    fun updateQrData(string: String) {
        _qrData.value = string
    }

    fun createQrData(bin: String, accountNumber: String, accountName: String) {
        val accountNameNew = accountName.removeDiacritics().removeExtraSpaces().uppercase()
        val qrData = BankBinVN.generateQrCode(bin, accountNumber, accountNameNew)
        _qrData.value = qrData
    }

}