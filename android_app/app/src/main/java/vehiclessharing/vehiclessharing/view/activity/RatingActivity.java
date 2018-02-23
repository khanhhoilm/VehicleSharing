package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.RatingUserTogetherAPI;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.utils.NetworkUtils;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        RatingBar.OnRatingBarChangeListener, RatingUserTogetherAPI.RatingCallback, UserInfoAPI.GetInfoUserCallback {

    private TextView mTxtName, mTxtSource, mTxtDes, mTxtTime, mTxtComment;
    private ImageView mAvatar, mVehicleType;

    private RatingBar mRatingBar;
    private Button mBtnSend;
    private ProgressBar mProgressBar;

    private int mJourneyId, isFavorite = 0, mNumberStar = 0;
    private String mApiToken = "";

    private DatabaseHelper mDatabaseHelper;
    private RequestInfo mYourRequestInfo;
    private UserInfoAPI mUserInfoAPI;

    private SharedPreferences mSharedPreferencesScreen;
    private SharedPreferences.Editor mEditorScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mApiToken = MainActivity.sSessionId;
        Bundle bundle = getIntent().getExtras();
        mJourneyId = bundle.getInt("journey_id", 0);
        mSharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);
        if (mJourneyId == 0) {
            mJourneyId = mSharedPreferencesScreen.getInt(VehicleMoveActivity.JOURNEY_ID, 0);
        }
        int screen = mSharedPreferencesScreen.getInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        if (mJourneyId == 0 || screen == MainActivity.MAIN_ACTIVITY) {
            goToMainActivity();
        } else {
            mEditorScreen = mSharedPreferencesScreen.edit();
            mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
            mEditorScreen.putInt(VehicleMoveActivity.JOURNEY_ID, mJourneyId);
            mEditorScreen.commit();
        }
        mDatabaseHelper = new DatabaseHelper(this);
        mYourRequestInfo = mDatabaseHelper.getRequestInfoNotMe(MainActivity.sUserId);


        mUserInfoAPI = new UserInfoAPI(this);
        addControls();
        if (mYourRequestInfo.getSourceLocation() != null && mYourRequestInfo.getDestLocation() != null) {
            loadUI();
        }
        addEvents();
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
        mEditorScreen.commit();
    }

    private void addEvents() {
        mBtnSend.setOnClickListener(this);
        mTxtComment.setOnFocusChangeListener(this);
        mRatingBar.setOnRatingBarChangeListener(this);
    }

    private void addControls() {
        mTxtName = findViewById(R.id.txtFullName);
        mAvatar = findViewById(R.id.imgAvatar);
        mTxtSource = findViewById(R.id.txtSourceLocation);
        mTxtDes = findViewById(R.id.txtDesLocation);
        mTxtTime = findViewById(R.id.txtTimeStartEnd);
        mVehicleType = findViewById(R.id.imgVehicleType);
        mRatingBar = findViewById(R.id.rbRating);
        mTxtComment = findViewById(R.id.txtWriteComment);
        mBtnSend = findViewById(R.id.btnSendRating);
        mProgressBar = findViewById(R.id.progressBar);

    }

    private void loadUI() {
        mUserInfoAPI.getUserInfoFromAPI(MainActivity.sSessionId, mYourRequestInfo.getUserId());
        try {
            mTxtSource.setText(PlaceHelper.getInstance(this).getAddressByLatLngLocation(mYourRequestInfo.getSourceLocation()));
            mTxtDes.setText(PlaceHelper.getInstance(this).getAddressByLatLngLocation(mYourRequestInfo.getDestLocation()));
            mTxtTime.setText(mYourRequestInfo.getTimeStart());
            switch (mYourRequestInfo.getVehicleType()) {
                case 0:
                    mVehicleType.setImageResource(R.drawable.ic_directions_run_indigo_700_24dp);
                    break;
                case 1:
                    mVehicleType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
                    break;
                case 2:
                    mVehicleType.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSendRating:
                if (NetworkUtils.isNetworkConnected(this)) {

                    if (mJourneyId != 0) {
                        mBtnSend.setEnabled(false);
                        mRatingBar.setEnabled(false);
                        mProgressBar.setVisibility(View.VISIBLE);
                        RatingUserTogetherAPI ratingUserTogetherAPI = new RatingUserTogetherAPI(this);
                        ratingUserTogetherAPI.rating(mApiToken, mJourneyId, mRatingBar.getRating(), mTxtComment.getText().toString());
                    } else {
                        goToMainActivity();
                    }
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mBtnSend.setAlpha(1);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        mBtnSend.setAlpha(1);
    }

    @Override
    public void ratingSuccess() {
        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "rating success", Toast.LENGTH_SHORT).show();

        DatabaseHelper mDatabase = new DatabaseHelper(this);
        mDatabase.deleteAllRequest();
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);

        mEditorScreen.putInt(VehicleMoveActivity.JOURNEY_ID, 0);
        mEditorScreen.commit();

        if (mRatingBar.getRating() < 4 || isFavorite == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LikeUserActivity.class);
            intent.putExtra(LikeUserActivity.PARTNER_ID, mYourRequestInfo.getUserId());
            startActivity(intent);
        }

    }

    private void goToMainActivity() {
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        mEditorScreen.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void ratingFailure(String message) {
        if (mNumberStar > 2) {
            goToMainActivity();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mBtnSend.setEnabled(true);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getInfoUserSuccess(InfomationUser infomationUser) {
        mTxtName.setText(infomationUser.getUserInfo().getName());
        mTxtSource.setText(infomationUser.getUserInfo().getName());
        if (infomationUser.getUserInfo().getAvatarLink() != null && !infomationUser.getUserInfo().getAvatarLink().equals("")) {
            Glide.with(this).load(infomationUser.getUserInfo().getAvatarLink()).placeholder(R.drawable.temp)
                    .error(R.drawable.temp).centerCrop().into(mAvatar);
        }
        isFavorite = infomationUser.getIsFavorite();
    }

    @Override
    public void getUserInfoFailure(String message) {
        Log.d("getUserInfo", message);
        mUserInfoAPI.getUserInfoFromAPI(MainActivity.sSessionId, mYourRequestInfo.getUserId());
    }
}
