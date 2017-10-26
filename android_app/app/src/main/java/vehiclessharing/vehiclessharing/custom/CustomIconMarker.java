package vehiclessharing.vehiclessharing.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import vehiclessharing.vehiclessharing.R;

/**
 * Created by Hihihehe on 5/27/2017
 * Make marker have avatar on marker
 */

public class CustomIconMarker {
    private static Context mContext;

    public CustomIconMarker(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Use canvas to custom marker have marker icon and avatar above
     *
     * @param
     * @return
     */
    public static Bitmap getCustomMarkerView(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);

        if (bitmap == null) {
            markerImageView.setImageResource(R.drawable.temp);
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

    /* public Bitmap customMarkerWithAvatar(final String url)
     {
         View customMarkerView=getCustomMarkerView(url);
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
     private View getCustomMarkerView(final String urlAvatar) {
         View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
         ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
         Bitmap bitmap=null;
         try{
             bitmap = BitmapFactory.decodeStream((InputStream) new URL(urlAvatar).getContent());
             markerImageView.setImageBitmap(bitmap);
         }
         catch (Exception e)
         {
             Log.e("Loi",e.toString());
         }


         return customMarkerView;
     }*/
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
