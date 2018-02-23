package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.CancelTripAPI;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link .} interface
 * to handle interaction events.
 * Use the {@link SendRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CancelTripDialog extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_TOKEN = "api_token";
    private static final String VEHICLE_TYPE = "vehicle_type";

    // TODO: Rename and change types of parameters
    private static CancelTripAPI.CancelTripInterface sCancelTripInterface;

    private String mApiToken;
    private int mVehicleType;
    private EditText mTxtNote;
    private Button mBtnSend, mBtnCancel;

    public CancelTripDialog() {

    }

    public static CancelTripDialog newInstance(String apiToken, int vehType, CancelTripAPI.CancelTripInterface tripInterface) {
        sCancelTripInterface = tripInterface;
        CancelTripDialog fragment = new CancelTripDialog();
        Bundle args = new Bundle();
        args.putString(API_TOKEN, apiToken);
        args.putInt(VEHICLE_TYPE, vehType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mApiToken = getArguments().getString(API_TOKEN);
            mVehicleType = getArguments().getInt(VEHICLE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cancel_trip, container, false);

        addControls(view);
        addEvents();

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

    private void addEvents() {
        mBtnSend.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

    }

    private void addControls(View view) {
        mTxtNote = (EditText) view.findViewById(R.id.txtNote);
        mBtnSend = (Button) view.findViewById(R.id.btnSendrequest);
        mBtnCancel = (Button) view.findViewById(R.id.btnCancelSend);
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendrequest:
                cancelTrip();
                break;
            case R.id.btnCancelSend:
                dismiss();
                break;
        }
    }

    private void cancelTrip() {
        String comment = "";
        if (mTxtNote.getText().length() > 0) {
            comment = mTxtNote.getText().toString();
        }
        CancelTripAPI cancelTripAPI = new CancelTripAPI(sCancelTripInterface);
        cancelTripAPI.cancel(mApiToken, mVehicleType, comment);
    }
}



