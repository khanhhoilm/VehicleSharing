package vehiclessharing.vehiclessharing.push;

/**
 * Created by Hihihehe on 11/6/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.controller.activity.ConfirmRequestActivity;
import vehiclessharing.vehiclessharing.controller.activity.MainActivity;
import vehiclessharing.vehiclessharing.controller.activity.RatingActivity;
import vehiclessharing.vehiclessharing.controller.activity.ReceiveConfirmRequestActivity;
import vehiclessharing.vehiclessharing.controller.activity.VehicleMoveActivity;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = CustomFirebaseMessagingService.class.getSimpleName();
    public static String DATA_RECEIVE = "data_about_sender";
    public static String SHARE_PREFER_END_TRIP = "share_prefer_end_trip";
    public static String IS_END_TRIP = "is_end_trip";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        //   JSONObject jsonObject=new JSONObject(data);
        //  String type=
        sendNotification(title, message, remoteMessage);
    }

    private void sendNotification(String title, String message, RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String type = "";
        Random random = new Random();
        int randomNumber = random.nextInt(9999 - 1000) + 1000;

        Intent intent = new Intent(this, MainActivity.class);

        String jsonReceive = "";
        String status = "";
        if (remoteMessage.getData().get("data") != null) {
            jsonReceive = remoteMessage.getData().get("data");

            try {
                JSONObject jsonObject = new JSONObject(jsonReceive);
                if (jsonObject.has("type")) {
                    type = (String) jsonObject.get("type");
                }
                if (jsonObject.has("status")) {
                    status = (String) jsonObject.get("status");
                }

                switch (type) {
                    case "send_request":
                        intent = new Intent(this, ConfirmRequestActivity.class);
                        intent.putExtra(DATA_RECEIVE, jsonReceive);
                        break;
                    case "confirm_request":
                        if (status.equals("accept")) {
                            intent = new Intent(this, ReceiveConfirmRequestActivity.class);
                            intent.putExtra(DATA_RECEIVE, jsonReceive);
                        } else {
                            intent = new Intent(this, MainActivity.class);
                        }
                        break;
                    case "start_the_trip":
                        intent = new Intent(this, VehicleMoveActivity.class);
                        if (jsonObject.has("journey_id")) {
                            //bug not cast
                            Integer journeyId = (Integer) jsonObject.get("journey_id");
                            intent.putExtra("journey_id", journeyId);
                            intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY, "start_request");
                        }
                        break;
                    case "end_the_trip":
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFER_END_TRIP, MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putBoolean(IS_END_TRIP, true);
                        edit.commit();
                        intent = new Intent(this, RatingActivity.class);
                        if (jsonObject.has("journey_id")) {
                            //bug not cast
                            Integer journeyId = (Integer) jsonObject.get("journey_id");
                            intent.putExtra("journey_id", journeyId);
                        }
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_directions_run_indigo_700_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
