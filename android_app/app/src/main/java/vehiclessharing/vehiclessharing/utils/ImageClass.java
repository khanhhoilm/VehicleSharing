/*
package vehiclessharing.vehiclessharing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.MainActivity;

*/
/**
 * Created by Tuan on 15/05/2017.
 *//*


public class ImageClass {

    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    public ImageClass() {
    }

    */
/**
     * Resize Image after upload Storage Firebase
     * @param c
     * @param uri
     * @param requiredSize 100
     * @return
     * @throws FileNotFoundException
     *//*

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return rotateImage(BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2),getExifOrientation(uri.getPath()));
    }

    */
/**
     * Get value rorating from an image
     * @param filepath
     * @return
     *//*

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognise a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        Log.d("orientation_value",String.valueOf(degree));
        return degree;
    }


    */
/**
     * Rotating image after upload firebase Storage
     * @param bitmap
     * @return
     *//*

    public static Bitmap rotateImage(Bitmap bitmap, int orientation){
        Matrix matrix = new Matrix();

        matrix.postRotate(orientation);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        return rotatedBitmap;
    }

    */
/**
     * get bytes array from Uri.
     * @param context current context.
     * @param uri uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     *//*

    public static byte[] getBytesFromUri(Context context, Uri uri) throws FileNotFoundException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { */
/* do nothing *//*
 }
        }
        return null;
    }

    */
/**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     *//*

    private static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ */
/* do nothing *//*
 }
        }
        return bytesResult;
    }

    */
/**
     * Load image offline (image cached)
     * @param url
     * @param context
     * @param imageView
     * @param progressBar
     *//*

    public static void loadImageOffline(String url, final Context context, final ImageView imageView, final ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.temp);
                    }
                });
    }

    */
/**
     * Load image online (image not cached)
     * @param url
     * @param context
     * @param imageView
     * @param progressBar
     *//*

    public static void loadImageOnline(String url, final Context context, final ImageView imageView, final ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(url)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.temp);
                    }
                });
    }


    */
/**
     *get Url image resized from Storage Firebase
     * @param context
     * @param imageView
     * @param callback
     *//*

    public static void getUrlThumbnailImage(final Context context, final ImageView imageView, final ImageCallback callback){
        StorageReference imageRef = storage.getReference();
        imageRef.child("/avatar/thumb100_" + MainActivity.mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("getDownloadUrl_success",uri.toString());
                if(!MainActivity.currentUser.getUser().getImage().equals(uri.toString()))
                    callback.onSuccess(uri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getDownloadUrl_failed",String.valueOf(e.getMessage()));
                callback.onError(e);
            }
        });
    }

}
*/
