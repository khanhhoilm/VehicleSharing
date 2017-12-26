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
import vehiclessharing.vehiclessharing.controller.activity.MainActivity;
import vehiclessharing.vehiclessharing.model.ActiveUser;

/**
 * Created by Hihihehe on 6/5/2017.
 */


public class CustomMarkerAsync extends AsyncTask<ActiveUser, Void, Bitmap> {
    /*private UserInfo user;
    private RequestInfo requestInfo;
    */private Activity mActivity;
    private GoogleMap googleMap;
    private ActiveUser activeUser;
    //  private String hashKey;

    public CustomMarkerAsync(Activity mActivity) {
        this.mActivity = mActivity;
        googleMap = MainActivity.mGoogleMap;
    }

    @Override
    protected Bitmap doInBackground(ActiveUser... params) {
        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        Bitmap bitmap = null;

        try {
            activeUser = params[0];
           /* user = params[0].getUserInfo();
            requestInfo = params[0].getRequestInfo();
*/
            if (activeUser.getUserInfo().getAvatarLink() != null) {
                bitmap = BitmapFactory.decodeStream(fetch(activeUser.getUserInfo().getAvatarLink()));
            }/* else {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.temp);
            }*/

        } catch (Exception e) {
            Log.e("Loi", e.toString());
        }


        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        //    super.onPostExecute(bitmap);
        Bitmap bitmap1 = null;

        LatLng source = new LatLng(0, 0);
        Marker customMarker = null;
        //object of user need add marker graber or needer

        if (activeUser != null && activeUser.getRequestInfo() != null && activeUser.getUserInfo() != null) {
            try {
                bitmap1 = getCustomMarkerView(bitmap, activeUser.getRequestInfo().getVehicleType());
                source = new LatLng(Double.parseDouble(activeUser.getRequestInfo().getSourceLocation().getLat()), Double.parseDouble(activeUser.getRequestInfo().getSourceLocation().getLng()));
                customMarker = googleMap.addMarker(new MarkerOptions().position(source).title(activeUser.getUserInfo().getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));
                customMarker.setTag("another");
                MainActivity.markerHashMap.put(activeUser, customMarker);
                MainActivity.userHashMap.put(customMarker, activeUser);
            } catch (Exception e) {

            }
        } /*else {
            source = new LatLng(10.795435800000002, 106.6824499);
            customMarker = googleMap.addMarker(new MarkerOptions().position(source).title(user.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));
            customMarker.setTag(activeUser);
            MainActivity.markerHashMap.put(activeUser, customMarker);
        }*/

    }

    private Bitmap getCustomMarkerView(Bitmap bitmap, int vehicleType) {
        View customMarkerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);

        if (bitmap == null) {
            switch (vehicleType) {
                case 0:
                    markerImageView.setImageResource(R.drawable.ic_accessibility_cyan_900_48dp);
                    break;
                case 1:
                    markerImageView.setImageResource(R.drawable.ic_motorcycle_cyan_900_48dp);
                    break;
                case 2:
                    markerImageView.setImageResource(R.drawable.ic_directions_car_cyan_900_48dp);
                    break;
                default:
                    markerImageView.setImageResource(R.drawable.temp);
            }
        } else {
            try {
                markerImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("Loi", e.toString());
            }
        }
        //final View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
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
