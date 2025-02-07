package vn.dihaver.tech.bank.widget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import vn.dihaver.tech.bank.widget.data.model.QrEntity
import vn.dihaver.tech.bank.widget.data.model.WidgetEntity
import vn.dihaver.tech.bank.widget.data.storage.WidgetStorage

class CreateWidgetViewModel: ViewModel() {

    private val _qrEntityCurrent = MutableLiveData<QrEntity>()
    val qrEntityCurrent: LiveData<QrEntity> = _qrEntityCurrent

    fun updateQrEntityCurrent(qrEntity: QrEntity) {
        _qrEntityCurrent.value = qrEntity
    }

    private val _widgetList = MutableLiveData<List<WidgetEntity>>()
    val widgetList: LiveData<List<WidgetEntity>> = _widgetList
    val isWidgetListEmpty: LiveData<Boolean> = _widgetList.map { it.isNullOrEmpty() }

    fun updateWidgetList(widgetList: List<WidgetEntity>) {
        _widgetList.value = widgetList
    }

    fun addWidget(widgetStorage: WidgetStorage, widgetEntity: WidgetEntity): Boolean {
        return if (widgetStorage.add(widgetEntity)) {
            updateWidgetList(widgetStorage.getAll())
            true
        } else false
    }

    fun editWidget(widgetStorage: WidgetStorage, widgetEntity: WidgetEntity): Boolean {
        return if (widgetStorage.update(widgetEntity)) {
            updateWidgetList(widgetStorage.getAll())
            true
        } else false
    }

    fun deleteWidget(widgetStorage: WidgetStorage, widgetEntity: WidgetEntity): Boolean {
        return if (widgetStorage.delete(widgetEntity)) {
            updateWidgetList(widgetStorage.getAll())
            true
        } else false
    }


}