package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.api.SendRequestAPI;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;

public class SendRequestFragment extends DialogFragment implements View.OnClickListener, SendRequestAPI.SendRequestInterface {
    private static final String API_TOKEN = "api_token";
    private static final String RECEIVER_ID = "receiver_id";

    private String mApiToken;
    private int mReceiverId;
    private EditText mTxtNote;
    private Button mBtnSend, mBtnCancel;
    private Activity mActivity;
    private static SendRequestCallBack sSendRequestCallBack;

    public SendRequestFragment() {

    }

    public static SendRequestFragment newInstance(String apiToken, int receiverId, SendRequestCallBack callBack) {
        sSendRequestCallBack = callBack;
        SendRequestFragment fragment = new SendRequestFragment();
        Bundle args = new Bundle();
        args.putString(API_TOKEN, apiToken);
        args.putInt(RECEIVER_ID, receiverId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mApiToken = getArguments().getString(API_TOKEN);
            mReceiverId = getArguments().getInt(RECEIVER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);
        mActivity = getActivity();
        addControls(view);
        addEvents();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
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
                sendRequestToChosenUser();
                break;
            case R.id.btnCancelSend:
                dismiss();
                break;
        }
    }

    public void sendRequestToChosenUser() {
        Log.d("sendRequestTogether", "api_token: " + mApiToken + ", receiver_id: " + String.valueOf(mReceiverId));
        String note = mTxtNote.getText().toString();

        SendRequestAPI sendRequestAPI = new SendRequestAPI(this);
        sendRequestAPI.sendRequestToChosenUser(mApiToken, mReceiverId, note);
    }

    @Override
    public void sendRequestSuccess() {
        if (isAdded()) {
            dismiss();
            Toast.makeText(mActivity, mActivity.getString(R.string.wait_accept), Toast.LENGTH_SHORT).show();
        }
        sSendRequestCallBack.sendRequestSuccess();

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
        String currentTime = sdf1.format(calendar.getTime());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_CONFIRM);
        editor.putString(MainActivity.TIME_SEND_REQUEST, currentTime);
        editor.commit();
    }

    @Override
    public void sendRequestFailure() {
        Toast.makeText(mActivity, mActivity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    public interface SendRequestCallBack {
        void sendRequestSuccess();
    }
}




