package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ConfirmRequest;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class ReceiveConfirmRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private ConfirmRequest mConfirmRequest;
    private CircleImageView mAvatar;
    private TextView mTxtUserName, mTxtSourLocation, mTxtEndLocation, mTxtTimeStart;
    private Button mBtnDirect;
    private DatabaseHelper mDatabaseHelper;
    private String mDataReceive;
    private RequestInfo mYourRequestInfo;
    private SharedPreferences mSharedPreferencesScreen;
    private SharedPreferences.Editor mEditorScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_confirm_request);
        Bundle bundle = getIntent().getExtras();
        mDataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");

        Gson gson = new Gson();
        mConfirmRequest = gson.fromJson(mDataReceive, ConfirmRequest.class);

        mYourRequestInfo = new RequestInfo();
        mYourRequestInfo.setUserId(mConfirmRequest.getUserId());
        mYourRequestInfo.setAvatarLink(mConfirmRequest.getAvartarLink());
        mYourRequestInfo.setVehicleType(mConfirmRequest.getVehicleType());
        mYourRequestInfo.setSourceLocation(mConfirmRequest.getStartLocation());
        mYourRequestInfo.setDestLocation(mConfirmRequest.getEndLocation());
        mYourRequestInfo.setTimeStart(mConfirmRequest.getStartTime());

        addControls();
        addEvents();
        loadContent();

        mSharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);
        mEditorScreen = mSharedPreferencesScreen.edit();
        mEditorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
        mEditorScreen.commit();
    }

    private void loadContent() {
        mTxtUserName.setText(mConfirmRequest.getUserName());
        try {
            String sourceLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(mConfirmRequest.getStartLocation());
            mTxtSourLocation.setText(sourceLocation);
            String desLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(mConfirmRequest.getEndLocation());
            mTxtEndLocation.setText(desLocation);
            mTxtTimeStart.setText(mConfirmRequest.getStartTime());
            if (mConfirmRequest.getAvartarLink() != null && !mConfirmRequest.getAvartarLink().equals("")) {
                Glide.with(this).load(mConfirmRequest.getAvartarLink()).placeholder(getResources()
                        .getDrawable(R.drawable.temp)).into(mAvatar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEvents() {
        mBtnDirect.setOnClickListener(this);
    }

    private void addControls() {
        mTxtUserName = findViewById(R.id.txtUserName);
        mAvatar = findViewById(R.id.imgAvatar);
        mTxtTimeStart = findViewById(R.id.txtTimeStart);
        mTxtSourLocation = findViewById(R.id.txtSource);
        mTxtEndLocation = findViewById(R.id.txtDestination);
        mBtnDirect = findViewById(R.id.btnDirect);
        mDatabaseHelper = new DatabaseHelper(this);
        if (mDatabaseHelper.insertRequestNotMe(mYourRequestInfo, mConfirmRequest.getUserId())) {
            Log.d("insertDatabase", "success");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDirect) {
            Intent intent = new Intent(ReceiveConfirmRequestActivity.this, VehicleMoveActivity.class);
            intent.putExtra(CustomFirebaseMessagingService.DATA_RECEIVE, mDataReceive);
            intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY, VehicleMoveActivity.RECEIVE_CONFIRM_REQUEST);
            startActivity(intent);
        }
    }
}
