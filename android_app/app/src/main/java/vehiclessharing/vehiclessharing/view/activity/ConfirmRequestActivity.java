package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.api.ConfirmRequestAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ReceiveRequest;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.utils.NetworkUtils;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class ConfirmRequestActivity extends AppCompatActivity implements View.OnClickListener, ConfirmRequestAPI.ConfirmRequestCallback {
    private ReceiveRequest mReceiveRequest;
    private Button mBtnAccept, mBtnDeny, mBtnDirect;
    private CircleImageView mAvatar;
    private TextView mTxtSourceLocation, mTxtDestinationLocation, mTxtUserName, mTxtTimeStart, mTxtNote, mTxtDistance;
    private String mApiToken = "";
    private int mUserId;
    private DatabaseHelper mDatabaseHelper;
    private RequestInfo mYourRequestInfo;
    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferencesScreen;
    private SharedPreferences.Editor mEditorScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        Bundle bundle = getIntent().getExtras();

        String dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");

        if (dataReceive.length() > 0) {
            Gson gson = new Gson();
            mReceiveRequest = gson.fromJson(dataReceive, ReceiveRequest.class);

        }

        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        mApiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        mUserId = sharedPreferences.getInt(SessionManager.USER_ID, 0);

        mDatabaseHelper = new DatabaseHelper(this);

        addControls();
        addEvents();
        loadContent();
        mSharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);

    }

    private void loadContent() {
        RequestInfo myRequestInfo = mDatabaseHelper.getRequestInfo(mUserId);

        Location myLocation = new Location("MyLocation");
        myLocation.setLatitude(Double.parseDouble(myRequestInfo.getSourceLocation().getLat()));
        myLocation.setLongitude(Double.parseDouble(myRequestInfo.getSourceLocation().getLng()));
        Location anotherLocation = new Location("AnotherLocation");
        anotherLocation.setLatitude(Double.parseDouble(mReceiveRequest.getStartLocation().getLat()));
        anotherLocation.setLongitude(Double.parseDouble(mReceiveRequest.getStartLocation().getLng()));

        float distance = Helper.getKiloMeter(myLocation.distanceTo(anotherLocation));

        mTxtUserName.setText(mReceiveRequest.getUserName());
        mTxtTimeStart.setText(mReceiveRequest.getStartTime());
        mTxtNote.setText(mReceiveRequest.getNote());

        if (mReceiveRequest.getAvartarLink() != null && !mReceiveRequest.getAvartarLink().equals("")) {
            Glide.with(this).load(mReceiveRequest.getAvartarLink())
                    .placeholder(getResources().getDrawable(R.drawable.temp)).into(mAvatar);
        }
        try {
            String sourceLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(mReceiveRequest.getStartLocation());
            mTxtSourceLocation.setText(sourceLocation);
            String endLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(mReceiveRequest.getEndLocation());
            mTxtDestinationLocation.setText(endLocation);
            mTxtDistance.setText("Cách bạn: " + String.valueOf(distance) + " km");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addEvents() {

        mBtnAccept.setOnClickListener(this);
        mBtnDeny.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        mBtnDirect.setOnClickListener(this);
    }


    private void addControls() {
        mTxtUserName = findViewById(R.id.txtUserName);
        mTxtSourceLocation = findViewById(R.id.txtSource);
        mTxtDestinationLocation = findViewById(R.id.txtDestination);
        mTxtTimeStart = findViewById(R.id.txtTimeStart);
        mAvatar = findViewById(R.id.imgAvatar);
        mBtnAccept = findViewById(R.id.btnAccept);
        mBtnDeny = findViewById(R.id.btnDeny);
        mTxtNote = findViewById(R.id.txtNote);
        mTxtDistance = findViewById(R.id.txtDistance);
        mBtnDirect = findViewById(R.id.btnDirect);
        mProgressBar = findViewById(R.id.progressBar);


        mBtnAccept.setVisibility(View.VISIBLE);
        mBtnDeny.setVisibility(View.VISIBLE);
        mBtnDirect.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAccept:
                if (NetworkUtils.isNetworkConnected(this)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mBtnAccept.setEnabled(false);
                    mBtnDeny.setEnabled(false);
                    sendConfirm(2);
                }
                break;
            case R.id.btnDeny:
                if (NetworkUtils.isNetworkConnected(this)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mBtnDeny.setEnabled(false);
                    mBtnDeny.setEnabled(false);
                    sendConfirm(1);
                }
                break;
            case R.id.btnDirect:

                Intent intent = new Intent(this, VehicleMoveActivity.class);

                intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY, VehicleMoveActivity.CONFIRM_REQUEST);
                startActivity(intent);
                break;
        }

    }

    private void sendConfirm(int confirmId) {
        ConfirmRequestAPI confirmRequestAPI = new ConfirmRequestAPI(this);
        confirmRequestAPI.sendConfirmRequest(mApiToken, mReceiveRequest.getUserId(), confirmId);
    }

    @Override
    public void confirmRequestSuccess(int confirmId) {
        mProgressBar.setVisibility(View.GONE);
        if (confirmId == 2) {
           acceptRequestSuccess();

        } else {
           denyRequestSuccess();
        }

    }
    private void acceptRequestSuccess(){
        Toast.makeText(this, getResources().getString(R.string.request_accept_send_success), Toast.LENGTH_SHORT).show();
        mYourRequestInfo = new RequestInfo();
        mYourRequestInfo.setUserId(mReceiveRequest.getUserId());
        mYourRequestInfo.setAvatarLink(mReceiveRequest.getAvartarLink());
        mYourRequestInfo.setTimeStart(mReceiveRequest.getStartTime());
        mYourRequestInfo.setSourceLocation(mReceiveRequest.getStartLocation());
        mYourRequestInfo.setDestLocation(mReceiveRequest.getEndLocation());
        mYourRequestInfo.setVehicleType(mReceiveRequest.getVehicleType());
        if (mDatabaseHelper.insertRequestNotMe(mYourRequestInfo, mReceiveRequest.getUserId())) {
            Log.d("insertRequest", "success");
        }
        Log.d("accept request", "success");

        mBtnAccept.setVisibility(View.GONE);
        mBtnDeny.setVisibility(View.GONE);
        mBtnDirect.setVisibility(View.VISIBLE);
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
        mEditorScreen.commit();

    }
    private void denyRequestSuccess(){
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.ADDED_REQUEST);
        finish();
    }

    @Override
    public void confirmRequestFailure(String message, int confirmId) {
        mProgressBar.setVisibility(View.GONE);
        if (message.equals("1")) {
            finish();

            SharedPreferences sharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesScreen.edit();
            editor.putInt(MainActivity.SCREEN_NAME, MainActivity.ADDED_REQUEST);
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        }
    }
}
