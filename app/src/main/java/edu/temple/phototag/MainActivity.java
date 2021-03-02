package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements GalleryViewFragment.GalleryViewListener {

    private static final int PERMISSION_REQUEST = 0; //request variable
    GalleryViewFragment galleryViewFragment; //initiate fragment
    String[] arrPath; //initiate array of paths

    /**
     *
     * @param savedInstanceState
     *
     * for creating the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Get permission to device library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
        } else{

            //callback
        }


        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();

        //Log.i("COUNT", "" + count);

        //Create an array to store path to all the images
        arrPath = new String[count];

        //loop through images on device and add paths to array
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i]= cursor.getString(dataColumnIndex);
            //Log.i("PATH", arrPath[i]);
        }
        cursor.close();


        FragmentManager fm = getSupportFragmentManager();

        galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.gallery);

        //create gallery view if it doesn't exist
        if(galleryViewFragment == null){
            galleryViewFragment = new GalleryViewFragment();
            Bundle bundle = new Bundle();
           // bundle.putParcelableArrayList("array",images);
            bundle.putStringArray("array",arrPath);
            galleryViewFragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.gallery,galleryViewFragment)
                    .commit();
        }
    }


    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * for asking user permission to device storage. makes app synchronous.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    finish();
                }
        }
    }

}