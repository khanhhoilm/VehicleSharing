package vehiclessharing.vehiclessharing.fragment;

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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.R;
import vehiclessharing.vehiclessharing.adapter.PlaceAutocompleteAdapter;
import vehiclessharing.vehiclessharing.adapter.SpinerVehicleTypeAdapter;
import vehiclessharing.vehiclessharing.controller.RestManager;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.session.SessionManager;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

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
    private View view;
    private TextView txtTitle, txtTimeStart;

    private Button btnOk, btnCancel;
    private Spinner spType;
    private SpinerVehicleTypeAdapter adapter;
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private int CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;
    private int DES_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
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
    private int vehicleType=1;

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
     if(mGoogleApiClient==null) {
         mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                 .enableAutoManage(getActivity(), 0, this)
                 .addApi(Places.GEO_DATA_API)
                 .build();
     }
        view = inflater.inflate(R.layout.fragment_add_request, container, false);

        addControls();

        // Inflate the layout for this fragment
        return view;
    }

    private void addControls() {
        mContext = getActivity();
        txtTitle = (TextView) view.findViewById(R.id.txtNeederDialogTitle);

        switch (getWhatBtnClick) {
            case "btnFindPeople":
               // vehicleType=spType.getSelectedItemPosition()+1;
                txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_people));
                break;
            case "btnFindVehicles":
               // vehicleType=0;
                txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_vehicle));

        }
       /* if (getWhatBtnClick.equals("btnFindPeople")) {

        }
        if(getWhatBtnClick.equals("btnFindVehicles"))
        {

        }*/

        txtCurLocation = (EditText) view.findViewById(R.id.txtCurLocate);
        txtDesLocation = (EditText) view.findViewById(R.id.txtDesLocate);
        txtCurLocation.setOnClickListener(this);
        txtDesLocation.setOnClickListener(this);
      /* mAutocompleteCurLocation= (AutoCompleteTextView) view.findViewById(R.id.autocomplete_cur_places);
        mAutocompleteDesLocation= (AutoCompleteTextView) view.findViewById(R.id.autocomplete_des_places);
*/
        // Register a listener that receives callbacks when a suggestion has been selected
        //mAutocompleteCurLocation.setOnItemClickListener(mAutocompleteClickListener);

        txtTimeStart = (TextView) view.findViewById(R.id.txtTimeStart);
        btnOk = (Button) view.findViewById(R.id.btnAddOK);
        btnCancel = (Button) view.findViewById(R.id.btnAddCancel);


        imgClearCurLocation = (ImageView) view.findViewById(R.id.imgClearCurLocation);
        //set current location
        imgClearDesLocation = (ImageView) view.findViewById(R.id.imgClearDesLocation);
        txtTimeStart.setText(sdf1.format(calendar.getTime()));
        currentDay=sdf2.format(calendar.getTime());
        txtTimeStart.setOnClickListener(this);
        imgClearCurLocation.setOnClickListener(this);
        imgClearDesLocation.setOnClickListener(this);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        String curPosition= PlaceHelper.getInstance(mContext).getCurrentPlace();
        txtCurLocation.setText(curPosition);
        spType= (Spinner) view.findViewById(R.id.spVehicleType);
        List<String> type=new ArrayList<>();
        type.add("Xe máy");
        type.add("Ô tô");
        adapter=new SpinerVehicleTypeAdapter(mContext,type);
        spType.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
      /* if (mListener != null) {
            mListener.addRequestResult();
        }*/
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
                boolean checkEmpty=checkValidation();
                if(!checkEmpty)
                {
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
                vehicleType=spType.getSelectedItemPosition()+1;
             //   txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_people));
                break;
            case "btnFindVehicles":
                vehicleType=0;
              //  txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_vehicle));

        }
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("vsharing_is_login",Context.MODE_PRIVATE);
        int userId=sharedPreferences.getInt(SessionManager.USER_ID,3);
        String sessionId=sharedPreferences.getString(SessionManager.KEY_SESSION,"");
        final LatLng srcLatLng=PlaceHelper.getInstance(mContext).getLatLngByName(txtCurLocation.getText().toString());
        final String sourLocation="{\"lat\":\""+String.valueOf(srcLatLng.latitude)+"\",\"lng\":\""+String.valueOf(srcLatLng.longitude)+"\"}";

        final LatLng desLatLng=PlaceHelper.getInstance(mContext).getLatLngByName(txtDesLocation.getText().toString());
        final String desLocation="{\"lat\":\""+String.valueOf(desLatLng.latitude)+"\",\"lng\":\""+String.valueOf(desLatLng.longitude)+"\"}";

        final String time=txtTimeStart.getText().toString();
        String deviceId= Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken="";
       // mListener.addRequestSuccess(srcLatLng,desLatLng,time,vehicleType);

       // vehicleType=spType.getSelectedItemPosition()+1;
        //callback b/c some reasom about network or anything not right
     //   mListener.addRequestResult(,,txtTimeStart.getText());
        //callback to true if add request success
