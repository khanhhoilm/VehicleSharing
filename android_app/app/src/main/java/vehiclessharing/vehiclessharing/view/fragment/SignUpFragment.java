package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.api.SignUpAPI;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.Validation;
import vehiclessharing.vehiclessharing.utils.HashAlgorithm;
import vehiclessharing.vehiclessharing.viewscustom.CustomToast;


public class SignUpFragment extends Fragment implements View.OnClickListener, SignUpAPI.SignUpInterface {

    private ProgressDialog mProgress;
    private Drawable mDrawable;
    private View mView;
    private EditText mTxtFullName, mTxtPhone,mTxtPassword, mTxtConfirmPassword;
    private RadioButton mBtnMale, mBtnFemale;
    private TextView mLogin;
    private Button mBtnSignup;
    private Activity mActivity;

    private Validation validation = null;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_signup, container, false);
        addControls();
        addEvents();
        return mView;
    }

    private void addControls() {

        mActivity = getActivity();
        //[Start] Setup for progress
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignUp);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        mTxtFullName = (EditText) mView.findViewById(R.id.txtFullName);
        mBtnMale = (RadioButton) mView.findViewById(R.id.rdMale);
        mBtnFemale = (RadioButton) mView.findViewById(R.id.rdFemale);
        mTxtPhone = (EditText) mView.findViewById(R.id.txtPhone);
        mTxtPassword = (EditText) mView.findViewById(R.id.txtPassword);
        mTxtConfirmPassword = (EditText) mView.findViewById(R.id.txtConfirmPassword);
        mBtnSignup = (Button) mView.findViewById(R.id.btnSignup);
        mLogin = (TextView) mView.findViewById(R.id.already_user);
        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
    }

    private void addEvents() {
        mBtnSignup.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mTxtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(mTxtFullName.getText().toString()))
                    mTxtFullName.setError("Fullname is required", mDrawable);
                if (Validation.isEmpty(mTxtPhone.getText().toString())) {
                    mTxtPhone.setError("Phone is required", mDrawable);
                } else {
                    validation = Validation.checkValidPhone(mTxtPhone.getText().toString());
                    if (!validation.getIsValid()) {
                        mTxtPhone.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        mTxtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(mTxtFullName.getText().toString()))
                    mTxtFullName.setError("Fullname is required", mDrawable);
            }
        });

        mTxtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(mTxtFullName.getText().toString()))
                    mTxtFullName.setError("Fullname is required", mDrawable);
                if (Validation.isEmpty(mTxtPhone.getText().toString())) {
                    mTxtPhone.setError("Phone is required", mDrawable);
                } else {
                    validation = Validation.checkValidPhone(mTxtPhone.getText().toString());
                    if (!validation.getIsValid()) {
                        mTxtPhone.setError(validation.getMessageValid(), mDrawable);
                    }
                }
                if (Validation.isEmpty(mTxtPassword.getText().toString())) {
                    mTxtPassword.setError("Password is required", mDrawable);
                } else {
                    validation = Validation.checkValidPassword(mTxtPassword.getText().toString());
                    if (!validation.getIsValid()) {
                        mTxtPassword.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        mBtnFemale.setOnClickListener(this);
        mBtnMale.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:
                checkValidation();
                break;
            case R.id.already_user:
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new SigninFragment()).commit();

                break;
            case R.id.rdFemale:
                mBtnMale.setChecked(false);
                mBtnFemale.setChecked(true);
                break;

            case R.id.rdMale:
                mBtnMale.setChecked(true);
                mBtnFemale.setChecked(false);
                break;
        }

    }

    private void checkValidation() {

        String fullName = mTxtFullName.getText().toString();
        String mobilePhone = mTxtPhone.getText().toString();
        String password = mTxtPassword.getText().toString();
        String confirmPassword = mTxtConfirmPassword.getText().toString();
        Validation validation;

        if (Validation.isEmpty(fullName)
                || (!mBtnMale.isChecked() && !mBtnFemale.isChecked())
                || Validation.isEmpty(mobilePhone)
                || Validation.isEmpty(password)
                || Validation.isEmpty(confirmPassword))

            new CustomToast().showToast(getActivity(), mView,
                    "All fields are required.");
        else {
            validation = Validation.checkValidPhone(mobilePhone);
            if (!validation.getIsValid())
                new CustomToast().showToast(getActivity(), mView,
                        validation.getMessageValid());
            else {
                validation = Validation.checkValidPassword(password);
                if (!validation.getIsValid())
                    new CustomToast().showToast(getActivity(), mView,
                            validation.getMessageValid());
                else {
                    validation = Validation.checkValidConfirmPassword(password, confirmPassword);
                    if (!validation.getIsValid())
                        new CustomToast().showToast(getActivity(), mView,
                                validation.getMessageValid());
                    else {
                        int gender = 0;
                        if (mBtnMale.isChecked()) {
                            gender = 0;
                        }
                        if (mBtnFemale.isChecked()) {
                            gender = 1;
                        }

                        mProgress.show();
                        password = HashAlgorithm.md5(password);

                        SignUpAPI.getInstance(this).signUp(mobilePhone, fullName, password, gender);
                    }
                }
            }
        }

    }

    @Override
    public void signUpSuccess() {
        mProgress.dismiss();
        Toast.makeText(getActivity(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new SigninFragment()).commit();
    }

    @Override
    public void signUpUnsuccess() {
        new CustomToast().showToast(getActivity(), mView,
                getResources().getString(R.string.user_exists));
        mProgress.dismiss();
    }

    @Override
    public void signUpFailure() {
        mProgress.dismiss();
        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

    }
}

