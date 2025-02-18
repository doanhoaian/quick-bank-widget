package vn.dihaver.tech.bank.widget.data.storage

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import java.io.IOException

class QrStorage(private val context: Context) {

    companion object {
        const val TAG = "QrStorage"
        const val TABLE = "qr_storage"
        const val QR_LIST = "qr_list"
        const val QR_ID_CURRENT = "qr_id_current"
    }

    private val sharedPre = context.applicationContext.getSharedPreferences(TABLE, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type = object : TypeToken<List<QrEntity>>() {}.type

    private var dataCache: MutableList<QrEntity>? = null

    init {
        loadCacheFromStorage()
    }

    @Synchronized
    private fun getDataFromStorage(): MutableList<QrEntity> {
        val json = sharedPre.getString(QR_LIST, null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing QR list from SharedPreferences", e)
            mutableListOf()
        }
    }

    @Synchronized
    private fun saveDataToStorage() {
        dataCache?.let {
            val json = gson.toJson(it)
            sharedPre.edit().putString(QR_LIST, json).apply()
        }
    }

    private fun loadCacheFromStorage() {
        dataCache = getDataFromStorage()
    }

    private fun ensureCacheLoaded(): MutableList<QrEntity> {
        if (dataCache == null) {
            loadCacheFromStorage()
        }
        return dataCache!!
    }

    fun get(index: Int): QrEntity {
        return ensureCacheLoaded()[index]
    }

    fun getById(id: String): QrEntity? {
        return ensureCacheLoaded().find { it.id == id }
    }

    fun getAll(): List<QrEntity> {
        return ensureCacheLoaded()
    }

    fun getItemCurrent(): QrEntity? {
        val id = sharedPre.getString(QR_ID_CURRENT, "")!!
        if (id.isNotEmpty()) {
            return getById(id)
        }
        return null
    }

    fun add(element: QrEntity, isDuplicate: Boolean = false): Boolean {
        val listQrs = ensureCacheLoaded()

        if (listQrs.any { it.id == element.id }) {
            Log.w(TAG, "QR with ID ${element.id} already exists.")
            return false
        }

        if (!isDuplicate) {
            if (listQrs.any { it.bankBin == element.bankBin && it.accNumber == element.accNumber }) {
                Log.e(TAG, "QR already exists.")
                return false
            }
        }

        listQrs.add(element)

        saveDataToStorage()
        return true
    }

    fun update(element: QrEntity): Boolean {
        val qrList = ensureCacheLoaded()
        val index = qrList.indexOfFirst { it.id == element.id }
        return if (index != -1) {
            qrList[index] = element
            saveDataToStorage()
            true
        } else {
            Log.w(TAG, "QR with ID ${element.id} not found.")
            false
        }
    }

    fun updateItemCurrent(id: String) {
        sharedPre.edit().putString(QR_ID_CURRENT, id).apply()
    }

    fun deleteById(id: String): Boolean {
        val qrList = ensureCacheLoaded()
        val removed = qrList.removeAll { it.id == id }
        if (removed) {
            saveDataToStorage()
        }
        return removed
    }

    @Suppress("unused")
    fun updateList(newOrder: List<QrEntity>) {
        if (ensureCacheLoaded() != newOrder) {
            dataCache = newOrder.toMutableList()
            saveDataToStorage()
        }
    }

    /** Định nghĩa đối tượng chứa dữ liệu backup: danh sách QR và QR đang được hiển thị. */
    data class BackupData(
        val qrList: List<QrEntity>,
        val qrCurrent: String?
    )

    fun backupToUri(uri: Uri): Boolean {
        val backupData = BackupData(
            qrList = getAll(),
            qrCurrent = sharedPre.getString(QR_ID_CURRENT, "")
        )
        val json = gson.toJson(backupData)
        val encoded = Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.DEFAULT)

        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(encoded.toByteArray(Charsets.UTF_8))
            } ?: throw IOException("Không mở được luồng ghi cho file backup")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi ghi file backup", e)
            false
        }
    }

    fun restoreFromUri(uri: Uri, overrideCurrent: Boolean): Boolean {
        return try {
            val encoded = context.contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
                reader?.readText() ?: throw IOException("Không đọc được nội dung file backup")
            }
            val decoded = String(Base64.decode(encoded, Base64.DEFAULT), Charsets.UTF_8)
            val backupData = gson.fromJson(decoded, BackupData::class.java)

            if (overrideCurrent) {
                dataCache = backupData.qrList.toMutableList()
                sharedPre.edit().putString(QR_LIST, gson.toJson(backupData.qrList)).apply()
            } else {
                val currentList = ensureCacheLoaded()
                backupData.qrList.forEach { qr ->
                    if (currentList.none { it.id == qr.id }) {
                        currentList.add(qr)
                    }
                }
                sharedPre.edit().putString(QR_LIST, gson.toJson(currentList)).apply()
            }
            backupData.qrCurrent?.let { currentId ->
                if (currentId.isNotEmpty()) {
                    sharedPre.edit().putString(QR_ID_CURRENT, currentId).apply()
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi khôi phục file backup", e)
            false
        }
    }
}