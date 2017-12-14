package vehiclessharing.vehiclessharing.utils;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import vehiclessharing.vehiclessharing.model.LatLngLocation;

/**
 * Created by Hihihehe on 10/6/2017.
 */

public class Helper {
    public static LatLng convertLatLngLocationToLatLng(LatLngLocation latLngLocation)
    {
        return new LatLng(Double.parseDouble(latLngLocation.getLat()),Double.parseDouble(latLngLocation.getLng()));
    }
    public static LatLngLocation convertLatLngToLatLngLocation(LatLng latLng)
    {
        LatLngLocation latLngLocation=new LatLngLocation();
        latLngLocation.setLat(String.valueOf(latLng.latitude));
        latLngLocation.setLng(String.valueOf(latLng.longitude));
        return latLngLocation;
    }
    public static byte[] getPictureByteOfArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap getBitmapFromByte(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static InputStream getStreamByURL(String address) throws MalformedURLException, IOException {
        HttpGet httpRequest = new HttpGet(URI.create(address));
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        InputStream instream = bufHttpEntity.getContent();
        return instream;
    }

    /**
     * This converts the distance traveled in
     meters into miles.
     * @param distanceInMeters
     * @return
     */
    public static float getKiloMeter(float distanceInMeters) {

        return distanceInMeters / 1000;
    }

}
