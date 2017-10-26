package vehiclessharing.vehiclessharing.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;

//import com.google.firebase.auth.FirebaseAuth;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import vehiclessharing.vehiclessharing.R;
import vehiclessharing.vehiclessharing.asynctask.CustomMarkerAsync;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
//import vehiclessharing.vehiclessharing.database.RealmDatabase;
import vehiclessharing.vehiclessharing.fragment.AddRequestFragment;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.CheckerGPS;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.UserInfo;
//import vehiclessharing.vehiclessharing.model.UserOnDevice;
import vehiclessharing.vehiclessharing.session.SessionManager;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, AddRequestFragment.OnFragmentAddRequestListener {

    //    private String TAG=MainActivity.this.getLocalClassName();
    private SessionManager sessionManager;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null; // View header
    private TextView txtFullName, txtEmail;
    public static ImageView imgUser; // Avatar of user
    public static ProgressBar progressBar;
    public static Bitmap bmImgUser = null; // Bitmap of avatar
    private static int CONTROLL_ON = 1;//Controll to on Locationchanged
    private static int CONTROLL_OFF = -1;//Controll to off Locationchanged


    public static GoogleMap mGoogleMap = null;//Instance google map API
    public static Polyline polyline = null;//Instance
    private static CheckerGPS checkerGPS;
    private Realm realm;
    private UserInfo userInfo;
    private Marker myMarker = null;
    private Location previousLocation = null;
    public static HashMap<ActiveUser, Marker> markerHashMap;
    private int userId;
    private String sessionId = "";
    private boolean changeLocation = false;
    private LatLng mySource,myDes;

    private List<ActiveUser> listActiveUser;

    //    private ValueEventListener requestNeederListener;
//    private HashMap<String, Marker> markerHashMap = new HashMap<>();//Hashmap store all the marker inside map
    //    public static Activity mactivity;
    //   private String mRequestKey;
    final private static int REQ_PERMISSION = 20;//Value request permission
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";

    private FloatingActionButton btnFindPeople, btnFindVehicles, btnCancelRequest, btnRestartRequest; // button fab action

    private int checkOnScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);
        if (!sessionManager.isLoggedIn()) {
            Intent signIn = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(signIn);
        }


        SharedPreferences sharedPreferences = getSharedPreferences("vsharing_is_login", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", 0);
        sessionId = sharedPreferences.getString("session_id", "");
        Log.d("SessionId", "SessionId=" + sessionId);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        userInfo = databaseHelper.getUser(String.valueOf(userId));
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Main Activity");
        //realm=Realm.getDefaultInstance();
        // userOnDevice= RealmDatabase.getCurrentUser(userId);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnFindPeople.setOnClickListener(this);
        btnFindVehicles.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPeople = (FloatingActionButton) findViewById(R.id.btnFindPeople);
        btnFindVehicles = (FloatingActionButton) findViewById(R.id.btnFindVehicle);

        viewHeader = navigationView.getHeaderView(0);
        txtEmail = (TextView) viewHeader.findViewById(R.id.txtEmail);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        progressBar = (ProgressBar) viewHeader.findViewById(R.id.loading_progress_img);
        checkerGPS = new CheckerGPS(MainActivity.this);

        btnCancelRequest = (FloatingActionButton) findViewById(R.id.btnCancelRequest);
        btnRestartRequest = (FloatingActionButton) findViewById(R.id.btnRestartRequest);
        checkOnScreen = 0;
        markerHashMap = new HashMap<>();
        listActiveUser=new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // updateUIHeader(loginWith);//Update information user into header layout
        Intent intent = getIntent();
        if (intent != null) {
            Log.d("notification_aaaa", String.valueOf(intent.getStringExtra("notification")));
            Log.d("notification_aaaa", String.valueOf(intent.getStringExtra("body")));

        }
       /* if (checkerGPS.checkLocationPermission() && !TrackGPSService.isRunning)
            startService(new Intent(this, TrackGPSService.class));//Enable tracking GPS*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
//            fragmentManager.beginTransaction().replace(R.id.frameContainer, new Home_Fragment(), Utils.Home_Fragment).commit();
        } else if (id == R.id.nav_profile) {
            //use activity
            //  startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            //  fragmentManager.beginTransaction().replace(R.id.frameContainer, new Profile_Fragment(), Utils.Profile_Fragment).commit();
        } else if (id == R.id.nav_history) {

            //   startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        } else if (id == R.id.nav_about) {
            // fab.callOnClick();
        } else if (id == R.id.nav_logout) {
            //    logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        String whatbtnClick = "";
        android.support.v4.app.DialogFragment dialogFragment;
        switch (v.getId()) {
            case R.id.btnFindPeople:
                whatbtnClick = "btnFindPeople";
                // dialogTitle[0] = "If you want find a vehicle together, you can fill out the form";
                dialogFragment = AddRequestFragment.newInstance(whatbtnClick);
                dialogFragment.show(getSupportFragmentManager(), "From Needer");
                break;
            case R.id.btnFindVehicle:
                whatbtnClick = "btnFindVehicles";
                dialogFragment = AddRequestFragment.newInstance(whatbtnClick);
                dialogFragment.show(getSupportFragmentManager(), "From Grabber");
                break;
            case R.id.btnCancelRequest:
                cancelRequest();
                break;
            case R.id.btnRestartRequest:
                break;
        }

    }

    private void cancelRequest() {
        btnCancelRequest.setVisibility(View.GONE);
        btnFindPeople.setVisibility(View.VISIBLE);
        btnFindVehicles.setVisibility(View.VISIBLE);
        if(listActiveUser.size()>0)
        {
            for (ActiveUser user:listActiveUser) {
                if (markerHashMap.containsKey(user))
                {
                    Marker markerRm=markerHashMap.get(user);
                    markerRm.remove();
            //        markerHashMap.remove(user);
                }
            }
            markerHashMap.clear();
        }
         /*
        When you add a marker on Map, you can store it into HashMap like this:

        HashMap<YourUniqueKey,Marker> hashMapMarker = new HashMap<>();
        Marker marker = googleMap.addMarker(markerOptions);
        hashMapMarker.put(YourUniqueKey,marker);
        At the time you want to delete particular marker just get your Maker by YourUniqueKey for that marker like this:

        Marker marker = hashMapMarker.get(YourUniqueKey);
        marker.remove();
        hashMapMarker.remove(YourUniqueKey);
        */

    }

    private void hideButtonFindVehicleAndPeople() {
        if (btnFindVehicles.getVisibility() == View.VISIBLE && btnFindPeople.getVisibility() == View.VISIBLE) {
            btnFindPeople.setVisibility(View.GONE);
            btnFindVehicles.setVisibility(View.GONE);
            if (btnCancelRequest.getVisibility() == View.GONE) {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    private void visibleButtonFindVehicleAndPeople() {
        if (btnFindVehicles.getVisibility() == View.GONE && btnFindPeople.getVisibility() == View.GONE) {
            btnFindPeople.setVisibility(View.VISIBLE);
            btnFindVehicles.setVisibility(View.VISIBLE);
            if (btnCancelRequest.getVisibility() == View.VISIBLE) {
                btnCancelRequest.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);

        if (mGoogleMap != null) {
            if (checkerGPS.checkPermission())
                mGoogleMap.setMyLocationEnabled(true);//Enable mylocation
           /* Location myLocation = mGoogleMap.getMyLocation();
            if(myLocation!=null) {
                LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title("Bạn đang ở đây"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            }*/
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            // if(previousLocation)
            getMyLocation(myLocation);

            mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    getMyLocation(location);
                }
            });

            //  mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));

            //show info window when touch marker
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    //Polyline polyline=mGoogleMap.get
                    View v = null;
                    // User user = new User();
                    String who = marker.getTitle();
                    if (!who.equals("You're here") && !who.equals("Your destination")) {
                        try {
                            v = displayInfoMarkerClick(marker);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    return v;
                }
            });
            /**
             * When user want sent to request to another user in map. They can click in infowindow
             * setOnInfoWindowClickListener contain dialog confirm send request to this user
             */
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    }
                    builder.setTitle("Send request")
                            .setMessage("Are you sure you want to send this request?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.ic_warning_red_600_24dp)
                            .show();
                }
            });
        }
        //makeCustomMaker(new LatLng(mGoogleMap.getMyLocation().getLatitude(),mGoogleMap.getMyLocation().getLongitude()),"I'm in here");
        //[START]add new
