package com.rohanprasad.automaticwallpaperchanger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    SharedPreferences settings;
    String PREFS_NAME = "WallpaperSharedPref";
    String DATA_COUNT = "DataEntries";
    String HEIGHT = "height";
    String TAG = "MainActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.btn_pick_image))
                .setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {

                        // in onCreate or any event where your want the user to
                        // select a file
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), SELECT_PICTURE);
                    }
                });

        if(getDataCount() == -1){
            Log.d(TAG, "Setting Pending Intent");
            Intent intent = new Intent(MainActivity.this,MyWallpaperService.class);
            PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this,0,intent,0);
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 2*60*60*1000, pendingIntent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                AddImage(selectedImagePath);
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void AddImage(String imageURI) {
        Log.d(TAG,"Adding Image: " + imageURI.toString());
        int num = getDataCount();
        num++;

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Integer.toString(num), imageURI.toString());
        editor.putInt(DATA_COUNT,num);
        editor.commit();
    }

    public Uri getImage(int pos){
        Uri mImageURI = null;
        String uriString = settings.getString(Integer.toString(pos),null);

        if(uriString != null){
            mImageURI = Uri.parse(uriString);
        }

        return  mImageURI;
    }

    public int getDataCount() {
        settings = getSharedPreferences(PREFS_NAME, 0);
        int count = settings.getInt(DATA_COUNT, -1);

        if (count == -1) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(DATA_COUNT, 0);
            editor.putInt(HEIGHT, height);
            editor.commit();
        }
        Log.d(TAG, "Data Count: " + count);
        return count;
    }
}
