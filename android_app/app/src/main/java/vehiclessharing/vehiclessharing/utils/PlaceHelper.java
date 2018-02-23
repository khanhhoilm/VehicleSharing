package vehiclessharing.vehiclessharing.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vehiclessharing.vehiclessharing.model.LatLngLocation;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;

/**
 * Created by Hihihehe on 10/8/2017.
 */

public class PlaceHelper {
    private static PlaceHelper sInstance;
    private Geocoder mGeocoder;
    private List<Address> mAddresses;
    private Context mContext;
    private Activity mActivity;

    public static PlaceHelper getInstance(Context context) {

        return sInstance = new PlaceHelper(context);
    }

    public static PlaceHelper getInstance(Context context, Activity activity) {

        return sInstance = new PlaceHelper(context, activity);
    }

    PlaceHelper(Context context) {
        mContext = context;
    }

    PlaceHelper(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    public String getCurrentPlace(GoogleMap googleMap) throws Exception {
        String fullAddress = "";
        try {
            GoogleMap mMap = googleMap;
            CheckerGPS checkerGPS = new CheckerGPS(mContext, mActivity);
            Location myLocation = null;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                if(checkerGPS.checkLocationPermission())
                myLocation = mMap.getMyLocation();
            } else {
                LocationManager locationManager = (LocationManager)
                        mContext.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();


                if (checkerGPS.checkLocationPermission()) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
                        myLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
            mGeocoder = new Geocoder(mContext, Locale.getDefault());
            if (myLocation != null) {
                double dLatitude = myLocation.getLatitude();
                double dLongitude = myLocation.getLongitude();
                try {
                    mAddresses = mGeocoder.getFromLocation(dLatitude, dLongitude, 1);
                    String listAddress[] = new String[4];
                    listAddress[0] = mAddresses.get(0).getAddressLine(0);
                    listAddress[1] = mAddresses.get(0).getLocality();
                    listAddress[2] = mAddresses.get(0).getAdminArea();
                    listAddress[3] = mAddresses.get(0).getCountryName();

                    for (int i = 0; i < listAddress.length; i++) {
                        if (listAddress[i] != null) {
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
               Log.d("getCurrentPlace", "Unable to fetch the current location");
            }
        }catch (Exception e)
        {
            Log.d("getCurrentPlace", "Unable to fetch the current location");
        }
        return fullAddress;
    }

    public LatLng getLatLngByName(String location) {
        mGeocoder = new Geocoder(mContext);
        try {
            mAddresses = mGeocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLng = new LatLng(mAddresses.get(0).getLatitude(), mAddresses.get(0).getLongitude());
        return latLng;
    }

    public String getAddressByLatLng(final LatLng latLng) throws IOException {
        String fullAddress = "";
        final String[] addressReplace = new String[1];
        mGeocoder = new Geocoder(mContext);
        try {
            mAddresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String[] listAddress = new String[4];
            listAddress[0] = mAddresses.get(0).getAddressLine(0);
            listAddress[1] = mAddresses.get(0).getLocality();
            listAddress[2] = mAddresses.get(0).getAdminArea();
            listAddress[3] = mAddresses.get(0).getCountryName();

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

              Thread thread=new Thread("threadAddress"){
                  @Override
                  public void run() {
                      try {
                          if(android.os.Debug.isDebuggerConnected())
                              android.os.Debug.waitForDebugger();
                          addressReplace[0] = getStringFromLocation(latLng.latitude, latLng.longitude);
                      }catch (Exception e)
                      {

                      }
                  }
              };
        }

      return fullAddress;
    }
    public String getAddressByLatLngLocation(LatLngLocation latLng) throws Exception {
        String fullAddress = "";
        mGeocoder = new Geocoder(mContext);
        try {
            mAddresses = mGeocoder.getFromLocation(Double.parseDouble(latLng.getLat()), Double.parseDouble(latLng.getLng()), 1);
            String[] listAddress = new String[4];
            listAddress[0] = mAddresses.get(0).getAddressLine(0);
            listAddress[1] = mAddresses.get(0).getLocality();
            listAddress[2] = mAddresses.get(0).getAdminArea();
            listAddress[3] = mAddresses.get(0).getCountryName();

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


    public static LatLng getLocationFromString(String address)
            throws JSONException {

        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(
                    "http://maps.google.com/maps/api/geocode/json?address="
                            + URLEncoder.encode(address, "UTF-8") + "&ka&sensor=false");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

        double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        return new LatLng(lat, lng);
    }

    public static String getStringFromLocation(double lat, double lng)
            throws ClientProtocolException, IOException, JSONException {
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        String address = String
                .format(Locale.ENGLISH,
                        "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                        + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;

        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        retList = new ArrayList<>();

        if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String indiStr = result.getString("formatted_address");
                Address addr = new Address(Locale.getDefault());
                addr.setAddressLine(0, indiStr);
                retList.add(addr);
            }
        }


        return getFullAdressFromAdressObject(retList);
    }


    private class GetAddressPositionTask extends
            AsyncTask<String, Integer, LatLng> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LatLng doInBackground(String... plookupString) {

            String lookupString = plookupString[0];
            final String lookupStringUriencoded = Uri.encode(lookupString);
            LatLng position = null;

            // best effort zoom
            try {
                if (mGeocoder != null) {
                    List<Address> addresses = mGeocoder.getFromLocationName(
                            lookupString, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address first_address = addresses.get(0);
                        position = new LatLng(first_address.getLatitude(),
                                first_address.getLongitude());
                    }
                } else {
                    Log.e("getAddressPosition", "geocoder was null, is the module loaded? ");
                }

            } catch (IOException e) {
                Log.e("getAddressPosition", "geocoder failed, moving on to HTTP");
            }
            // try HTTP lookup to the maps API
            if (position == null) {
                HttpGet httpGet = new HttpGet(
                        "http://maps.google.com/maps/api/geocode/json?address="
                                + lookupStringUriencoded + "&sensor=true");
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                StringBuilder stringBuilder = new StringBuilder();

                try {
                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();
                    int b;
                    while ((b = stream.read()) != -1) {
                        stringBuilder.append((char) b);
                    }
                } catch (ClientProtocolException e) {
                } catch (IOException e) {
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    // Log.d("MAPSAPI", stringBuilder.toString());

                    jsonObject = new JSONObject(stringBuilder.toString());
                    if (jsonObject.getString("status").equals("OK")) {
                        jsonObject = jsonObject.getJSONArray("results")
                                .getJSONObject(0);
                        jsonObject = jsonObject.getJSONObject("geometry");
                        jsonObject = jsonObject.getJSONObject("location");
                        String lat = jsonObject.getString("lat");
                        String lng = jsonObject.getString("lng");

                        position = new LatLng(Double.valueOf(lat),
                                Double.valueOf(lng));
                    }

                } catch (JSONException e) {
                    Log.e("getAddressPosition", e.getMessage(), e);
                }

            }
            return position;
        }

        @Override
        protected void onPostExecute(LatLng result) {
            Log.i("GEOCODE", result.toString());
            super.onPostExecute(result);
        }

    };

    public static String getFullAdressFromAdressObject(List<Address> addressList){
        String fullAddress="";
        String[] listAddress = new String[4];
        listAddress[0] = addressList.get(0).getAddressLine(0);
        listAddress[1] = addressList.get(0).getLocality();
        listAddress[2] = addressList.get(0).getAdminArea();
        listAddress[3] = addressList.get(0).getCountryName();

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
        return fullAddress;
    }

   /* public void asyncJson(String address){
    //    Toast.makeText(getContext(),"go to convert 1", Toast.LENGTH_LONG).show();
        //final Location tempLocation = new Location("location");
        AQuery aq = new AQuery(getContext());
        address = address.replace(" ", "+");

        String url = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyBgjRbTCo-uYojxcr9Ce5PlKEVef-qtgdY&address="+ address +"&sensor=true";

        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if(json != null){
                   // Toast.makeText(getContext(),"go to convert 2", Toast.LENGTH_LONG).show();

                    //here you work with the response json
                    JSONArray results = null;
                    try {
                        results = json.getJSONArray("results");
                        locationShop = new Location("location");
                        Log.e("convetLocaiotn","go to convert 3" +results.toString());

                        locationShop.setLatitude(Double.parseDouble(results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat")));
                        locationShop.setLongitude(Double.parseDouble(results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng")));
                        if(locationShop.getLatitude() != 0 && locationShop.getLongitude() != 0){
                            adMarker(locationShop.getLatitude(),locationShop.getLongitude(),true);
                        }
                    } catch (JSONException e) {
                     //   Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();

                        e.printStackTrace();
                    }

                }else{
                    Log.e("convetLocaiotn", "Error:" + status.getCode());
                //    Toast.makeText(getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }*/
}