//        requestNeederRef = FirebaseDatabase.getInstance().getReference().child("requests_needer");
//        requestNeederRef.addValueEventListener(requestNeederListener);
//        requestNeederRef.addChildEventListener(requestChildListener);
        //[END]add new
//      makecustomMarkerAvatar(new LatLng(10.8719808, 106.790409),mUser.getUid(), "Nong Lam University");


    }

    private void getMyLocation(Location myLocation) {


        if (!changeLocation) {
            final double[] longitude = {myLocation.getLongitude()};
            final double[] latitude = {myLocation.getLatitude()};
            LatLng myLatLng = new LatLng(latitude[0], longitude[0]);

            if (!myLocation.equals(previousLocation) && previousLocation != null) {

                if (myLocation.distanceTo(previousLocation) > 20.0 || !myLocation.equals(previousLocation)) {
                    if (myMarker != null) {
                        myMarker.remove();

                    }
                    myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title("Bạn đang ở đây"));
                    //  markerHashMap.put(userId,myMarker);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                    mGoogleMap.animateCamera(cameraUpdate);

                }
            }
            if (previousLocation == null) {
                previousLocation = myLocation;
                myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title("Bạn đang ở đây"));
                // markerHashMap.put(userId,myMarker);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }

    }


    private void makeCustomMarkerMyself(LatLng source, LatLng destination) {
        mGoogleMap.clear();
        BitmapDescriptor bitmapSource = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp);
        BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp);
        Marker sourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("You're here").icon(bitmapSource));
        Marker destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Your destination").icon(bitmapDestination));
        sourceMarker.setTag(userId);
        destinationMarker.setTag(userId);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * if user not current user custom info of marker. Info marker have info about fullname, source location, destination location
     * Time start
     *
     * @param marker
     * @return
     */
    private View displayInfoMarkerClick(Marker marker) throws IOException {
        View v = getLayoutInflater().inflate(R.layout.info_marker, null);
        final TextView txtFullname, txtSourceLocation, txtDeslocation, txtTime;
        final ImageView imgVehicleType;

        txtFullname = (TextView) v.findViewById(R.id.txtFullNameUser);
        txtSourceLocation = (TextView) v.findViewById(R.id.txtSourceLocationUser);
        txtDeslocation = (TextView) v.findViewById(R.id.txtDesLocationUser);
        txtTime = (TextView) v.findViewById(R.id.txtTimeUser);
        imgVehicleType = (ImageView) v.findViewById(R.id.imgVehicleTypeUser);
        //txtVehicleType.setVisibility(View.GONE);
        String time = "";
        int vehicleType;
        LatLng souceLocation = null;
        LatLng desLocation = null;
        String idUser = "";
        final String[] fullName = new String[1];
        Boolean checkNullObject = true;
       // if (checkOnScreen == 1) {
            ActiveUser user= (ActiveUser) marker.getTag();
            //user = UserInfomation.getInstance().getInfoUserById(graber.getUserId());
            if (user != null) {
             //   souceLocation = new LatLng(user.getRequestInfo().getSourceLocation().getLat(),user.getRequestInfo().getSourceLocation().getLng());
               // desLocation = new LatLng(user.getRequestInfo().getDestLocation().getLat(),user.getRequestInfo().getDestLocation().getLng());
                time = user.getRequestInfo().getTimeStart();
                vehicleType=user.getRequestInfo().getVehicleType();
                /*if (imgVehicleType.getVisibility() == View.INVISIBLE) {
                    imgVehicleType.setVisibility(View.VISIBLE);
                }// txtVehicleType.setText(graber.getVehicleType());
                if (graber.getVehicleType().equals("Bike")) {
                    imgVehicleType.setImageResource(R.drawable.ic_motorcycle_green_900_24dp);
                }
                checkNullObject = false;
                idUser = graber.getUserId();*/
                //replace mysource to test because source and des of active user is null
                String sourceAddress = PlaceHelper.getInstance(this).getAddressByLatLng(mySource);
                //LatLng des = new LatLng(desLocation.getLatidude(), desLocation.getLongtitude());
                String destinationAddress = PlaceHelper.getInstance(this).getAddressByLatLng(mySource);

                txtFullname.setText(user.getUserInfo().getName());
                txtSourceLocation.setText(sourceAddress);
                txtDeslocation.setText(destinationAddress);
                txtTime.setText(time);
                switch (user.getRequestInfo().getVehicleType()) {
                    case 0:
                        imgVehicleType.setImageResource(R.drawable.ic_directions_run_indigo_700_24dp);
                        break;
                    case 1:
                        imgVehicleType.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
                        break;
                    case 2:
                        imgVehicleType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
                        break;
                }
                if (DrawRoute.polylineNotCurUser != null) {
                    DrawRoute.polylineNotCurUser.remove();
                }
                DrawRoute draw = new DrawRoute(this);
                //code test
                draw.drawroadBetween2Location(mySource, mySource, 1);
                // code right
                //draw.drawroadBetween2Location(mySource, desLocation, 1);

                //Button btnSend= (Button) v.findViewById(R.id.btnSendRequest);
                /*btnSend.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Toast.makeText(MainActivity.this, "btnsend click", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });*/
          /*  } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        //txtFullname.setText("hihihehehe");
        return v;
    }


    @Override
    public void addRequestSuccess(LatLng cur, LatLng des, String time, int type, List<ActiveUser> list) {
        Log.d("addRequestSuccess", "add request success");

        listActiveUser=list;
        changeLocation=true;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cur, 17);
        mGoogleMap.animateCamera(cameraUpdate);

        DrawRoute drawRoute = new DrawRoute(this);
        drawRoute.setmSubject(0);
        mySource=cur;
        myDes=des;
        drawRoute.drawroadBetween2Location(cur, des, 0);
        makeCustomMarkerMyself(cur, des);
        //drawroadBetween2Location(curLocation, desLocation);
        hideButtonFindVehicleAndPeople();
        for (ActiveUser activeUser : list) {
            new CustomMarkerAsync(this).execute(activeUser);
        }
        //ForGraber.getInstance().getAllNeederNear(this, mUser.getUid());
    }

    @Override
    public void addRequestFailure() {

    }
}
