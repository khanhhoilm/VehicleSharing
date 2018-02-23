package vehiclessharing.vehiclessharing.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.view.fragment.HistoryDriverFragment;
import vehiclessharing.vehiclessharing.view.fragment.HistoryHikerFragment;

/**
 * Created by Hihihehe on 6/6/2017.
 */

public class HistoryFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private String mTabTitles[] = new String[]{"Khi là Người chở", "Khi là người quá giang"};
    private Context mContext;
    private int mAnotherUserId = 0;

    public HistoryFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    public HistoryFragmentPagerAdapter(FragmentManager fm, Context context, int anotherUserId) {
        super(fm);
        this.mContext = context;
        this.mAnotherUserId = anotherUserId;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (mAnotherUserId == 0) {
            //use in my histoy
            if (position == 0) {
                fragment = HistoryDriverFragment.newInstance(position);
            } else {
                fragment = HistoryHikerFragment.newInstance(position);
            }
        } else {
            //use in another user history
            if (position == 0) {
                fragment = HistoryDriverFragment.newInstance(position, mAnotherUserId);
            } else {
                fragment = HistoryHikerFragment.newInstance(position, mAnotherUserId);
            }
        }

        return fragment;
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
        //    mFragmentTitleList.add(title);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Drawable image = null;
        if (position == 0) {
            image = mContext.getResources().getDrawable(R.drawable.ic_directions_car_indigo_a700_24dp);
        } else if (position == 1) {
            image = mContext.getResources().getDrawable(R.drawable.ic_accessibility_indigo_700_24dp);
        }

        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + mTabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

}
