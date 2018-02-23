package vehiclessharing.vehiclessharing.constant;

public class Utils {
    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9]+@[A-Za-z]+\\.[A-Za-z]{2,4}\\b";
    public static final String regPx = "^[0-9]*$";

    //Setting for FirebaseApp

  /*  public static final FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(),
            new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://vehiclessharing-74957.firebaseio.com/")
                    .setApiKey("AIzaSyCe6VAITl-FDrMnbOxvhaMudE8RCR0dPSU")
                    .setApplicationId("1:1093380309543:android:56f0bc5cd958205a").build(),
            "VehiclesSharing");
*/
    //Fragments Tags
    public static final String LOGIN_FRAGMENT = "Login_Fragment";
    public static final String Home_Fragment = "Home_Fragment";
    public static final String Profile_Fragment = "Profile_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";

    //Message ProgressDialog
    public static final String SignIn = "Sign in...";
    public static final String ChangePassword = "Updating password...";
    public static final String Updating = "Updating...";
    public static final String PleaseWait = "Please Wait...";
    public static final String SignUp = "Sign up...";
    public static final String EnterBothCredentials = "Enter both credentials.";

    //Text for button
    public static final String TextButtonGG = "Log in with Google +";
    /**
     * Device token key's name.
     */
    public static final String DEVICE_TOKEN = "device_token";

    //Tag error try cat
    public static final String TAG_ERROR_GET_BYTE_IMAGE = "error_get_byte_image";
    public static final String TAG_ERROR_UPLOAD_IMAGE_FIREBASE = "error_upload_image";
    public static final String TAG_UPLOAD_IMAGE = "upload_image";
    public static final String TAG_UPDATE_PROFILE = "update_profile";
    public static final String TAG_ERROR_ROUTING = "error_routing";
    public static final String TAG_ERROR_SELECT_IMAGE = "error_select_iamge";
    public static final String TAG_ERROR_LOCATION = "error_location";

}
