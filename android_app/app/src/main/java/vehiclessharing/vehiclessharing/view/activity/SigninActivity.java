package vehiclessharing.vehiclessharing.view.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
/*
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.view.fragment.SigninFragment;


import static vehiclessharing.vehiclessharing.constant.Utils.LOGIN_FRAGMENT;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{
    public static ProgressDialog sProgress;

    private FragmentManager mFragmentManager;
    private ImageView mImgClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_signin);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction().replace(R.id.frameContainer, new SigninFragment(),LOGIN_FRAGMENT).commit();
        }

        addControls();
        addEvents();
    }

    private void addEvents() {
        mImgClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_activity:
                this.finishAffinity();
                break;
        }
    }

    private void addControls() {
        sProgress =new ProgressDialog(this);
        sProgress.setTitle(Utils.SignIn);
        sProgress.setMessage(Utils.PleaseWait);
        sProgress.setCancelable(false);
        sProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        mImgClose = (ImageView) findViewById(R.id.close_activity);
        //set animation Close
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pendulum);
        mImgClose.startAnimation(animation);
    }

    public void replaceLoginFragment() {
        mFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new SigninFragment()).commit();
    }

    @Override
    public void onBackPressed() {

        Fragment signUpFragment = mFragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);
        Fragment forgetPasswordFragment = mFragmentManager
                .findFragmentByTag(Utils.ForgotPassword_Fragment);

        finishAffinity();
        if (signUpFragment != null)
            replaceLoginFragment();
        else if (forgetPasswordFragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
     }

    @Override
    public void onStop() {
        super.onStop();
    }
}
