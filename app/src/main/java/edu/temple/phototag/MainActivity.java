package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements GalleryViewFragment.GalleryViewListener {

    private static final int PERMISSION_REQUEST = 0; //request variable
    GalleryViewFragment galleryViewFragment; //initiate fragment
    SinglePhotoViewFragment singlePhotoViewFragment;
    String[] arrPath, names; //initiate array of paths
    SettingsFragment settingsFragment;
    /**
     * @param savedInstanceState for creating the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Get permission to device library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        } else {

            //callback
        }


        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();

        //Log.i("COUNT", "" + count);

        //Create an array to store path to all the images
        arrPath = new String[count];
        names = new String[count];

        //loop through images on device and add paths to array
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = cursor.getString(dataColumnIndex);
            // names[i] = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

            //Log.i("PATH", arrPath[i]);
        }
        cursor.close();


        FragmentManager fm = getSupportFragmentManager();

        galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.gallery);

        //create gallery view if it doesn't exist
        if (galleryViewFragment == null) {
            galleryViewFragment = new GalleryViewFragment();
            Bundle bundle = new Bundle();
            // bundle.putParcelableArrayList("array",images);
            bundle.putStringArray("array", arrPath);
            galleryViewFragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.gallery, galleryViewFragment)
                    .commit();
        }
    }

    /**
     *
     * @param menu
     * @return
     *
     * for adding buttons to action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu); //inflate menu view

        MenuItem settingsButton = menu.findItem(R.id.settingsButton); // get instance of settings button

        //if settings button clicked
        settingsButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                FragmentManager fm = getSupportFragmentManager();


                //check if instance of fragment exists
                if(settingsFragment == null) {

                    settingsFragment = new SettingsFragment();

                }

                //do not allow more than one settings fragment to be added
                if(fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }

                //add settings fragment
                    fm.beginTransaction()
                            .hide(galleryViewFragment)
                            .add(R.id.gallery, settingsFragment)
                            .addToBackStack(null)
                            .commit();

                return true;
            }
        });

        return true;
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults for asking user permission to device storage. makes app synchronous.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
        }
    }


    /**
     * @param position for viewing individual photos in full size
     */
    @Override
    public void viewPhoto(int position) {

        //get instance of fragment manager
        FragmentManager fm = getSupportFragmentManager();

        //get instance of single photo view fragment
        SinglePhotoViewFragment singlePhotoViewFragment = new SinglePhotoViewFragment();

        //create instance of bundle
        Bundle bundle = new Bundle();

        //put image path in bundle
        bundle.putString("photo", arrPath[position]);

        //set the bundle to fragment
        singlePhotoViewFragment.setArguments(bundle);

        //begin fragment
        fm.beginTransaction()
                .hide(galleryViewFragment)
                .add(R.id.gallery, singlePhotoViewFragment)
                //.replace(R.id.gallery,singlePhotoViewFragment)
                .addToBackStack(null)
                .commit();
    }
}

