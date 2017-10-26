package vehiclessharing.vehiclessharing.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import vehiclessharing.vehiclessharing.activity.MainActivity;

/**
 * Created by Hihihehe on 10/8/2017.
 */

public class PlaceHelper {
    private static PlaceHelper instance;
    private static Geocoder geocoder;
    private static List<Address> addresses;
    private Context mContext;

    public static PlaceHelper getInstance(Context mContext) {

        return instance = new PlaceHelper(mContext);
    }
    PlaceHelper()
    {

    }
    PlaceHelper(Context context)
    {
        mContext=context;
    }

    public String getCurrentPlace() {
        String fullAddress = "";
        GoogleMap mMap = MainActivity.mGoogleMap;
        Location myLocation = mMap.getMyLocation();
        geocoder = new Geocoder(mContext, Locale.getDefault());
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            try {
                addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
                String listAddress[]=new String[4];
                listAddress[0] = addresses.get(0).getAddressLine(0);
                listAddress[1] = addresses.get(0).getLocality();
                listAddress[2] = addresses.get(0).getAdminArea();
                listAddress[3] = addresses.get(0).getCountryName();

                for(int i=0;i<listAddress.length;i++)
                {
                    if(listAddress[i]!=null)
                    {
                        fullAddress = fullAddress.concat(listAddress[i]);
                        //fullAddress += listAddress;
                        if (i != listAddress.length - 1) {
                            fullAddress = fullAddress.concat(", ");
                        }
                    }
                }

            } catch (IOException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }
        return fullAddress;
    }

    public LatLng getLatLngByName(String location) {
        geocoder = new Geocoder(mContext);
        try {
            addresses = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        return latLng;
    }

    public String getAddressByLatLng(LatLng latLng) throws IOException {
        String fullAddress = "";
        geocoder = new Geocoder(mContext);
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String[] listAddress = new String[4];
            listAddress[0] = addresses.get(0).getAddressLine(0);
            listAddress[1] = addresses.get(0).getLocality();
            listAddress[2] = addresses.get(0).getAdminArea();
            listAddress[3] = addresses.get(0).getCountryName();

            String fullAd = listAddress[0] + ", " + listAddress[1] + ", " + listAddress[2] + ", " + listAddress[3];
            Log.e("fullAddress", fullAd);
            for (int i = 0; i < listAddress.length; i++) {
                if (listAddress[i]!=null && !listAddress[i].isEmpty()) {
                    fullAddress = fullAddress.concat(listAddress[i]);
                    //fullAddress += listAddress;
                    if (i != listAddress.length - 1) {
                        fullAddress = fullAddress.concat(", ");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullAddress;
    }
}