/*
* @Query("user_id") int userId, @Query("source_location") String sourceLocation,
                                         @Query("destination_location") String desLocation, @Query("time_start") String timeStart,
                                         @Query("api_token") String session, @Query("device_id") String devideId, @Query("vehicle_type") int vehicleType);
*/


        /*RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("source_location", sourLocation)
                .add("destination_location", sourLocation)
                .add("time_start", sourLocation)
                .add("api_token", deviceToken)
                .add("device_id", deviceId)
                .add("vehicle_type",String.valueOf(vehicleType))
                .build();
*/
      /*  mManager.getApiService().registerRequest(formBody).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (response.isSuccessful()) {
                    mListener.addRequestSuccess(srcLatLng, desLatLng, time, vehicleType,response.body().getActiveUsers());
                    dismiss();
                }else
                {
                    Toast.makeText(mContext, mContext.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

            }
        });
*/
        mManager.getApiService().registerRequest(userId,sourLocation,desLocation,time,sessionId,deviceId,vehicleType).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (response.isSuccessful()) {
                    List<ActiveUser> activeUserList=new ArrayList<ActiveUser>();
                    if(response.body().getActiveUsers().size()>0) {
                        activeUserList=response.body().getActiveUsers();

                    }else {
                        mListener.addRequestSuccess(srcLatLng, desLatLng, time, vehicleType, );
                    }

                            @Override
                            public boolean isEmpty() {
                                return false;
                            }

                            @Override
                            public boolean contains(Object o) {
                                return false;
                            }

                            @NonNull
                            @Override
                            public Iterator<ActiveUser> iterator() {
                                return null;
                            }

                            @NonNull
                            @Override
                            public Object[] toArray() {
                                return new Object[0];
                            }

                            @NonNull
                            @Override
                            public <T> T[] toArray(@NonNull T[] a) {
                                return null;
                            }

                            @Override
                            public boolean add(ActiveUser activeUser) {
                                return false;
                            }

                            @Override
                            public boolean remove(Object o) {
                                return false;
                            }

                            @Override
                            public boolean containsAll(@NonNull Collection<?> c) {
                                return false;
                            }

                            @Override
                            public boolean addAll(@NonNull Collection<? extends ActiveUser> c) {
                                return false;
                            }

                            @Override
                            public boolean addAll(int index, @NonNull Collection<? extends ActiveUser> c) {
                                return false;
                            }

                            @Override
                            public boolean removeAll(@NonNull Collection<?> c) {
                                return false;
                            }

                            @Override
                            public boolean retainAll(@NonNull Collection<?> c) {
                                return false;
                            }

                            @Override
                            public void clear() {

                            }

                            @Override
                            public ActiveUser get(int index) {
                                return null;
                            }

                            @Override
                            public ActiveUser set(int index, ActiveUser element) {
                                return null;
                            }

                            @Override
                            public void add(int index, ActiveUser element) {

                            }

                            @Override
                            public ActiveUser remove(int index) {
                                return null;
                            }

                            @Override
                            public int indexOf(Object o) {
                                return 0;
                            }

                            @Override
                            public int lastIndexOf(Object o) {
                                return 0;
                            }

                            @Override
                            public ListIterator<ActiveUser> listIterator() {
                                return null;
                            }

                            @NonNull
                            @Override
                            public ListIterator<ActiveUser> listIterator(int index) {
                                return null;
                            }

                            @NonNull
                            @Override
                            public List<ActiveUser> subList(int fromIndex, int toIndex) {
                                return null;
                            }
                        });
                    }
                        dismiss();
                }else
                {
                    Toast.makeText(mContext, mContext.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
               // mListener.addRequestSuccess(srcLatLng,desLatLng,time,vehicleType);
              //  dismiss();
                Toast.makeText(mContext, mContext.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

            }
        });
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
        String desLocate=txtDesLocation.getText().toString();
        boolean checkNull=false;
        if(curLocate.equals(""))
        {
            Toast.makeText(mContext, "Vị trí bắt đầu không được để trống", Toast.LENGTH_SHORT).show();
            //txtCurLocation.setError("Vị trí bắt đầu không nên để trống");
            checkNull=true;
        }
        else if(desLocate.equals(""))
        {
            Toast.makeText(mContext, "Vị trí kết thúc không được để trống", Toast.LENGTH_SHORT).show();
            //txtDesLocation.setError("Vị trí kết thúc không nên để trống");
            checkNull=true;
        }
        return checkNull;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        if (requestCode == CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            //    if (resultCode == 1) {
            if (txtCurLocation != null&&place!=null) {
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
            if(txtDesLocation!=null&&place!=null)
            {
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
    }
}
