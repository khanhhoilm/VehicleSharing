package vehiclessharing.vehiclessharing.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Hihihehe on 12/11/2017.
 */

public class CheckWriteStorage {
    private static CheckWriteStorage checkWriteStorage=null;
    private static Context mContext;
    private static Activity mActivity;
    public static int STORAGE_PERMISSTION_CODE=1;

   public static CheckWriteStorage getInstance(Context context,Activity activity){
       checkWriteStorage=new CheckWriteStorage();
       mContext=context;
       mActivity=activity;
       return checkWriteStorage;
   }

    private boolean checkSelfPermission(String permission){
        if(ActivityCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_GRANTED) {
            Log.v("checkSelfPermission", "Permission is granted");
            //File write logic here
        }
        return true;
    }
    public boolean isStoragePermissionGranted() {
        boolean isCheck=false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.v("isStoragePermission","Permission is granted");
                isCheck= true;
            } else {

                Log.v("isStoragePermission","Permission is revoked");
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSTION_CODE);
               // return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("isStoragePermission","Permission is granted");

            isCheck= true;
        }
        return isCheck;
    }
}
