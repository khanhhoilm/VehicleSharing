package vehiclessharing.vehiclessharing.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.nio.Buffer;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.R;
import vehiclessharing.vehiclessharing.activity.MainActivity;
import vehiclessharing.vehiclessharing.activity.SigninActivity;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.controller.RestManager;
import vehiclessharing.vehiclessharing.custom.CustomToast;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
//import vehiclessharing.vehiclessharing.database.RealmDatabase;
import vehiclessharing.vehiclessharing.model.SignInResult;
import vehiclessharing.vehiclessharing.model.SignUpResult;
//import vehiclessharing.vehiclessharing.model.UserOnDevice;
import vehiclessharing.vehiclessharing.model.Users;
import vehiclessharing.vehiclessharing.model.Validation;
import vehiclessharing.vehiclessharing.session.SessionManager;

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
    private CheckBox show_hide_password;
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

     /*   mAuth = FirebaseAuth.getInstance(); // instance Authentication firebase

        //[Start] Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginfbButton = (LoginButton) view.findViewById(R.id.btnFbLogin);
        loginfbButton.setReadPermissions("email", "public_profile");
        loginfbButton.setFragment(this);
        //[End] Initialize Facebook Login button


        //[Start]setText Google Login button
        loginggButton = (SignInButton) view.findViewById(R.id.btnGgLogin);
        setGooglePlusButtonText(loginggButton,Utils.TextButtonGG);
     */   //[End] setText Google Login button


        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }

    }

    /**
     * Set text for button google login
     * @param loginggButton instance reference to button google in layout
     * @param s content button for button
     */
  /*  private void setGooglePlusButtonText(SignInButton loginggButton, String s) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < loginggButton.getChildCount(); i++) {
            View v = loginggButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(s);
                tv.setPadding(0,5,0,5);
                //tv.setPadding(0,5,50,5);
                return;
            }
        }
    }*/

    /**
     * Set Listeners
     */
    private void addEvents() {
        btnLogin.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checked then show password else hide password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change checkbox text

                            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                            txtPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change checkbox text

                            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txtPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    /**
     * Handling button/textview click
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                mProgress.show();
                checkValidation();
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
           /* case R.id.btnGgLogin:
                signInGoogle();
                break;*/
        }

    }


    /**
     * Sign in google plus
     */
   /* private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(session.mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/

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

       /* // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                mProgress.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        else {
            mProgress.show();
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }*/
    }


    /**
     * Check validation before login
     */
    private void checkValidation() {
        // Get email and password
        final String phoneNumber = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();

        // Check for both field is empty or not
        if (Validation.isEmpty(phoneNumber) || Validation.isEmpty(password)) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    Utils.EnterBothCredentials);

        }
        // Check if email id is valid or not
        else {
            Validation validation = Validation.checkValidPhone(phoneNumber);
            if (!validation.getIsValid())
                new CustomToast().Show_Toast(getActivity(), view,
                        validation.getMessageValid());

                // Else do login with email and password
            else {
                password = SignUp_Fragment.md5(password);
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
                                    session.createLoginSession(signInResult.getData().getUserInfo().getId(),signInResult.getData().getApiToken() );
                                    DatabaseHelper databaseHelper=new DatabaseHelper(getActivity());
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
                                    mProgress.dismiss();
                                    break;
                                //Toast.makeText(getActivity(), "Số điện thoại đã được dùng để đăng ký", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInResult> call, Throwable t) {
                        Log.d("Signin", "onFailure");
                        new CustomToast().Show_Toast(getActivity(), view, "Số điện thoại hoặc mật khẩu không chính xác. Vui lòng thử lại");
                        mProgress.dismiss();

                    }

                });
                // signInWithEmailAndPassword(getEmailId,getPassword);

            }
        }
    }

   /* private String bodyToString(final ResponseBody request) {
        try {
            final ResponseBody copy = request;
            final okio.Buffer buffer = new okio.Buffer();
            if (copy != null)
                copy.string();
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
   */ //[End]Check Validation before login

   /*
    private void signInWithEmailAndPassword(String getEmailId, String getPassword){
        mProgress.show();
        mAuth.signInWithEmailAndPassword(getEmailId, getPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        mProgress.dismiss();
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mProgress.dismiss();
                            new CustomToast().Show_Toast(getActivity(), view,
                                    task.getException().getMessage());
                        }
//                        else getProfileUser();
                    }
                });
    }*/

}
