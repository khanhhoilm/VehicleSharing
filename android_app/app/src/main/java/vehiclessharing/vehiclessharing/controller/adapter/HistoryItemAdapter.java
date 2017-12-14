package vehiclessharing.vehiclessharing.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.model.JourneyDone;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;


public class HistoryItemAdapter extends
        RecyclerView.Adapter<HistoryItemAdapter.CardViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<JourneyDone> receivePushArrayList;

    public interface OnItemsInCardClicked {
        void OnCategoryClick(int cateId);
    }

    private HistoryItemAdapter.OnItemsInCardClicked mCallback;

    public HistoryItemAdapter(Context context) {
        receivePushArrayList=new ArrayList<>();
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return receivePushArrayList.size();
    }

    public void add(List<JourneyDone> journeyDoneArrayList) {
        receivePushArrayList = journeyDoneArrayList;
        notifyDataSetChanged();
    }

    public void addAItem(JourneyDone journeyDone) {
        receivePushArrayList.add(journeyDone);
        notifyDataSetChanged();
    }

    public void clear() {
        receivePushArrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public HistoryItemAdapter.CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.history_item, viewGroup, false);

        return new CardViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {

        JourneyDone journeyDone = receivePushArrayList.get(position);

        //Glide.with(mContext).load(journeyDone.getJourney().getParnerInfo().getInfoUser().getAvatarLink());
        //holder.txtUserName.setText(journeyDone.getJourney().getParnerInfo().getInfoUser().getName());
        try {
            holder.txtStartAndFinishTime.setText(journeyDone.getJourney().getStartTime().getDate() + " - " + journeyDone.getJourney().getFinishTime());

            holder.txtSourceAddress.setText(PlaceHelper.getInstance(mContext).getAddressByLatLngLocation(journeyDone.getJourney().getStartLocation()));
            holder.txtDesAddress.setText(PlaceHelper.getInstance(mContext).getAddressByLatLngLocation(journeyDone.getJourney().getEndLocation()));
         //   holder.averageRatingBar.setRating(journeyDone.getJourney().getRatingValue());

        }catch (Exception e){

        }
        }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgAvatar;
        public TextView txtUserName, txtSourceAddress, txtDesAddress, txtStartAndFinishTime;
        public RatingBar averageRatingBar;
        public Button btnMyRating, btnYourRating;

        public CardViewHolder(View v, int cardType) {
            super(v);
            imgAvatar = v.findViewById(R.id.imgAvatar);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtSourceAddress = v.findViewById(R.id.txtStartLocation);
            txtDesAddress = v.findViewById(R.id.txtEndLocation);
            txtStartAndFinishTime = v.findViewById(R.id.txtStartAndFinishTime);
            averageRatingBar = v.findViewById(R.id.rbAveragrRating);
            btnMyRating = v.findViewById(R.id.btnMyRating);
            btnYourRating = v.findViewById(R.id.btnYourRating);

            btnMyRating.setOnClickListener(this);
            btnYourRating.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnMyRating){
            Toast.makeText(mContext, "My rating button click", Toast.LENGTH_SHORT).show();
        }
    }

}
