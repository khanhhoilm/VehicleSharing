package vehiclessharing.vehiclessharing.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ConfirmRequest;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class ReceiveConfirmRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private ConfirmRequest confirmRequest;
    private CircleImageView imgAvatar;
    private TextView txtUserName, txtSourLocation, txtEndLocation, txtTimeStart, txtVehicleType;
    private Button btnDirect;
    private DatabaseHelper databaseHelper;
    private String dataReceive;
    private RequestInfo yourRequestInfo;
    private int journeyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_confirm_request);
        Bundle bundle = getIntent().getExtras();
        dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");

        Gson gson = new Gson();
        confirmRequest = gson.fromJson(dataReceive, ConfirmRequest.class);

        yourRequestInfo = new RequestInfo();
        yourRequestInfo.setVehicleType(confirmRequest.getVehicleType());
        yourRequestInfo.setSourceLocation(confirmRequest.getStartLocation());
        yourRequestInfo.setDestLocation(confirmRequest.getEndLocation());
        yourRequestInfo.setTimeStart(confirmRequest.getStartTime());

        addControls();
        addEvents();
        loadContent();
    }

    private void loadContent() {
        txtUserName.setText(confirmRequest.getUserName());
        try {
            String sourceLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(confirmRequest.getStartLocation());
            txtSourLocation.setText(sourceLocation);
            String desLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(confirmRequest.getEndLocation());
            txtEndLocation.setText(desLocation);
            txtTimeStart.setText(confirmRequest.getStartTime());
            // databaseHelper.insertRequest(confirmRequest,confirmRequest.getUserId())
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addEvents() {
        btnDirect.setOnClickListener(this);
    }

    private void addControls() {
        txtUserName = findViewById(R.id.txtUserName);
        txtTimeStart = findViewById(R.id.txtTimeStart);
        txtSourLocation = findViewById(R.id.txtSource);
        txtEndLocation = findViewById(R.id.txtDestination);
        txtVehicleType = findViewById(R.id.txtVehicleType);
        btnDirect = findViewById(R.id.btnDirect);
        databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.insertRequestNotMe(yourRequestInfo, confirmRequest.getUserId())) {
            Log.d("insertDatabase", "success");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDirect) {
            Intent intent = new Intent(ReceiveConfirmRequestActivity.this, VehicleMoveActivity.class);
            intent.putExtra(CustomFirebaseMessagingService.DATA_RECEIVE, dataReceive);
            intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY,"receive_confirm_request");
            startActivity(intent);
        }
    }
}
