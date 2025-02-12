package vn.dihaver.tech.bank.widget.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.alexzhirkevich.customqrgenerator.QrData
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.storage.WidgetStorage
import vn.dihaver.tech.bank.widget.utils.BitmapUtils
import vn.dihaver.tech.bank.widget.utils.ImageUtils.drawableQrToBitmap
import vn.dihaver.tech.bank.widget.utils.QrCreator
import vn.dihaver.tech.bank.widget.view.activity.CreateWidgetActivity.Companion.ACTION_WIDGET_PINNED
import vn.dihaver.tech.bank.widget.view.activity.MainActivity

class QrAdvancedWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val intent = Intent(ACTION_WIDGET_PINNED).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            putExtra("style", "advanced")
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        appWidgetIds.forEach { appWidgetId ->
            updateQrAdvanceWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }
}

internal fun updateQrAdvanceWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetStorage = WidgetStorage(context)
    val widgetEntity = widgetStorage.getById(appWidgetId.toString())
    if (widgetEntity != null) {
        val qrCreator = QrCreator(context)
        val dataQr = QrData.Text(widgetEntity.qrEntity.qrContent)
        val drawableQr = qrCreator.createDrawable(dataQr, widgetEntity.qrEntity.cusQrEntity)

        val textAlias = widgetEntity.qrEntity.accAlias
        val textHolderName = widgetEntity.qrEntity.accHolderName
        val textNumber = widgetEntity.qrEntity.accNumber
        val bitmapLogo = BitmapUtils.getBitmapFromResource(context, widgetEntity.qrEntity.bankLogoRes)
        val bitmapQr = drawableQr.drawableQrToBitmap(Color.parseColor(widgetEntity.qrEntity.cusQrEntity.qrBackgroundColor))

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("qr_entity_id", widgetEntity.qrEntity.id)
        }
        val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val remoteViews = RemoteViews(context.packageName, R.layout.widget_qr_advanced)
        remoteViews.setViewVisibility(R.id.image_error, View.INVISIBLE)
        remoteViews.setViewVisibility(R.id.image_logo_bank, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.text_holder_name, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.text_number, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.image_qr, View.VISIBLE)
        if (textAlias.isNotEmpty()) {
            remoteViews.setViewVisibility(R.id.text_alias, View.VISIBLE)
            remoteViews.setTextViewText(R.id.text_alias, textAlias)
        }
        remoteViews.setTextViewText(R.id.text_holder_name, textHolderName)
        remoteViews.setTextViewText(R.id.text_number, textNumber)
        remoteViews.setImageViewBitmap(R.id.image_logo_bank, bitmapLogo)
        remoteViews.setImageViewBitmap(R.id.image_qr, bitmapQr)
        remoteViews.setOnClickPendingIntent(R.id.main, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    } else {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val remoteViews = RemoteViews(context.packageName, R.layout.widget_qr_advanced)
        remoteViews.setViewVisibility(R.id.image_qr, View.INVISIBLE)
        remoteViews.setViewVisibility(R.id.image_error, View.VISIBLE)
        remoteViews.setOnClickPendingIntent(R.id.main, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }
}