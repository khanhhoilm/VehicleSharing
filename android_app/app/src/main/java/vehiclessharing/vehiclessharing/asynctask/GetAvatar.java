package vehiclessharing.vehiclessharing.asynctask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by Hihihehe on 6/5/2017.
 */


public class GetAvatar extends AsyncTask<String, Void, Bitmap> {
    private Activity mActivity;
    private String mAvatarLink="";
    private GetBitMapAvatarInterface mAvatarInterface;


    public GetAvatar(Activity mActivity,GetBitMapAvatarInterface getBitMapAvatarInterface) {
        this.mActivity = mActivity;
        mAvatarInterface=getBitMapAvatarInterface;


    }

    @Override
    protected Bitmap doInBackground(String... params) {
         Bitmap bitmap = null;

        try {
            mAvatarLink = params[0];

            if (mAvatarLink != null &&!mAvatarLink.equals("")) {
                bitmap = BitmapFactory.decodeStream(fetch(mAvatarLink));
            } else {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.temp);
            }
        } catch (Exception e) {
            Log.e("Loi", e.toString());
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
            if(bitmap!=null) {
                mAvatarInterface.getBitMapSuccess(bitmap);
            }
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

    public interface GetBitMapAvatarInterface{
        void getBitMapSuccess(Bitmap bitmap);
    }
}
