package vehiclessharing.vehiclessharing.controller.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.GetHistoryData;
import vehiclessharing.vehiclessharing.controller.adapter.HistoryFragmentPagerAdapter;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;

public class HistoryActivity extends AppCompatActivity implements GetHistoryData.HistoryCallback {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static Activity activity;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        addControls();
        addEvents();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void addEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                onBackPressed();
            }
        });
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar_General);
        toolbar.setTitle(getString(R.string.history));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        viewPager= findViewById(R.id.viewpager);
        tabLayout= findViewById(R.id.sliding_tabs);

        viewPager.setAdapter(new HistoryFragmentPagerAdapter(getSupportFragmentManager(),this));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void getHistorySuccess(UserHistoryInfo userHistoryInfo) {

    }

    @Override
    public void getHistoryFailured(String message) {

    }
}
