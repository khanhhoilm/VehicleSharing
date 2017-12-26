package vehiclessharing.vehiclessharing.controller.fragment;

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
import vehiclessharing.vehiclessharing.api.GetHistoryData;
import vehiclessharing.vehiclessharing.controller.activity.MainActivity;
import vehiclessharing.vehiclessharing.controller.adapter.HistoryItemAdapter;
import vehiclessharing.vehiclessharing.model.JourneyDone;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;

/**
 * Created by Hihihehe on 6/10/2017.
 */

public class HistoryDriverFragment extends Fragment implements GetHistoryData.HistoryDriverCallback,SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    private List<JourneyDone> journeyDoneArrayList;
    private RecyclerView rvHistory;
    private HistoryItemAdapter adapter;
    private Context mContext;
    private int page;
    private String userType = "driver";
    private TextView txtFailure;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static HistoryDriverFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryDriverFragment fragment = new HistoryDriverFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PAGE);
        }

        addControls(view);
        addEvents();
        // txtTest.setText(userType);
        GetHistoryData.getInstance(this).getHistoryDriver(MainActivity.sessionId, userType);
        return view;
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    private void addEvents() {
    }

    private void addControls(View view) {
        mContext = getContext();
        rvHistory = view.findViewById(R.id.rcHistory);
        txtFailure=view.findViewById(R.id.txtFailure);
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        adapter = new HistoryItemAdapter(mContext);
        journeyDoneArrayList = new ArrayList<>();
        adapter.add(journeyDoneArrayList);
        rvHistory.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvHistory.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void getHistoryDriverSuccess(UserHistoryInfo userHistoryInfo, String userType) {
        swipeRefreshLayout.setRefreshing(false);
        if (userHistoryInfo != null && userHistoryInfo.getSuccessJourney().size() > 0) {
            journeyDoneArrayList = userHistoryInfo.getSuccessJourney();
            adapter.add(journeyDoneArrayList);
            adapter.notifyDataSetChanged();
            txtFailure.setVisibility(View.GONE);
        }else {
            txtFailure.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getHistoryFailured(String message) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        GetHistoryData.getInstance(this).getHistoryDriver(MainActivity.sessionId, userType);

    }
}
