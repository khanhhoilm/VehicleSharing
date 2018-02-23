package vehiclessharing.vehiclessharing.push;

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
import vehiclessharing.vehiclessharing.view.activity.ConfirmRequestActivity;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.view.activity.RatingActivity;
import vehiclessharing.vehiclessharing.view.activity.ReceiveConfirmRequestActivity;
import vehiclessharing.vehiclessharing.view.activity.VehicleMoveActivity;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = CustomFirebaseMessagingService.class.getSimpleName();
    public static String DATA_RECEIVE = "data_about_sender";
    public static String SHARE_PREFER_END_TRIP = "share_prefer_end_trip";
    public static String IS_END_TRIP = "is_end_trip";
    private SharedPreferences mSharedPreferencesScreen;
    SharedPreferences.Editor mEditorScreen;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String title = "", message = "";
        String type = "";
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
                mSharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);


                switch (type) {
                    case "send_request":
                        intent = new Intent(this, ConfirmRequestActivity.class);
                        intent.putExtra(DATA_RECEIVE, jsonReceive);
                        title = "Yêu cầu đi chung";
                        message = "Có 1 yêu cầu muốn đi chung được gửi đến bạn";
                        break;
                    case "confirm_request":
                        if (status.equals("accept")) {
                            intent = new Intent(this, ReceiveConfirmRequestActivity.class);
                            intent.putExtra(DATA_RECEIVE, jsonReceive);
                            message = "Yêu cầu của bạn đã được chấp nhận";
                        } else {
                            mEditorScreen = mSharedPreferencesScreen.edit();
                            mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.ADDED_REQUEST);
                            mEditorScreen.commit();
                            intent = new Intent(this, MainActivity.class);
                            message = "Yêu cầu của bạn đã bị từ chối";
                        }
                        title = "Phản hồi xác nhận yêu cầu";

                        break;
                    case "start_the_trip":
                        intent = new Intent(this, VehicleMoveActivity.class);
                        if (jsonObject.has("journey_id")) {

                            //bug not cast
                            Integer journeyId = (Integer) jsonObject.get("journey_id");
                            intent.putExtra("journey_id", journeyId);
                            intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY, VehicleMoveActivity.START_TRIP);

                            mEditorScreen = mSharedPreferencesScreen.edit();
                            mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
                            mEditorScreen.putInt(VehicleMoveActivity.JOURNEY_ID, journeyId);
                            mEditorScreen.commit();
                        }
                        title = "Bắt đầu chuyến đi";
                        message = "Người có xe đã đến nơi và đón người không có xe thành công";
                        break;
                    case "end_the_trip":
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_PREFER_END_TRIP, MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putBoolean(IS_END_TRIP, true);
                        edit.commit();
                        intent = new Intent(this, RatingActivity.class);
                        if (jsonObject.has("journey_id")) {
                            Integer journeyId = (Integer) jsonObject.get("journey_id");
                            intent.putExtra("journey_id", journeyId);
                        }
                        title = "Chuyến đi đã kết thúc";
                        message = "Hãy rating ngay cho người đi cùng  nào";
                        break;
                    case "cancel_the_trip":
                        title = "Chuyến đi của bạn đã bị hủy";
                        String comment = "";
                        if (jsonObject.has("comment")) {
                            comment = jsonObject.getString("comment");
                        }
                        if (comment.length() > 0) {
                            message = "Lí do: " + comment;
                        } else {
                            message = "Người đi chung đã hủy với không lí do";
                        }
                        intent =new Intent(this,MainActivity.class);
                        mEditorScreen = mSharedPreferencesScreen.edit();
                        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
                        mEditorScreen.commit();
                        break;
                    default:
                        intent=new Intent(this,MainActivity.class);
                        mEditorScreen = mSharedPreferencesScreen.edit();
                        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
                        mEditorScreen.commit();
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_accessibility_indigo_700_24dp)
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
