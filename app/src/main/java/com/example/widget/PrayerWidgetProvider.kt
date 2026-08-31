package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.repository.MosqueRepository

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val mosqueInfo = MosqueRepository.mosqueInfo
        val prayers = MosqueRepository.calculateTodayPrayers()

        val fajrTime = prayers.find { it.type == com.example.ui.components.PrayerType.FAJR }?.azanTime ?: "০৫:০২"
        val dhuhrTime = prayers.find { it.type == com.example.ui.components.PrayerType.DHUHR }?.azanTime ?: "১২:১৫"
        val asrTime = prayers.find { it.type == com.example.ui.components.PrayerType.ASR }?.azanTime ?: "০৪:৪৫"
        val maghribTime = prayers.find { it.type == com.example.ui.components.PrayerType.MAGHRIB }?.azanTime ?: "০৬:২৪"
        val ishaTime = prayers.find { it.type == com.example.ui.components.PrayerType.ISHA }?.azanTime ?: "০৭:৪৫"

        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.prayer_home_widget).apply {
                setTextViewText(R.id.widget_mosque_title, mosqueInfo.nameBn)
                setTextViewText(R.id.widget_district_name, "${mosqueInfo.district} জেলা")
                setTextViewText(R.id.widget_fajr, "ফজর\n$fajrTime")
                setTextViewText(R.id.widget_dhuhr, "যোহর\n$dhuhrTime")
                setTextViewText(R.id.widget_asr, "আসর\n$asrTime")
                setTextViewText(R.id.widget_maghrib, "মাগরিব\n$maghribTime")
                setTextViewText(R.id.widget_isha, "ইশা\n$ishaTime")
                setOnClickPendingIntent(R.id.widget_mosque_title, pendingIntent)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
