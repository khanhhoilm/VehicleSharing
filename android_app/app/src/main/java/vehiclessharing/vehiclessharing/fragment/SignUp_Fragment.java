package vehiclessharing.vehiclessharing.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import retrofit2.Callback;

import vehiclessharing.vehiclessharing.R;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.controller.RestManager;
import vehiclessharing.vehiclessharing.custom.CustomToast;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.SignUpResult;
import vehiclessharing.vehiclessharing.model.Validation;


//import static com.google.android.gms.internal.zzt.TAG;


public class SignUp_Fragment extends Fragment implements View.OnClickListener {


    private ProgressDialog mProgress;
    private Drawable mDrawable;
    //  private DatabaseReference mDatabase;

    private View view;
    private EditText txtFullName;
    // private static EditText txtEmail;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private RadioButton rdMale, rdFemale;
    private TextView login;
    private Button btnSignup;
    // private ApiService apiService;
    private RestManager mManager;
    // private static CheckBox terms_conditions;
    private Activity mActivity;

    DatabaseHelper db;//Instance to using SQLITE

    private Validation validation = null;//Instance to check validation

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        addControls();
        addEvents();
        return view;
    }

    /**
     * Initialize all views
     */
    private void addControls() {

        mActivity=getActivity();
        //[Start] Setup for progress
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle(Utils.SignUp);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        txtFullName = (EditText) view.findViewById(R.id.txtFullName);
        rdMale = (RadioButton) view.findViewById(R.id.rdMale);
        rdFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        //    txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.txtConfirmPassword);
        btnSignup = (Button) view.findViewById(R.id.btnSignup);
        login = (TextView) view.findViewById(R.id.already_user);
        //terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            //  terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    /**
     * Set Listeners
     */
    private void addEvents() {
        btnSignup.setOnClickListener(this);
        login.setOnClickListener(this);
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if (Validation.isEmpty(txtPhone.getText().toString())) {
                    txtPhone.setError("Phone is required", mDrawable);
                } else {
                    validation = Validation.checkValidPhone(txtPhone.getText().toString());
                    if (!validation.getIsValid()) {
                        txtPhone.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        txtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
            }
        });

        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Validation.isEmpty(txtFullName.getText().toString()))
                    txtFullName.setError("Fullname is required", mDrawable);
                if (Validation.isEmpty(txtPhone.getText().toString())) {
                    txtPhone.setError("Phone is required", mDrawable);
                } else {
                    validation = Validation.checkValidPhone(txtPhone.getText().toString());
                    if (!validation.getIsValid()) {
                        txtPhone.setError(validation.getMessageValid(), mDrawable);
                    }
                }
                if (Validation.isEmpty(txtPassword.getText().toString())) {
                    txtPassword.setError("Password is required", mDrawable);
                } else {
                    validation = Validation.checkValidPassword(txtPassword.getText().toString());
                    if (!validation.getIsValid()) {
                        txtPassword.setError(validation.getMessageValid(), mDrawable);
                    }
                }
            }
        });

        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Signin_Fragment()).commit();

                break;
            case R.id.rdFemale:
                rdMale.setChecked(false);
                rdFemale.setChecked(true);
                break;

            case R.id.rdMale:
                rdMale.setChecked(true);
                rdFemale.setChecked(false);
                break;
        }

    }

    /**
     * Check Validation Method
     */
    private void checkValidation() {

        // Get all edittext texts
        String fullName = txtFullName.getText().toString();
        //   final String getEmailId = txtEmail.getText().toString();
        String mobilePhone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        Validation validation;//Instance to check feild is valid

        // Check if all strings are null or not
        if (Validation.isEmpty(fullName)
                || (!rdMale.isChecked() && !rdFemale.isChecked())
                //  || Validation.isEmpty(getEmailId)
                || Validation.isEmpty(mobilePhone)
                || Validation.isEmpty(password)
                || Validation.isEmpty(confirmPassword))

            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");
        else {
            validation = Validation.checkValidPhone(mobilePhone);
            if (!validation.getIsValid())
                new CustomToast().Show_Toast(getActivity(), view,
                        validation.getMessageValid());

                //Check if password must be at least 6 characters
            else {
                validation = Validation.checkValidPassword(password);
                if (!validation.getIsValid())
                    new CustomToast().Show_Toast(getActivity(), view,
                            validation.getMessageValid());

                    // Check if both password should be equal
                else {
                    validation = Validation.checkValidConfirmPassword(password, confirmPassword);
                    if (!validation.getIsValid())
                        new CustomToast().Show_Toast(getActivity(), view,
                                validation.getMessageValid());

                        // Make sure user should check Terms and Conditions checkbox
                       /* else if (!terms_conditions.isChecked())
                            new CustomToast().Show_Toast(getActivity(), view,
                                    "Please select Terms and Conditions.");
*/
                        // Else do signup or do your stuff
                    else {

                        int gender = 0;
                        if (rdMale.isChecked()) {
                            gender = 0;
                        }
                        if (rdFemale.isChecked()) {
                            gender = 1;
                        }

                        mProgress.show();
                        // Log.d("Validate signup","password = "+password);
                        //Create new account call api
                        // signUp();
                        password = md5(password);
                        mManager.getApiService().signUp(mobilePhone, fullName, password, gender).enqueue(new Callback<SignUpResult>() {
                            @Override
                            public void onResponse(retrofit2.Call<SignUpResult> call, retrofit2.Response<SignUpResult> response) {
                                JSONObject s = null;
                                if (response.isSuccessful()) {
                                    SignUpResult responseBody = response.body();

                                    //  s = new JSONObject(response.body().);
                                    switch (responseBody.getStatus().getError()) {
                                        case 0:
                                            mProgress.dismiss();
                                            Toast.makeText(getActivity(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                                                    .replace(R.id.frameContainer, new Signin_Fragment()).commit();
                                            break;
                                        case 1:
                                            new CustomToast().Show_Toast(getActivity(), view,
                                                    getResources().getString(R.string.user_exists));
                                            mProgress.dismiss();
                                            break;
                                    }
                                } else {
                                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<SignUpResult> call, Throwable t) {
                                mProgress.dismiss();
                                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                            }
                        });
                                         }
                }

            }
        }

    }


   /*
     * Storage profile's user into both Firebase Database and SQLite
     *
     * @param userId   - userid
     * @param email    - user's email
     * @param image    - user's url image
     * @param fullname - user's fullname
     * @param phone    - user's phoneNumber
     * @param sex      - user's sex
     * @param address  - user's address
     */

    /*private void writeNewUser(String userId, String email, String image, String fullname, String phone,
                              String sex, UserAddress address, BirthDay birthDay) {
        User user = new User(email, image, fullname, phone, sex, address, birthDay);
        //[START]Storage in Firebase Database
       *//* mDatabase.child("users").child(userId).setValue(user);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .build();*//*
      *//*  mUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });*//*
    }*/
/*
    @Override
    public void register(String message) {

    }*/

    /* @Override
     public void registerSentResult(String message) {
         Log.d("SignUp", message);
         mProgress.dismiss();
         getFragmentManager()
                 .beginTransaction()
                 .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                 .replace(R.id.frameContainer, new Login_Fragment()).commit();

     }

     @Override
     public void signInResult(String message) {

     }

     @Override
     public void errorEncountered(String message) {

     }*/
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

