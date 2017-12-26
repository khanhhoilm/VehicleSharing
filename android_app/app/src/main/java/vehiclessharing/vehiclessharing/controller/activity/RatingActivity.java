package vehiclessharing.vehiclessharing.controller.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.GetUserInfo;
import vehiclessharing.vehiclessharing.api.RatingUserTogether;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.permission.CheckInternetAndLocation;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        RatingBar.OnRatingBarChangeListener, RatingUserTogether.RatingCallback, GetUserInfo.GetInfoUserCallback {
    private TextView txtName, txtSource, txtDes, txtTime, txtComment;
    private ImageView imgAvatar, imgType;

    private RatingBar ratingBar;
    private Button btnSend;
    private int journeyId;
    private String apiToken = "";
    private DatabaseHelper databaseHelper;
    private RequestInfo yourRequestInfo;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferencesScreen;
    private SharedPreferences.Editor editorScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        apiToken = MainActivity.sessionId;
        Bundle bundle = getIntent().getExtras();
        journeyId = bundle.getInt("journey_id", 0);
        sharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);
        if (journeyId == 0) {
            journeyId = sharedPreferencesScreen.getInt(VehicleMoveActivity.JOURNEY_ID, 0);
        }
        databaseHelper = new DatabaseHelper(this);
        yourRequestInfo = databaseHelper.getRequestInfoNotMe(MainActivity.userId);
        addControls();
        loadUI();
        addEvents();
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
        editorScreen.commit();
    }

    private void addEvents() {
        btnSend.setOnClickListener(this);
        txtComment.setOnFocusChangeListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
    }

    private void addControls() {
        txtName = findViewById(R.id.txtFullName);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtSource = findViewById(R.id.txtSourceLocation);
        txtDes = findViewById(R.id.txtDesLocation);
        txtTime = findViewById(R.id.txtTimeStartEnd);
        imgType = findViewById(R.id.imgVehicleType);
        ratingBar = findViewById(R.id.rbRating);
        txtComment = findViewById(R.id.txtWriteComment);
        btnSend = findViewById(R.id.btnSendRating);
        progressBar = findViewById(R.id.progressBar);

    }

    private void loadUI() {
        GetUserInfo.getInstance(this).getUserInfoFromAPI(MainActivity.sessionId, yourRequestInfo.getUserId());
        try {
            txtSource.setText(PlaceHelper.getInstance(this).getAddressByLatLngLocation(yourRequestInfo.getSourceLocation()));
            txtDes.setText(PlaceHelper.getInstance(this).getAddressByLatLngLocation(yourRequestInfo.getDestLocation()));
            txtTime.setText(yourRequestInfo.getTimeStart());
            switch (yourRequestInfo.getVehicleType()) {
                case 0:
                    imgType.setImageResource(R.drawable.ic_directions_run_indigo_700_24dp);
                    break;
                case 1:
                    imgType.setImageResource(R.drawable.ic_directions_car_indigo_a700_24dp);
                    break;
                case 2:
                    imgType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSendRating:
                if (CheckInternetAndLocation.isOnline(this)) {
                    btnSend.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    if (journeyId != 0) {
                        RatingUserTogether.getInstance(this).rating(apiToken, journeyId, ratingBar.getRating(), txtComment.getText().toString());
                    }
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        btnSend.setAlpha(1);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        btnSend.setAlpha(1);
    }

    @Override
    public void ratingSuccess() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "rating success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        editorScreen.commit();
    }

    @Override
    public void ratingFailure(String message) {
        progressBar.setVisibility(View.GONE);
        btnSend.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getInfoUserSuccess(User userInfo) {
        txtName.setText(userInfo.getName());
        if (userInfo.getAvatarLink() != null && !userInfo.equals("")) {
            Glide.with(this).load(userInfo.getAvatarLink()).placeholder(R.drawable.temp)
                    .error(R.drawable.temp).centerCrop().into(imgAvatar);
        }
    }

    @Override
    public void getUserInfoFailure(String message) {

    }
}
