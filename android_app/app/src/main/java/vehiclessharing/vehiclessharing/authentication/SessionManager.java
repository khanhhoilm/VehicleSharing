package vehiclessharing.vehiclessharing.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import vehiclessharing.vehiclessharing.view.activity.SigninActivity;

/**
 * Created by Hihihehe on 9/23/2017.
 */

public class SessionManager {
    private SharedPreferences mPref;

    private SharedPreferences.Editor mEditor;
    private Context mContext;

    private int PRIVATE_MODE = 0;

    public static final String PREF_NAME_LOGIN = "vsharing_is_login";

    public static final String IS_LOGIN = "is_loggedin";

    public static final String KEY_NAME = "name";

    public static final String KEY_SESSION = "session_id";
    public static final String USER_ID = "user_id";


    public SessionManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME_LOGIN, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void createLoginSession(int userId, String session_id) {
        mEditor.putBoolean(IS_LOGIN, true);

        mEditor.putInt(USER_ID, userId);

        mEditor.putString(KEY_SESSION, session_id);
        mEditor.commit();
    }

    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();

        Intent signInIntent = new Intent(mContext, SigninActivity.class);

        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(signInIntent);
    }

    public boolean isLoggedIn() {
        Log.d("isLoggedIn", "Sessionid: " + mPref.getString(KEY_SESSION, ""));
        return mPref.getBoolean(IS_LOGIN, false);
    }
}
