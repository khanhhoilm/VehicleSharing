package vehiclessharing.vehiclessharing.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.GetHistoryData;
import vehiclessharing.vehiclessharing.controller.activity.MainActivity;
import vehiclessharing.vehiclessharing.controller.adapter.HistoryItemAdapter;
import vehiclessharing.vehiclessharing.model.JourneyDone;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;
import vehiclessharing.vehiclessharing.utils.Helper;

/**
 * Created by Hihihehe on 6/10/2017.
 */

public class HistoryFragment extends Fragment implements GetHistoryData.HistoryCallback {
    public static final String ARG_PAGE = "ARG_PAGE";
    private List<JourneyDone> journeyDoneArrayList;
    private RecyclerView rvHistory;
    private HistoryItemAdapter adapter;
    private Context mContext;
    private int page;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static HistoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryFragment fragment = new HistoryFragment();
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
        GetHistoryData.getInstance(this).getHistory(MainActivity.sessionId);
        return view;
    }

    private void addEvents() {
        //if(Che)

    }

    private void addControls(View view) {
        mContext = getContext();
        rvHistory = view.findViewById(R.id.rcHistory);
        adapter = new HistoryItemAdapter(mContext);
        journeyDoneArrayList = new ArrayList<>();
        adapter.add(journeyDoneArrayList);
        rvHistory.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvHistory.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void getHistorySuccess(UserHistoryInfo userHistoryInfo) {
        switch (page) {
            case 1:
                journeyDoneArrayList = userHistoryInfo.getSuccessJourney().getDriver();
                break;
            case 2:
                journeyDoneArrayList=userHistoryInfo.getSuccessJourney().getHiker();
                break;
        }

        adapter.add(journeyDoneArrayList);
    }

    @Override
    public void getHistoryFailured(String message) {

    }
}
