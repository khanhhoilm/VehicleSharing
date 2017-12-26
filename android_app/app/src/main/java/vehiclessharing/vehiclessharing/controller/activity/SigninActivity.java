package vehiclessharing.vehiclessharing.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import vehiclessharing.vehiclessharing.controller.fragment.Signin_Fragment;


import static vehiclessharing.vehiclessharing.constant.Utils.Login_Fragment;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{

    public static ProgressDialog mProgress;//Progress to wait login



    private static FragmentManager fragmentManager;// Instance fragmentManager to switch fragment
    ImageView imgClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up notitle
       supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_signin);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Signin_Fragment(),
                            Login_Fragment).commit();
        }

        addControls();
        addEvents();
    }

    private void addEvents() {
        imgClose.setOnClickListener(this);

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
        mProgress =new ProgressDialog(this);
        mProgress.setTitle(Utils.SignIn);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        imgClose = (ImageView) findViewById(R.id.close_activity);
        //set animation Close
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pendulum);
        imgClose.startAnimation(animation);
    }

    public void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Signin_Fragment()).commit();


    }

    @Override
    public void onBackPressed() {

        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(Utils.ForgotPassword_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        finishAffinity();
        if (SignUp_Fragment != null)
            replaceLoginFragment();
        else if (ForgotPassword_Fragment != null)
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
