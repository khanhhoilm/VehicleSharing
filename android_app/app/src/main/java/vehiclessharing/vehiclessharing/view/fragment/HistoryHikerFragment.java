package vehiclessharing.vehiclessharing.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.HistoryDataAPI;
import vehiclessharing.vehiclessharing.model.JourneyDone;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.view.adapter.HistoryItemAdapter;

/**
 * Created by Hihihehe on 6/10/2017.
 */

public class HistoryHikerFragment extends Fragment implements HistoryDataAPI.HistoryHikerCallback, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    private List<JourneyDone> mJourneyDoneArrayList;
    private RecyclerView mRvHistory;
    private HistoryItemAdapter mAdapter;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mUserType = "hiker";
    private TextView mTxtFailure;
    private int mAnotherUserId;
    private HistoryDataAPI mHistoryDataAPI;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static HistoryHikerFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryHikerFragment fragment = new HistoryHikerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static HistoryHikerFragment newInstance(int page, int userId) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putInt(HistoryDriverFragment.USER_ID, userId);
        HistoryHikerFragment fragment = new HistoryHikerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        if (getArguments() != null) {
            mAnotherUserId = getArguments().getInt(HistoryDriverFragment.USER_ID, 0);
        }
        mHistoryDataAPI = new HistoryDataAPI(this);

        addControls(view);

        if (mAnotherUserId == 0) {
            mHistoryDataAPI.getHistoryHiker(MainActivity.sSessionId, mUserType);
        } else {
            mHistoryDataAPI.getHistoryAnotherHiker(MainActivity.sSessionId, mUserType, mAnotherUserId);

        }
        return view;
    }

    @Override
    public boolean getUserVisibleHint() {

        return super.getUserVisibleHint();
    }

    private void addControls(View view) {
        mContext = getContext();
        mRvHistory = view.findViewById(R.id.rcHistory);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        mTxtFailure = view.findViewById(R.id.txtFailure);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (mAnotherUserId == 0) {
            mAdapter = new HistoryItemAdapter(mContext);
        } else {
            mAdapter = new HistoryItemAdapter(mContext, true);
        }
        mJourneyDoneArrayList = new ArrayList<>();
        mAdapter.add(mJourneyDoneArrayList);
        mRvHistory.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvHistory.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void getHistoryHikerSuccess(UserHistoryInfo userHistoryInfo, String userType) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (userHistoryInfo != null && userHistoryInfo.getSuccessJourney().size() > 0) {
            mJourneyDoneArrayList = userHistoryInfo.getSuccessJourney();
            mAdapter.add(mJourneyDoneArrayList);
            mAdapter.notifyDataSetChanged();
            mTxtFailure.setVisibility(View.GONE);
        } else {
            mTxtFailure.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getHistoryFailured(String message) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRefresh() {
        mHistoryDataAPI.getHistoryHiker(MainActivity.sSessionId, mUserType);
    }
}
