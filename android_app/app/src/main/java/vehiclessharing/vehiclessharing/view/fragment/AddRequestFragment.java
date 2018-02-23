package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.AddRequestAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.interfaces.AuthenticationFail;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.view.adapter.SpinerVehicleTypeAdapter;

public class AddRequestFragment extends DialogFragment implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, AddRequestAPI.AddRequestInterfaceCallback {
    // TODO: Rename parameter arguments, choose names that match

    private static final String WHAT_BTN_CLICK = "what_btn_click";
    private static final String PRESENT_ADDRESS = "present_address";
    private int CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
    private int DES_PLACE_AUTOCOMPLETE_REQUEST_CODE = 4;

    private String mGetWhatBtnClick = "";
    private TextView mTxtTitle, mTxtTimeStart;

    private Button mBtnOk, mBtnCancel;
    private Spinner mSpType;
    private SpinerVehicleTypeAdapter mAdapter;
    protected GoogleApiClient mGoogleApiClient;

    private int mUserId;
    private String mSessionId = "", mRefreshedToken = "";
    private Context mContext;
    private LatLng mSrcLatLng, mDesLatLng;
    private String mTime = "";

    java.util.Calendar mCalendar;
    java.text.SimpleDateFormat mSimpleDateFormat;

    private ImageView mImgClearCurLocation, mImgClearDesLocation;
    private TextView mTxtCurLocation, txtDesLocation;
    private Drawable mDrawable;
    private int mVehicleType = 1;
    private String mPresentAdress = "";
    private ProgressBar mProgressBar;
    private OnFragmentAddRequestListener mListener;

    public AddRequestFragment() {
    }

    public static AddRequestFragment newInstance(String btnClick, String presentAddress) {
        AddRequestFragment fragment = new AddRequestFragment();
        Bundle args = new Bundle();
        args.putString(WHAT_BTN_CLICK, btnClick);
        args.putString(PRESENT_ADDRESS, presentAddress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGetWhatBtnClick = getArguments().getString(WHAT_BTN_CLICK);
            mPresentAdress = getArguments().getString(PRESENT_ADDRESS);
        }
        mCalendar = java.util.Calendar.getInstance();
        mSimpleDateFormat = new java.text.SimpleDateFormat("HH:mm");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), 0, this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
        View view = inflater.inflate(R.layout.fragment_add_request, container, false);

        addControls(view);
        mRefreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token FCM", "Token Value: " + mRefreshedToken);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SessionManager.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        mSessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        Log.d("User Info", "User_id: " + String.valueOf(mUserId) + ", api_token: " + String.valueOf(mSessionId));

