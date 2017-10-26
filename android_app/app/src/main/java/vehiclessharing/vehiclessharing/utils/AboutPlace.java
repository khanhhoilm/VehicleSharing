/*
package vehiclessharing.vehiclessharing.utils;

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

import project.com.vehiclessharing.activity.MainActivity;

*/
/**
 * Created by Hihihehe on 5/16/2017.
 *//*


public class AboutPlace {
    private static AboutPlace instance;
    private static Geocoder geocoder;
    private static List<Address> addresses;

    public static AboutPlace getInstance() {
        return instance = new AboutPlace();
    }

    public String getCurrentPlace(Context activity) {
        String fullAddress = "";
        GoogleMap mMap = MainActivity.mGoogleMap;
        Location myLocation = mMap.getMyLocation();
        geocoder = new Geocoder(activity, Locale.getDefault());
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
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }
        return fullAddress;
    }

    public LatLng getLatLngByName(Context activity, String location) {
        geocoder = new Geocoder(activity);
        try {
            addresses = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        return latLng;
    }

    public String getAddressByLatLng(Context activity, LatLng latLng) throws IOException {
        String fullAddress = "";
        geocoder = new Geocoder(activity);
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
*/
