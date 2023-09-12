import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.safecircle.R

class BatteryLevelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct: Float = level / scale.toFloat() * 100

        if (batteryPct < 10) {
            showBatteryLowNotification(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showBatteryLowNotification(context: Context) {
        val channelId = "battery_notification_channel"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Battery Alert!")
            .setContentText("Battery level is below 80%")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())
    }
}
