package vehiclessharing.vehiclessharing.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.controller.activity.VehicleMoveActivity;

/**
 * Created by Hihihehe on 12/18/2017.
 */

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("5' nữa chuyến đi của bạn sẽ bắt đầu, chuẩn bị di chuyển thôi nào!");
    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent=new Intent(this,VehicleMoveActivity.class);
        intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY,VehicleMoveActivity.ALARM_START);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Đến giờ di chuyển")
                .setSmallIcon(R.drawable.ic_accessibility_indigo_700_24dp)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setAutoCancel(true)
                .setContentText(msg);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}

