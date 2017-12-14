package vehiclessharing.vehiclessharing.controller.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.api.ApiService;
import vehiclessharing.vehiclessharing.controller.adapter.PlaceAutocompleteAdapter;
import vehiclessharing.vehiclessharing.controller.adapter.SpinerVehicleTypeAdapter;
import vehiclessharing.vehiclessharing.api.RelateRequestAPI;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

//import vehiclessharing.vehiclessharing.push.CustomFirebaseInstanceIDService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * to handle interaction events.
 * Use the {@link AddRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRequestFragment extends DialogFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WHAT_BTN_CLICK = "what_btn_click";
    private String getWhatBtnClick = "";
    private TextView txtTitle, txtTimeStart;

    private Button btnOk, btnCancel;
    private Spinner spType;
    private SpinerVehicleTypeAdapter adapter;
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private int CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;
    private int DES_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private RelateRequestAPI.CancelRequestCallBack cancelRequestCallBack;
    private int userId;
    private String sessionId="",refreshedToken="";
    //private AutoCompleteTextView mAutocompleteCurLocation,mAutocompleteDesLocation;

    private Context mContext;


    java.util.Calendar calendar = java.util.Calendar.getInstance();
    java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("dd/MM/yyyy");
    private static final LatLngBounds myBound = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    private ImageView imgClearCurLocation, imgClearDesLocation;
    private TextView txtCurLocation, txtDesLocation;
    private Drawable mDrawable;
    private RestManager mManager;
    private String currentDay;
    private int vehicleType = 1;

    private OnFragmentAddRequestListener mListener;

    public AddRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param btnClick Parameter 1.
     * @return A new instance of fragment AddRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRequestFragment newInstance(String btnClick) {
        AddRequestFragment fragment = new AddRequestFragment();
        Bundle args = new Bundle();
        args.putString(WHAT_BTN_CLICK, btnClick);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getWhatBtnClick = getArguments().getString(WHAT_BTN_CLICK);
        }
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
        txtTitle = (TextView) view.findViewById(R.id.txtNeederDialogTitle);

        txtCurLocation = (EditText) view.findViewById(R.id.txtCurLocate);
        txtDesLocation = (EditText) view.findViewById(R.id.txtDesLocate);
        txtCurLocation.setOnClickListener(this);
        txtDesLocation.setOnClickListener(this);

        txtTimeStart = (TextView) view.findViewById(R.id.txtTimeStart);
        btnOk = (Button) view.findViewById(R.id.btnAddOK);
        btnCancel = (Button) view.findViewById(R.id.btnAddCancel);


        imgClearCurLocation = (ImageView) view.findViewById(R.id.imgClearCurLocation);
        //set current location
        imgClearDesLocation = (ImageView) view.findViewById(R.id.imgClearDesLocation);
        txtTimeStart.setText(sdf1.format(calendar.getTime()));
        currentDay = sdf2.format(calendar.getTime());
        txtTimeStart.setOnClickListener(this);
        imgClearCurLocation.setOnClickListener(this);
        imgClearDesLocation.setOnClickListener(this);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        String curPosition = PlaceHelper.getInstance(mContext).getCurrentPlace();
        txtCurLocation.setText(curPosition);
        spType = (Spinner) view.findViewById(R.id.spVehicleType);
        List<String> type = new ArrayList<>();
        type.add("Xe máy");
        type.add("Ô tô");
        adapter = new SpinerVehicleTypeAdapter(mContext, type);
        spType.setAdapter(adapter);
        switch (getWhatBtnClick) {
            case "btnFindPeople":
                // vehicleType=spType.getSelectedItemPosition()+1;

                txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_people));
                spType.setVisibility(View.VISIBLE);
                break;
            case "btnFindVehicles":
                // vehicleType=0;
                txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_vehicle));
                spType.setVisibility(View.GONE);

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

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token FCM", "Token Value: " + refreshedToken);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SessionManager.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        sessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        Log.d("User Info", "User_id: " + String.valueOf(userId) + ", api_token: " + String.valueOf(sessionId));

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
                //   int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
                try {
                    //   txtCurLocation.setText("");
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
                // txtDesLocation.setText("");

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
                    btnOk.setEnabled(false);
                    sendRequestToServer();
                }
                break;
            case R.id.btnAddCancel:
                dismiss();

                break;
            case R.id.imgClearCurLocation:
                txtCurLocation.setText("");
                break;
            case R.id.imgClearDesLocation:
                txtDesLocation.setText("");
                break;
            case R.id.txtTimeStart:
                showTimePicker();
        }
    }

    private void sendRequestToServer() {
        switch (getWhatBtnClick) {
            case "btnFindPeople":
                vehicleType = spType.getSelectedItemPosition() + 1;
                //   txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_people));
                break;
            case "btnFindVehicles":
                vehicleType = 0;
                //  txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_vehicle));

        }

        final LatLng srcLatLng = PlaceHelper.getInstance(mContext).getLatLngByName(txtCurLocation.getText().toString());
        final String sourLocation = "{\"lat\":\"" + String.valueOf(srcLatLng.latitude) + "\",\"lng\":\"" + String.valueOf(srcLatLng.longitude) + "\"}";

        final LatLng desLatLng = PlaceHelper.getInstance(mContext).getLatLngByName(txtDesLocation.getText().toString());
        final String desLocation = "{\"lat\":\"" + String.valueOf(desLatLng.latitude) + "\",\"lng\":\"" + String.valueOf(desLatLng.longitude) + "\"}";

        final String time = txtTimeStart.getText().toString();
        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("Token FCM", "Token Value: " + deviceToken);
        Log.d("DeviceId", "Device Id Value: " + deviceId);

        mManager.getApiService().registerRequest(userId, sourLocation, desLocation, time, sessionId, deviceId, vehicleType, deviceToken).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    Log.d("registerRequest","registerRequest success");
                    List<ActiveUser> activeUserList = new ArrayList<ActiveUser>();
                    //cancelRequestCallBack.addRequestSuccessful(sourLocation, desLocation, time, vehicleType, response.body().getActiveUsers());

                    mListener.addRequestSuccess(srcLatLng, desLatLng, time, vehicleType, response.body().getActiveUsers());
                    if (isAdded()) {
                        dismiss();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.add_request_fail), Toast.LENGTH_SHORT).show();
                    btnOk.setEnabled(true);
                }

            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                if(isAdded()) {
                    btnOk.setEnabled(true);
                    Toast.makeText(mContext, getString(R.string.add_request_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //}
        //  RelateRequestAPI.getInstance(this).addRequest(userId, sourLocation, desLocation, time, sessionId, deviceId, vehicleType,deviceToken );
    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //Hiển thị sự thay đổi theo người dùng
                calendar.set(java.util.Calendar.HOUR_OF_DAY, i);
                calendar.set(java.util.Calendar.MINUTE, i1);
                txtTimeStart.setText(sdf1.format(calendar.getTime()));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                mContext,
                callBack,
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE), true
        );

        //nếu đối số cuối =true thì định dạng 24h, =false định dạng 12h
        timePickerDialog.show();
    }

    private boolean checkValidation() {
        String curLocate = txtCurLocation.getText().toString();
        String desLocate = txtDesLocation.getText().toString();
        boolean checkNull = false;
        if (curLocate.equals("")) {
            Toast.makeText(mContext, "Vị trí bắt đầu không được để trống", Toast.LENGTH_SHORT).show();
            //txtCurLocation.setError("Vị trí bắt đầu không nên để trống");
            checkNull = true;
        } else if (desLocate.equals("")) {
            Toast.makeText(mContext, "Vị trí kết thúc không được để trống", Toast.LENGTH_SHORT).show();
            //txtDesLocation.setError("Vị trí kết thúc không nên để trống");
            checkNull = true;
        }
        return checkNull;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        if (requestCode == CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            //    if (resultCode == 1) {
            if (txtCurLocation != null && place != null) {
                txtCurLocation.setText(place.getAddress());
            }

//            Log.d("onActivityResult", "Place: " + place.getName() + "Address: " + place.getAddress());
            /*else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);*/
            // TODO: Handle the error.
            //Log.i(TAG, status.getStatusMessage());

          /*  } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }*/
        } else if (requestCode == DES_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (txtDesLocation != null && place != null) {
                txtDesLocation.setText(place.getAddress());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            // Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getActivity(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            //   Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                //  Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            /*// Format details of the place for display and show it in a TextView.
            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i(TAG, "Place details received: " + place.getName());
*/
            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        //Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
        //        websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }


    public interface OnFragmentAddRequestListener {
        // TODO: Update argument type and name
        void addRequestSuccess(LatLng cur, LatLng des, String time, int type, List<ActiveUser> list);

        void addRequestFailure();

        void cancelRequestSuccess(boolean success);
    }
}
