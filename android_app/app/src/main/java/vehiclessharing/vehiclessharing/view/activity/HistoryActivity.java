package vehiclessharing.vehiclessharing.view.activity;

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
import vehiclessharing.vehiclessharing.view.adapter.HistoryFragmentPagerAdapter;
import vehiclessharing.vehiclessharing.view.fragment.HistoryDriverFragment;
import vehiclessharing.vehiclessharing.view.fragment.HistoryHikerFragment;

public class HistoryActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private HistoryFragmentPagerAdapter mAdapter;
    public static Activity sActivity;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        addControls();
        addEvents();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void addEvents() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                onBackPressed();
            }
        });
    }

    private void addControls() {
        mToolbar = findViewById(R.id.toolbar_General);
        mToolbar.setTitle(getString(R.string.history));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        mViewPager= findViewById(R.id.viewpager);
        mTabLayout= findViewById(R.id.sliding_tabs);

        mAdapter = new HistoryFragmentPagerAdapter(getSupportFragmentManager(),this);
        mAdapter.addFragment(HistoryDriverFragment.newInstance(0));
        mAdapter.addFragment(HistoryHikerFragment.newInstance(1));

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
