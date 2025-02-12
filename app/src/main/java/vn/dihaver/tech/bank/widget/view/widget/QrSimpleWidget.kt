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
import vn.dihaver.tech.bank.widget.utils.ImageUtils.drawableQrToBitmap
import vn.dihaver.tech.bank.widget.utils.QrCreator
import vn.dihaver.tech.bank.widget.view.activity.CreateWidgetActivity.Companion.ACTION_WIDGET_PINNED
import vn.dihaver.tech.bank.widget.view.activity.MainActivity

class QrSimpleWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val intent = Intent(ACTION_WIDGET_PINNED).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            putExtra("style", "simple")
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        appWidgetIds.forEach { appWidgetId ->
            updateQrSimpleWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context?) {

    }

    override fun onDisabled(context: Context) {

    }
}

internal fun updateQrSimpleWidget(
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
        val bitmapQr = drawableQr.drawableQrToBitmap(Color.parseColor(widgetEntity.qrEntity.cusQrEntity.qrBackgroundColor))

        val remoteViews = RemoteViews(context.packageName, R.layout.widget_qr_simple)
        remoteViews.setViewVisibility(R.id.image_error, View.INVISIBLE)
        remoteViews.setViewVisibility(R.id.image_qr, View.VISIBLE)
        remoteViews.setImageViewBitmap(R.id.image_qr, bitmapQr)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("qr_entity_id", widgetEntity.qrEntity.id)
        }
        val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.main, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)

    } else {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_qr_simple)
        remoteViews.setViewVisibility(R.id.image_qr, View.INVISIBLE)
        remoteViews.setViewVisibility(R.id.image_error, View.VISIBLE)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(R.id.main, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }
}