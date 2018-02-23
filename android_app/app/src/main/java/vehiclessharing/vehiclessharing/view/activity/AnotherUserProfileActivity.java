package vehiclessharing.vehiclessharing.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.view.adapter.HistoryFragmentPagerAdapter;
import vehiclessharing.vehiclessharing.view.fragment.HistoryDriverFragment;
import vehiclessharing.vehiclessharing.view.fragment.HistoryHikerFragment;

public class AnotherUserProfileActivity extends AppCompatActivity implements UserInfoAPI.GetInfoUserCallback {

    public static final String USER_ID = "user_id";
    private android.support.v7.widget.Toolbar mToolbar;
    private CircleImageView mAvatar;
    private ImageView mGender;
    private TextView mTxtName, mTxtAge, mTxtPhone;
    private int anotherUserId;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HistoryFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        Bundle bundle = getIntent().getExtras();
        anotherUserId = bundle.getInt(USER_ID, 0);

        addControls();
        loadUI();
    }

    private void loadUI() {
        UserInfoAPI userInfoAPI = new UserInfoAPI(this);
        userInfoAPI.getUserInfoFromAPI(MainActivity.sessionId, anotherUserId);
    }

    private void addControls() {
        configToolbar();
        mAvatar = findViewById(R.id.imgAvatar);
        mTxtName = findViewById(R.id.txtUserName);
        mTxtPhone = findViewById(R.id.txtPhone);
        mTxtAge = findViewById(R.id.txtAge);
        mGender = findViewById(R.id.gender);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.sliding_tabs);

        adapter = new HistoryFragmentPagerAdapter(getSupportFragmentManager(), this, anotherUserId);
        adapter.addFragment(HistoryDriverFragment.newInstance(0, anotherUserId));
        adapter.addFragment(HistoryHikerFragment.newInstance(1, anotherUserId));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void configToolbar() {
        mToolbar = findViewById(R.id.toolbar_General);
        mToolbar.setTitle(getString(R.string.user_info));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                finish();
                onBackPressed();
            }
        });
    }

    @Override
    public void getInfoUserSuccess(InfomationUser userInfo) {
        if (userInfo.getUserInfo().getAvatarLink() != null && !userInfo.getUserInfo().getAvatarLink().equals("")) {
            Glide.with(this).load(userInfo.getUserInfo().getAvatarLink()).
                    placeholder(getResources().getDrawable(R.drawable.temp)).centerCrop().fitCenter().into(mAvatar);
        }
        mTxtName.setText(userInfo.getUserInfo().getName());
        mTxtPhone.setText(userInfo.getUserInfo().getPhone());

        if (userInfo.getUserInfo().getBirthday() != null && !userInfo.getUserInfo().getBirthday().equals("")) {
            mTxtAge.setText(caculateAge(userInfo.getUserInfo().getBirthday()));
        }
        if (userInfo.getUserInfo().getGender() == 0) {
            mGender.setImageResource(R.drawable.gender_male);
        } else {
            mGender.setImageResource(R.drawable.gender_female);
        }
    }

    @Override
    public void getUserInfoFailure(String message) {

    }

    private int caculateAge(String birthday) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] year = birthday.split("/");
        int age = currentYear - Integer.parseInt(year[2]);
        return age;
    }
}
