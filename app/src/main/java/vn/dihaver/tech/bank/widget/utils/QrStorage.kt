package vn.dihaver.tech.bank.widget.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import vn.dihaver.tech.bank.widget.data.model.QrEntity

class QrStorage(context: Context) {

    companion object {
        const val TAG = "QrStorage"
        const val QR_STORAGE = "qr_storage"
        const val QR_LIST = "qr_list"
    }

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(QR_STORAGE, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val qrListType = object : TypeToken<List<QrEntity>>() {}.type

    private var qrCache: MutableList<QrEntity>? = null

    init {
        loadCacheFromStorage()
    }

    @Synchronized
    private fun loadQrListFromStorage(): MutableList<QrEntity> {
        val json = sharedPreferences.getString(QR_LIST, null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, qrListType) ?: mutableListOf()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing QR list from SharedPreferences", e)
            mutableListOf()
        }
    }

    @Synchronized
    private fun saveQrListToStorage() {
        qrCache?.let {
            val json = gson.toJson(it)
            sharedPreferences.edit().putString(QR_LIST, json).apply()
        }
    }

    private fun loadCacheFromStorage() {
        qrCache = loadQrListFromStorage()
    }

    private fun ensureCacheLoaded(): MutableList<QrEntity> {
        if (qrCache == null) {
            loadCacheFromStorage()
        }
        return qrCache!!
    }

    fun addQr(qr: QrEntity): Boolean {
        val listQrs = ensureCacheLoaded()

        if (listQrs.any { it.bankBin == qr.bankBin && it.accountNumber == qr.accountNumber }) {
            Log.w(TAG, "QR with both bankBin ${qr.bankBin} and accountNumber ${qr.accountNumber} already exists.")
            return false
        }

        if (listQrs.any { it.id == qr.id }) {
            Log.w(TAG, "QR with ID ${qr.id} already exists.")
            return false
        }

        listQrs.add(qr)

        saveQrListToStorage()
        return true
    }

    fun getQrById(id: String): QrEntity? {
        return ensureCacheLoaded().find { it.id == id }
    }

    fun getAllQr(): List<QrEntity> {
        return ensureCacheLoaded()
    }

    fun deleteQrById(id: String): Boolean {
        val qrList = ensureCacheLoaded()
        val removed = qrList.removeAll { it.id == id }
        if (removed) {
            saveQrListToStorage()
        }
        return removed
    }

    fun updateQr(updatedQr: QrEntity): Boolean {
        val qrList = ensureCacheLoaded()
        val index = qrList.indexOfFirst { it.id == updatedQr.id }
        return if (index != -1) {
            qrList[index] = updatedQr
            saveQrListToStorage()
            true
        } else {
            Log.w(TAG, "QR with ID ${updatedQr.id} not found.")
            false
        }
    }

    fun updateQrOrder(newOrder: List<QrEntity>) {
        if (ensureCacheLoaded() != newOrder) {
            qrCache = newOrder.toMutableList()
            saveQrListToStorage()
        }
    }

}