package com.rohanprasad.automaticwallpaperchanger;

import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by WIN-8 on 27-01-2015.
 */
public class MyWallpaperService extends Service {

    String PREFS_NAME = "WallpaperSharedPref";
    String DATA_COUNT = "DataEntries";
    String TAG = "MyWallpaperService";
    String HEIGHT = "height";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"Service Started");
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        String imageUri = getImageUri();

        if(imageUri != null){
            Log.d(TAG,"Updating Wallpaper");
            try {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);

//                // use if resizing the image
//                BitmapFactory.Options options = new BitmapFactory.Options();
//
//                // set to true to set image bounds
//                options.inJustDecodeBounds = true;
//
//                // set to 2, 4, 6, etc to create a progressively smaller image
//                options.inSampleSize = 2;
//
//                // set to false to prepare image for decoding
//                options.inJustDecodeBounds = false;
//
//                //Bitmap bitmap = BitmapFactory.decodeFile(imageUri, options);
//                //if(bitmap != null)
                myWallpaperManager.setStream(new FileInputStream(imageUri));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else {
            Log.d(TAG,"No Images present");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public String getImageUri(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        int max = settings.getInt(DATA_COUNT,-1);

        if(max == -1){
            return null;
        }

        Random rand = new Random();
        int randNum = 0;
        if(max > 0){
            randNum = rand.nextInt(max);
            randNum += 1;
            randNum %= max;

            if(randNum == 0){
                randNum++;
            }
        }
        Log.i(TAG,"POS: " + randNum);
        String uriString = settings.getString(Integer.toString(randNum),null);
        return uriString;
    }
}