        // Inflate the layout for this fragment
        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void addControls(View view) {
        mContext = getActivity();
        mTxtTitle = (TextView) view.findViewById(R.id.txtNeederDialogTitle);

        mTxtCurLocation = (EditText) view.findViewById(R.id.txtCurLocate);
        txtDesLocation = (EditText) view.findViewById(R.id.txtDesLocate);
        mTxtCurLocation.setOnClickListener(this);
        txtDesLocation.setOnClickListener(this);

        mTxtTimeStart = (TextView) view.findViewById(R.id.txtTimeStart);
        mBtnOk = (Button) view.findViewById(R.id.btnAddOK);
        mBtnCancel = (Button) view.findViewById(R.id.btnAddCancel);
        mProgressBar = view.findViewById(R.id.progressBar);


        mImgClearCurLocation = (ImageView) view.findViewById(R.id.imgClearCurLocation);
        //set current location
        mImgClearDesLocation = (ImageView) view.findViewById(R.id.imgClearDesLocation);
        mTxtTimeStart.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        mTxtTimeStart.setOnClickListener(this);
        mImgClearCurLocation.setOnClickListener(this);
        mImgClearDesLocation.setOnClickListener(this);

        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        if (!mPresentAdress.equals("")) {
            mTxtCurLocation.setText(mPresentAdress);
        } else {
            try {
                mTxtCurLocation.setText(PlaceHelper.getInstance(mContext).getCurrentPlace(MainActivity.mGoogleMap));
            } catch (Exception e) {
                mTxtCurLocation.setText("");

            }
        }
        mSpType = (Spinner) view.findViewById(R.id.spVehicleType);
        List<String> type = new ArrayList<>();
        type.add("Xe máy");
        type.add("Ô tô");
        mAdapter = new SpinerVehicleTypeAdapter(mContext, type);
        mSpType.setAdapter(mAdapter);
        switch (mGetWhatBtnClick) {
            case "btnFindPeople":
                mTxtTitle.setText(mContext.getResources().getString(R.string.dialog_find_people));
                mSpType.setVisibility(View.VISIBLE);
                break;
            case "btnFindVehicles":
                // vehicleType=0;
                mTxtTitle.setText(mContext.getResources().getString(R.string.dialog_find_vehicle));
                mSpType.setVisibility(View.GONE);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAddRequestListener) {
            mListener = (OnFragmentAddRequestListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        mListener = null;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtCurLocate: {
                mTxtCurLocation.setEnabled(false);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            }
            case R.id.txtDesLocate:
                txtDesLocation.setEnabled(false);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, DES_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            case R.id.btnAddOK:

                boolean checkEmpty = checkValidation();
                if (!checkEmpty) {
                    mBtnOk.setEnabled(false);
                    mBtnCancel.setEnabled(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                    sendRequestToServer();
                    MainActivity.sBtnAddClick = false;
                }
                break;
            case R.id.btnAddCancel:
                dismiss();
                MainActivity.sBtnAddClick = false;
                break;
            case R.id.imgClearCurLocation:
                mTxtCurLocation.setText("");
                break;
            case R.id.imgClearDesLocation:
                txtDesLocation.setText("");
                break;
            case R.id.txtTimeStart:
                showTimePicker();
        }
    }

    private void sendRequestToServer() {
        try {
            switch (mGetWhatBtnClick) {
                case "btnFindPeople":
                    mVehicleType = mSpType.getSelectedItemPosition() + 1;
                    break;
                case "btnFindVehicles":
                    mVehicleType = 0;

            }

            mSrcLatLng = PlaceHelper.getInstance(mContext).getLatLngByName(mTxtCurLocation.getText().toString());
            final String sourLocation = "{\"lat\":\"" + String.valueOf(mSrcLatLng.latitude) + "\",\"lng\":\"" + String.valueOf(mSrcLatLng.longitude) + "\"}";


            mDesLatLng = PlaceHelper.getInstance(mContext).getLatLngByName(txtDesLocation.getText().toString());
            final String desLocation = "{\"lat\":\"" + String.valueOf(mDesLatLng.latitude) + "\",\"lng\":\"" + String.valueOf(mDesLatLng.longitude) + "\"}";

            mTime = mTxtTimeStart.getText().toString();
            String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceToken = FirebaseInstanceId.getInstance().getToken();

            Log.d("Token FCM", "Token Value: " + deviceToken);
            Log.d("DeviceId", "Device Id Value: " + deviceId);

            java.util.Calendar calendarCurrent = java.util.Calendar.getInstance();
            java.text.SimpleDateFormat simpleDateFormatCurrent = new java.text.SimpleDateFormat("HH:mm");

            String currentTime = simpleDateFormatCurrent.format(calendarCurrent.getTime());

            AddRequestAPI addRequestAPI = new AddRequestAPI(this);
            addRequestAPI.addRequest(mUserId, sourLocation, desLocation, mTime, mSessionId, deviceId, mVehicleType,
                    deviceToken, currentTime);

        } catch (Exception e) {
            Toast.makeText(mContext, "send request to server ", Toast.LENGTH_SHORT).show();
        }

    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                mCalendar.set(java.util.Calendar.HOUR_OF_DAY, i);
                mCalendar.set(java.util.Calendar.MINUTE, i1);
                mTxtTimeStart.setText(mSimpleDateFormat.format(mCalendar.getTime()));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                mContext,
                callBack,
                mCalendar.get(java.util.Calendar.HOUR_OF_DAY),
                mCalendar.get(java.util.Calendar.MINUTE), true
        );
        timePickerDialog.show();
    }

    private boolean checkValidation() {
        String curLocate = mTxtCurLocation.getText().toString();
        String desLocate = txtDesLocation.getText().toString();
        boolean checkNull = false;
        if (curLocate.equals("")) {
            Toast.makeText(mContext, "Vị trí bắt đầu không được để trống", Toast.LENGTH_SHORT).show();
            checkNull = true;
        } else if (desLocate.equals("")) {
            Toast.makeText(mContext, "Vị trí kết thúc không được để trống", Toast.LENGTH_SHORT).show();
            checkNull = true;
        }
        return checkNull;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTxtCurLocation.setEnabled(true);
        txtDesLocation.setEnabled(true);
        Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        if (requestCode == CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            //    if (resultCode == 1) {
            if (mTxtCurLocation != null && place != null) {
                mTxtCurLocation.setText(place.getAddress());
            }

        } else if (requestCode == DES_PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            if (txtDesLocation != null && place != null) {
                txtDesLocation.setText(place.getAddress());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void addRequestSuccess(RequestResult requestResult) {
        mProgressBar.setVisibility(View.GONE);
        List<ActiveUser> listActive = new ArrayList<>();
        Log.d("registerRequest", "registerRequest success");
        if (requestResult.getActiveUsers() != null && requestResult.getActiveUsers().size() > 0) {
            listActive = requestResult.getActiveUsers();
        } else {
            String vType = "";
            if (mVehicleType == 0) {
                vType = "chia sẻ xe";
            } else {
                vType = "cần xe chia sẻ";
            }
            Toast.makeText(mContext, "Không có người nào đang " + vType, Toast.LENGTH_SHORT).show();
        }
        mListener.addRequestSuccess(mSrcLatLng, mDesLatLng, mTime, mVehicleType, listActive);

        if (isAdded()) {
            dismiss();
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void addRequestUnsuccess(int statusCode) {
        switch (statusCode) {
            case 403:
                AuthenticationFail.unAuthorized(getActivity(), getActivity().getSupportFragmentManager());
                break;
            default:
                Toast.makeText(mContext, getString(R.string.add_request_fail), Toast.LENGTH_SHORT).show();
                mBtnOk.setEnabled(true);
                mBtnCancel.setEnabled(true);
                break;
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void addRequestFailure() {
        mProgressBar.setVisibility(View.GONE);
        if (isAdded()) {
            mBtnOk.setEnabled(true);
            mBtnCancel.setEnabled(true);
            Toast.makeText(mContext, getString(R.string.add_request_fail), Toast.LENGTH_SHORT).show();
        }
    }


    public interface OnFragmentAddRequestListener {
        // TODO: Update argument type and name
        void addRequestSuccess(LatLng cur, LatLng des, String time, int type, List<ActiveUser> list);

        void addRequestFailure();

        void cancelRequestSuccess(boolean success);
    }
}
