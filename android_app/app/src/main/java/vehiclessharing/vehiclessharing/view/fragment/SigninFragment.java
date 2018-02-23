package vehiclessharing.vehiclessharing.view.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.api.SignInAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.SignInResponse;
import vehiclessharing.vehiclessharing.model.Validation;
import vehiclessharing.vehiclessharing.utils.HashAlgorithm;
import vehiclessharing.vehiclessharing.viewscustom.CustomToast;

public class SigninFragment extends Fragment implements View.OnClickListener,SignInAPI.SignInInterfaceCallback {
    private static View view;

    private EditText mTxtPhone, mTxtPassword;
    private Button mBtnLogin;
    private TextView mForgotPassword;
    private TextView mSignUp;
    private LinearLayout mLoginLayout;
    private Animation mShakeAnimation;
    private FragmentManager mFragmentManager;
    private ProgressDialog mProgress;
    private SessionManager sSession;
    private int mTimeLoginFailed = 0;

    public SigninFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        addControls();
        addEvents();
        return view;
    }


    private void addControls() {
        mFragmentManager = getActivity().getSupportFragmentManager(); // fragement manager to switch fragment another

        sSession = new SessionManager(getActivity());

        //[Start] Setup for progress
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignIn);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
//        //[End] Setup for progress

        mTxtPhone = (EditText) view.findViewById(R.id.txtPhone);
        mTxtPassword = (EditText) view.findViewById(R.id.txtPassword);
        mBtnLogin = (Button) view.findViewById(R.id.btnLogin);
        mForgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        mSignUp = (TextView) view.findViewById(R.id.createAccount);
        mLoginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        mShakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);
    }

    private void addEvents() {
        mBtnLogin.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                mProgress.show();
                if (checkValidation()) {
                    login();
                }
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
               /* fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();*/
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUpFragment()).commit();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private boolean checkValidation() {
        boolean checkValidate = false;

        String phoneNumber = mTxtPhone.getText().toString();
        String password = mTxtPassword.getText().toString();

        if (Validation.isEmpty(phoneNumber) || Validation.isEmpty(password)) {
            mLoginLayout.startAnimation(mShakeAnimation);
            new CustomToast().showToast(getActivity(), view,
                    Utils.EnterBothCredentials);

        }

        else {
            Validation validatePhone = Validation.checkValidPhone(phoneNumber);
            Validation validatePassword = Validation.checkValidPassword(password);
            if (!validatePhone.getIsValid()) {
                new CustomToast().showToast(getActivity(), view,
                        validatePhone.getMessageValid());
            }
            if (!validatePassword.getIsValid()) {
                new CustomToast().showToast(getActivity(), view,
                        validatePassword.getMessageValid());
            }

            if (validatePhone.getIsValid() && validatePassword.getIsValid()) {
                checkValidate = true;

            }
        }
        return checkValidate;
    }

    private void login() {
        String password = HashAlgorithm.md5(mTxtPassword.getText().toString());
        Log.d("pss", "pw" + password);
        String phoneNumber = mTxtPhone.getText().toString();

        SignInAPI.getInstance(this).signIn(phoneNumber,password);
    }

    @Override
    public void signInSuccess(SignInResponse signInResult) {
        mProgress.dismiss();
        sSession.createLoginSession(signInResult.getData().getUserInfo().getId(), signInResult.getData().getApiToken());
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        databaseHelper.insertUser(signInResult.getData().getUserInfo());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        Bundle bd = new Bundle();
        intent.putExtras(bd);
        startActivity(intent);
    }

    @Override
    public void signInUnsuccess(SignInResponse signInResult) {
        String warnningMessage = "";
        if (mTimeLoginFailed < 3) {
            warnningMessage = "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại";
        } else {
            warnningMessage = "Bạn đã nhập sai thông tin đăng nhập quá 3 lần. Nếu chưa có tài khoản vui lòng vào trang đăng ký";
        }
        new CustomToast().showToast(getActivity(), view, warnningMessage);

        mTimeLoginFailed++;
        mProgress.dismiss();
    }

    @Override
    public void signInFailure() {
        Log.d("Signin", "onFailure");
        new CustomToast().showToast(getActivity(), view, "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại");

        mProgress.dismiss();
    }
}
