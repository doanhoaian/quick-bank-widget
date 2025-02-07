package vn.dihaver.tech.bank.widget.data.storage

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import vn.dihaver.tech.bank.widget.data.model.WidgetEntity

class WidgetStorage(context: Context) {

    companion object {
        const val TAG = "WidgetStorage"
        const val TABLE = "widget_storage"
        const val WIDGET_LIST = "widget_list"
    }

    private val sharedPre = context.applicationContext.getSharedPreferences(TABLE, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type = object : TypeToken<List<WidgetEntity>>() {}.type

    private var dataCache: MutableList<WidgetEntity>? = null

    init {
        loadCacheFromStorage()
    }

    @Synchronized
    private fun getDataFromStorage(): MutableList<WidgetEntity> {
        val json = sharedPre.getString(WIDGET_LIST, null) ?: return mutableListOf()
        return try {
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing data from SharedPre", e)
            return mutableListOf()
        }
    }

    @Synchronized
    private fun saveDataToStorage() {
        dataCache?.let {
            val json = gson.toJson(it)
            sharedPre.edit().putString(WIDGET_LIST, json).apply()
        }
    }

    private fun loadCacheFromStorage() {
        dataCache = getDataFromStorage()
    }

    private fun ensureCacheLoaded(): MutableList<WidgetEntity> {
        if (dataCache == null) {
            loadCacheFromStorage()
        }
        return dataCache!!
    }

    fun get(index: Int): WidgetEntity {
        return ensureCacheLoaded()[index]
    }

    fun getById(id: String): WidgetEntity? {
        return ensureCacheLoaded().find { it.id == id }
    }

    fun getAll(): List<WidgetEntity> {
        return ensureCacheLoaded()
    }

    fun add(element: WidgetEntity): Boolean {
        val list = ensureCacheLoaded()

        if (list.any { it.id == element.id}) {
            return false
        }

        list.add(element)
        saveDataToStorage()
        return true
    }

    fun addAll(elements: List<WidgetEntity>) {
        val list = ensureCacheLoaded()

        for (element in elements) {
            if (list.any { it.id == element.id }) {
                break
            }

            list.add(element)
        }

        saveDataToStorage()
    }

    fun update(element: WidgetEntity): Boolean {
        val list = ensureCacheLoaded()
        val index = list.indexOfFirst { it.id == element.id }
        return if (index != -1) {
            list[index] = element
            saveDataToStorage()
            true
        } else false
    }

    fun updateList(elements: List<WidgetEntity>): Boolean {
        return if (ensureCacheLoaded() != elements) {
            dataCache = elements.toMutableList()
            saveDataToStorage()
            true
        } else false
    }

    fun delete(element: WidgetEntity): Boolean {
        val list = ensureCacheLoaded()
        return if (list.remove(element)) {
            saveDataToStorage()
            true
        } else false
    }

    fun deleteById(id: String): Boolean {
        val list = ensureCacheLoaded()
        val index = list.indexOfFirst { it.id == id }
        return if (index != -1) {
            list.removeAt(index)
            saveDataToStorage()
            true
        } else false
    }

    fun deleteAll(elements: List<WidgetEntity>): Boolean {
        val list = ensureCacheLoaded()
        return if (list.removeAll(elements)) {
            saveDataToStorage()
            true
        } else false
    }

    fun clear() {
        val list = ensureCacheLoaded()
        list.clear()
        saveDataToStorage()
    }


}