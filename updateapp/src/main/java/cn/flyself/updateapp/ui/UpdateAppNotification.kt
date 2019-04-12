package cn.flyself.updateapp.ui

import android.annotation.SuppressLint
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import cn.flyself.updateapp.R


class UpdateAppNotification(private val context: Context) {
    private var intent: Intent? = null

    companion object {
        const val CHANNEL_DOWNLOAD = "download"
        const val TYPE_DOWNLOAD = 1
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotificationChannel()
        }
    }

    /**
     * 设置跳转的intent
     * @param intent
     * @return
     * */
    fun setIntent(intent: Intent) {
        this.intent = intent
    }

    /**
     * 显示一条通知
     * @param title
     * @param text
     * @param progress
     * @param icon
     * @return
     * */
    fun showNotification(title: String, text: String, progress: Int, icon: Int) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        @SuppressLint("InlinedApi")
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
            context.startActivity(intent)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_DOWNLOAD)
            .setContentTitle(title)
            .setContentText(text)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(icon)
            .setAutoCancel(true)
            //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
            .setVibrate(LongArray(0))
            .setSound(null)
            .setShowWhen(false)
        if (progress in 0..100) {
            builder.setProgress(100, progress, false)
        }
        if (intent != null) {
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
        }
        val notification = builder.build()
        manager.notify(TYPE_DOWNLOAD, notification)
    }

    /**
     * 清除指定id的通知
     * @param id
     * @return
     * */
    fun clearNotification(id: Int) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun initNotificationChannel() {
        val channelId = CHANNEL_DOWNLOAD
        val channelName = context.getString(R.string.update_app_notification_channel)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        createNotificationChannel(channelId, channelName, importance)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.vibrationPattern = LongArray(0)
        channel.setSound(null, null)
        channel.setShowBadge(false)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.createNotificationChannel(channel)
    }
}