package vehiclessharing.vehiclessharing.asynctask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.model.ActiveUser;

/**
 * Created by Hihihehe on 6/5/2017.
 */

public class CustomMarkerAsync extends AsyncTask<ActiveUser, Void, Bitmap> {
    private Activity mActivity;
    private GoogleMap mGoogleMap;
    private ActiveUser mActiveUser;
    private int mPositionInList;

    public CustomMarkerAsync(Activity mActivity, int position) {
        this.mActivity = mActivity;
        mGoogleMap = MainActivity.mGoogleMap;
        mPositionInList = position;
    }

    @Override
    protected Bitmap doInBackground(ActiveUser... params) {
        Bitmap bitmap = null;

        try {
            mActiveUser = params[0];

            if (mActiveUser.getUserInfo().getAvatarLink() != null) {
                bitmap = BitmapFactory.decodeStream(fetch(mActiveUser.getUserInfo().getAvatarLink()));
            } else {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.temp);
            }
        } catch (Exception e) {
            Log.e("Loi", e.toString());
        }


        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        Bitmap bitmap1 = null;

        LatLng source = new LatLng(0, 0);
        Marker customMarker = null;
        //object of user need add marker graber or needer

        if (mActiveUser != null && mActiveUser.getRequestInfo() != null && mActiveUser.getUserInfo() != null) {
                bitmap1 = getCustomMarkerView(bitmap,mActiveUser.getUserInfo().getAvatarLink(),
                        mActiveUser.getRequestInfo().getVehicleType(), mActiveUser.getUserInfo().getIsFavorite());
                source = new LatLng(Double.parseDouble(mActiveUser.getRequestInfo().getSourceLocation().getLat()),
                        Double.parseDouble(mActiveUser.getRequestInfo().getSourceLocation().getLng()));
                customMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title(mActiveUser.getUserInfo().getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));
                customMarker.setTag("another");
                MainActivity.markerHashMap.put(mActiveUser, customMarker);
                MainActivity.userHashMap.put(customMarker, mActiveUser);
                MainActivity.numberMarkerInHashMap.put(mPositionInList, customMarker);
        }

    }

    /**
     * @param vehicleType
     * @param isFavorite  1 is favorite
     * @return
     */
    private Bitmap getCustomMarkerView(Bitmap bitmap,String avatarLink, int vehicleType, int isFavorite) {
        View customMarkerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker, null);
        CircleImageView imgAvatar = customMarkerView.findViewById(R.id.profile_image);
        ImageView imgVehicleType = (ImageView) customMarkerView.findViewById(R.id.imgVehicleType);
        ImageView imgFavorite = customMarkerView.findViewById(R.id.imgFavorite);

        if (avatarLink != null&&!avatarLink.equals("")) {
            Glide.with(mActivity).load(avatarLink).placeholder(mActivity.getResources().getDrawable(R.drawable.temp))
                    .centerCrop().into(imgAvatar);
            imgAvatar.setImageBitmap(bitmap);
        }else {
            imgAvatar.setImageResource(R.drawable.temp);
        }
        if (isFavorite == 1) {
            imgFavorite.setVisibility(View.VISIBLE);
        } else {
            imgFavorite.setVisibility(View.GONE);
        }
        switch (vehicleType) {
            case 0:
                imgVehicleType.setImageResource(R.drawable.ic_accessibility_cyan_900_48dp);
                break;
            case 1:
                imgVehicleType.setImageResource(R.drawable.ic_motorcycle_cyan_900_48dp);
                break;
            case 2:
                imgVehicleType.setImageResource(R.drawable.ic_directions_car_cyan_900_48dp);
                break;
            default:
                imgVehicleType.setVisibility(View.GONE);
        }
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    private static InputStream fetch(String address) throws MalformedURLException, IOException {
        HttpGet httpRequest = new HttpGet(URI.create(address));
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        InputStream instream = bufHttpEntity.getContent();
        return instream;
    }
}
