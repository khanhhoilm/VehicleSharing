package vehiclessharing.vehiclessharing.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.model.JourneyDone;
import vehiclessharing.vehiclessharing.utils.ChangeDateTimeGMT;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;


public class HistoryItemAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<JourneyDone> mReceivePushArrayList;
    private boolean isAnotherUser = false;

    public interface OnItemsInCardClicked {
        void OnCategoryClick(int cateId);
    }

    private HistoryItemAdapter.OnItemsInCardClicked mCallback;

    public HistoryItemAdapter(Context context, boolean anotherUserHistory) {
        mReceivePushArrayList = new ArrayList<>();
        isAnotherUser = anotherUserHistory;
        this.mContext = context;
    }

    public HistoryItemAdapter(Context context) {
        mReceivePushArrayList = new ArrayList<>();
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mReceivePushArrayList.size();
    }

    public void add(List<JourneyDone> journeyDoneArrayList) {
        mReceivePushArrayList = journeyDoneArrayList;
        notifyDataSetChanged();
    }

    public void addAItem(JourneyDone journeyDone) {
        mReceivePushArrayList.add(journeyDone);
        notifyDataSetChanged();
    }

    public void clear() {
        mReceivePushArrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = null;
        if (!isAnotherUser) {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.history_item, viewGroup, false);
            return new CardViewHolder(itemView, viewType);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_rating_another_user, viewGroup, false);
            return new AnotherUserHistoryHolder(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        JourneyDone journeyDone = mReceivePushArrayList.get(position);

        try {
            if (viewHolder instanceof CardViewHolder && !isAnotherUser) {
                CardViewHolder holder = (CardViewHolder) viewHolder;

                if (journeyDone.getJourney().getPartner().getAvatarLink() != null && !journeyDone.getJourney().getPartner().getAvatarLink().equals("")) {
                    Glide.with(mContext).load(journeyDone.getJourney().getPartner().getAvatarLink()).placeholder(mContext.getResources().getDrawable(R.drawable.temp)).into(holder.imgAvatar);
                }
                holder.txtUserName.setText(journeyDone.getJourney().getPartner().getName());

                String timeStart="";
                timeStart=ChangeDateTimeGMT.convertToDate(journeyDone.getJourney().getStartTime().getDate());
                String timeEnd=ChangeDateTimeGMT.convertToDate(journeyDone.getJourney().getFinishTime());
                holder.txtStartAndFinishTime.setText(timeStart + " - " + timeEnd );

                holder.txtSourceAddress.setText(PlaceHelper.getInstance(mContext).getAddressByLatLngLocation(journeyDone.getJourney().getStartLocation()));
                holder.txtDesAddress.setText(PlaceHelper.getInstance(mContext).getAddressByLatLngLocation(journeyDone.getJourney().getEndLocation()));

                float ratingValue = 0;
                int myRating = 0;
                int anotherRating = 0;
                if (journeyDone.getUserAction() != null) {
                    myRating = journeyDone.getUserAction().getRatingValue();
                }
                if (journeyDone.getJourney().getPartnerRating() != null) {
                    anotherRating = journeyDone.getJourney().getPartnerRating().getRatingValue();
                }
                ratingValue = (float) (myRating + anotherRating) / 2;
                holder.averageRatingBar.setRating(ratingValue);
            } else {
                AnotherUserHistoryHolder holder = (AnotherUserHistoryHolder) viewHolder;
                if (journeyDone.getJourney().getPartner().getAvatarLink() != null && !journeyDone.getJourney().getPartner().getAvatarLink().equals("")) {
                    Glide.with(mContext).load(journeyDone.getJourney().getPartner().getAvatarLink()).placeholder(mContext.getResources().getDrawable(R.drawable.temp)).into(holder.imgAvatar);
                }
                holder.txtUserName.setText(journeyDone.getJourney().getPartner().getName());

                String timeStart="";
                timeStart=ChangeDateTimeGMT.convertToDate(journeyDone.getJourney().getStartTime().getDate());
                String timeEnd=ChangeDateTimeGMT.convertToDate(journeyDone.getJourney().getFinishTime());
                holder.txtStartAndFinishTime.setText(timeStart + " - " + timeEnd );

                holder.txtSourceAddress.setText(PlaceHelper.getInstance(mContext).getAddressByLatLngLocation(journeyDone.getJourney().getStartLocation()));
                holder.txtDesAddress.setText(PlaceHelper.getInstance(mContext).getAddressByLatLngLocation(journeyDone.getJourney().getEndLocation()));
                int anotherRating = 0;

                if (journeyDone.getJourney().getPartnerRating() != null) {
                    anotherRating = journeyDone.getJourney().getPartnerRating().getRatingValue();
                }
                holder.averageRatingBar.setRating(anotherRating);

                if (journeyDone.getJourney().getPartnerRating() != null) {
                    holder.txtComment.setText(journeyDone.getJourney().getPartnerRating().getComment());
                    holder.txtFailure.setVisibility(View.GONE);
                } else {
                    holder.txtComment.setText("");
                    holder.txtFailure.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {

        }
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgAvatar;
        public TextView txtUserName, txtSourceAddress, txtDesAddress, txtStartAndFinishTime;
        public RatingBar averageRatingBar, ratingValue;
        public Button btnMyRating, btnYourRating;
        public TextView txtWhoRating, txtComment, txtFailure;
        public LinearLayout lnSeeRating, lnButtonRating;

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
            txtWhoRating = v.findViewById(R.id.txtWhoRating);
            lnSeeRating = v.findViewById(R.id.lnSeeRating);
            txtFailure = v.findViewById(R.id.txtFailure);

            ratingValue = v.findViewById(R.id.rbRatingValue);
            txtComment = v.findViewById(R.id.txtComment);
            lnButtonRating = v.findViewById(R.id.lnButtonRating);
            lnButtonRating.setScaleX(1f);
            lnButtonRating.setScaleY(1f);
            btnMyRating.setOnClickListener(this);
            btnYourRating.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnMyRating:

                    txtWhoRating.setText(mContext.getResources().getString(R.string.my_rating));
                    lnSeeRating.setVisibility(View.VISIBLE);
                    if (mReceivePushArrayList.get(getAdapterPosition()).getUserAction() != null) {
                        ratingValue.setRating(Float.parseFloat(String.valueOf(mReceivePushArrayList.get(getAdapterPosition())
                                .getUserAction().getRatingValue())));
                        txtComment.setText(mReceivePushArrayList.get(getAdapterPosition()).getUserAction().getComment());
                        txtFailure.setVisibility(View.GONE);
                    } else {
                        txtFailure.setVisibility(View.VISIBLE);
                        ratingValue.setRating(0);
                        txtComment.setText("");
                    }
                    lnButtonRating.setScaleX(0.7f);
                    lnButtonRating.setScaleY(0.7f);
                    break;
                case R.id.btnYourRating:

                    txtWhoRating.setText(mContext.getResources().getString(R.string.your_rating));
                    lnSeeRating.setVisibility(View.VISIBLE);
                    if (mReceivePushArrayList.get(getAdapterPosition()).getJourney().getPartnerRating() != null) {
                        ratingValue.setRating(mReceivePushArrayList.get(getAdapterPosition()).getJourney().getPartnerRating().getRatingValue());
                        txtComment.setText(mReceivePushArrayList.get(getAdapterPosition()).getJourney().getPartnerRating().getComment());
                        txtFailure.setVisibility(View.GONE);
                    } else {
                        txtFailure.setVisibility(View.VISIBLE);
                        ratingValue.setRating(0);
                        txtComment.setText("");
                    }
                    lnSeeRating.setVisibility(View.VISIBLE);
                    lnButtonRating.setScaleX(0.7f);
                    lnButtonRating.setScaleY(0.7f);

                    break;
            }
        }
    }

    public class AnotherUserHistoryHolder extends RecyclerView.ViewHolder {

        public ImageView imgAvatar;
        public TextView txtUserName, txtSourceAddress, txtDesAddress, txtStartAndFinishTime;
        public RatingBar averageRatingBar;
        public TextView txtComment, txtFailure;

        public AnotherUserHistoryHolder(View v, int cardType) {
            super(v);
            imgAvatar = v.findViewById(R.id.imgAvatar);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtSourceAddress = v.findViewById(R.id.txtStartLocation);
            txtDesAddress = v.findViewById(R.id.txtEndLocation);
            txtStartAndFinishTime = v.findViewById(R.id.txtStartAndFinishTime);
            averageRatingBar = v.findViewById(R.id.rbAveragrRating);

            txtFailure = v.findViewById(R.id.txtFailure);
            txtComment = v.findViewById(R.id.txtComment);

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnMyRating) {
            Toast.makeText(mContext, "My rating button click", Toast.LENGTH_SHORT).show();
        }
    }

}
