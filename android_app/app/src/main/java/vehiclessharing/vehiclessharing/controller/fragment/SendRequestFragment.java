package vehiclessharing.vehiclessharing.controller.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.vehiclessharing.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link .} interface
 * to handle interaction events.
 * Use the {@link SendRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendRequestFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String API_TOKEN = "api_token";
    private static final String RECEIVER_ID = "receiver_id";

    // TODO: Rename and change types of parameters
    private String apiToken;
    private int receiverId;
    private EditText txtNote;
    private Button btnSend, btnCancel;
    private RestManager restManager;
    private Activity mActivity;
    private static SendRequestCallBack sendRequestCallBack;

    public SendRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SendRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendRequestFragment newInstance(String apiToken, int receiverId,SendRequestCallBack callBack) {
        sendRequestCallBack=callBack;
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
            apiToken = getArguments().getString(API_TOKEN);
            receiverId = getArguments().getInt(RECEIVER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void addEvents() {
        btnSend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    private void addControls(View view) {
        txtNote = (EditText) view.findViewById(R.id.txtNote);
        btnSend = (Button) view.findViewById(R.id.btnSendrequest);
        btnCancel = (Button) view.findViewById(R.id.btnCancelSend);
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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
        Log.d("sendRequestTogether", "api_token: " + apiToken + ", receiver_id: " + String.valueOf(receiverId));
        String note = txtNote.getText().toString();
        restManager.getApiService().sendRequestTogether(apiToken, receiverId, note).enqueue(new Callback<ResultSendRequest>() {
            @Override
            public void onResponse(Call<ResultSendRequest> call, Response<ResultSendRequest> response) {
                if (response.isSuccessful() && response.body().getStatus().getError()==0) {
               if(isAdded()) {
                   dismiss();
                   Toast.makeText(mActivity, mActivity.getString(R.string.wait_accept), Toast.LENGTH_SHORT).show();
               }
                    sendRequestCallBack.sendRequestSuccess();
                }else {
                    Toast.makeText(mActivity, "Send failed", Toast.LENGTH_SHORT).show();
                   if(isAdded()) {
                       dismiss();
                   }
                }
            }

            @Override
            public void onFailure(Call<ResultSendRequest> call, Throwable t) {
                Toast.makeText(mActivity, mActivity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }
    public interface SendRequestCallBack {
        // TODO: Update argument type and name
        void sendRequestSuccess();
    }
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



