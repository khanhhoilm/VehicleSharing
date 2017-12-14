package vehiclessharing.vehiclessharing.controller.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.controller.activity.MainActivity;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.utils.HashAlgorithm;
import vehiclessharing.vehiclessharing.viewscustom.CustomToast;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
//import vehiclessharing.vehiclessharing.database.RealmDatabase;
import vehiclessharing.vehiclessharing.model.SignInResult;
//import vehiclessharing.vehiclessharing.model.UserOnDevice;
import vehiclessharing.vehiclessharing.model.Validation;
import vehiclessharing.vehiclessharing.authentication.SessionManager;

public class Signin_Fragment extends Fragment implements View.OnClickListener {
    private static View view;

    public static final int RC_SIGN_IN = 0;

    private EditText txtPhone;
    private EditText txtPassword;
    private Button btnLogin;
    //    private static LoginButton loginfbButton;
//    private static SignInButton loginggButton;
    private TextView forgotPassword;
    private TextView signUp;
  //  private CheckBox show_hide_password;
    private LinearLayout loginLayout;
    private Animation shakeAnimation;
    private FragmentManager fragmentManager;
    private RestManager mManager;
    private ProgressDialog mProgress;
    private SessionManager session;


    public Signin_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        addControls();
        addEvents();
        return view;
    }


    /**
     * Initiate Views
     */
    private void addControls() {
        fragmentManager = getActivity().getSupportFragmentManager(); // fragement manager to switch fragment another

        session = new SessionManager(getActivity());

        //[Start] Setup for progress
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignIn);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
//        //[End] Setup for progress

        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
      /*  show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);*/
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);
    }
    private void addEvents() {
        btnLogin.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        // Set check listener over checkbox for showing and hiding password

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
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUp_Fragment()).commit();
                break;
        }

    }


    /**
     * Handling Result for login with google/facebook
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    /**
     * Check validation before login
     */
    private boolean checkValidation() {
        boolean checkValidate = false;
        // Get email and password
        String phoneNumber = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();

        // Check for both field is empty or not
        if (Validation.isEmpty(phoneNumber) || Validation.isEmpty(password)) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    Utils.EnterBothCredentials);

        }
        // Check if phone id is valid or not
        else {
            Validation validatePhone = Validation.checkValidPhone(phoneNumber);
            Validation validatePassword = Validation.checkValidPassword(password);
            if (!validatePhone.getIsValid()) {
                new CustomToast().Show_Toast(getActivity(), view,
                        validatePhone.getMessageValid());
            }
            if (!validatePassword.getIsValid()) {
                new CustomToast().Show_Toast(getActivity(), view,
                        validatePassword.getMessageValid());
            }
            // Else do login with email and password
            if (validatePhone.getIsValid() && validatePassword.getIsValid()) {
                checkValidate = true;
                // signInWithEmailAndPassword(getEmailId,getPassword);
            }
        }
        return checkValidate;
    }

    private void login() {
        String password = HashAlgorithm.md5(txtPassword.getText().toString());
        Log.d("pss","pw"+password);
        String phoneNumber = txtPhone.getText().toString();
        //CALL API SIGNIN
        mManager.getApiService().signIn(phoneNumber, password).enqueue(new Callback<SignInResult>() {
            @Override
            public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                if (response.isSuccessful()) {
                    //Users user = response.body();
                    String checkLogin = null;
                    SignInResult signInResult = response.body();

                    switch (signInResult.getStatus().getError()) {
                        case 0:
                            mProgress.dismiss();
                            session.createLoginSession(signInResult.getData().getUserInfo().getId(), signInResult.getData().getApiToken());
                            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                            databaseHelper.insertUser(signInResult.getData().getUserInfo());
                                   /* Realm.init(getContext());
                                    Realm realm = Realm.getDefaultInstance();

                                    //open db
                                    //realm.beginTransaction();
                                    UserOnDevice userOnDevice = realm.createObject(UserOnDevice.class);
                                    userOnDevice.setUserId(String.valueOf(signInResult.getData().getUserInfo().getId()));
                                    userOnDevice.setUser(signInResult.getData().getUserInfo());
                                    RealmDatabase realmDatabase=new RealmDatabase();
                                    realmDatabase.storageOnDiviceRealm(userOnDevice);*/
                            //realm.commitTransaction();//close the db

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            Bundle bd = new Bundle();
                            //  bd.putSerializable("user_info", user);
                            intent.putExtras(bd);
                            startActivity(intent);
                            break;
                        case 1:
                            new CustomToast().Show_Toast(getActivity(), view, "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại");
                            //Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                            break;
                        //Toast.makeText(getActivity(), "Số điện thoại đã được dùng để đăng ký", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignInResult> call, Throwable t) {
                Log.d("Signin", "onFailure");
                //new CustomToast().Show_Toast(getActivity(), view, "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại");
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                mProgress.dismiss();

            }

        });
    }
}
