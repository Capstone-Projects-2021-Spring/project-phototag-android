package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements GalleryViewFragment.GalleryViewListener, LoginFragment.LoginInterface {

    //General variables
    String[] arrPath; //initiate array of paths
    FragmentManager fm;
    private static final int PERMISSION_REQUEST = 0; //request variable
    //Fragment variables
    GalleryViewFragment galleryViewFragment; //initiate fragment
    LoginFragment loginViewFragment; //initiate fragment
    SettingsFragment settingsFragment;
    SinglePhotoViewFragment singlePhotoViewFragment;
    //UI variables
    MenuItem settingsButton;
    MenuItem searchButton;

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

        //loop through images on device and add paths to array
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = cursor.getString(dataColumnIndex);
            //Log.i("PATH", arrPath[i]);
        }
        cursor.close();

        fm = getSupportFragmentManager();

        loginViewFragment = (LoginFragment) fm.findFragmentById(R.id.main);
        galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.main);

        //create login view  fragment if it doesn't exist, then load.
        if(loginViewFragment == null) {
            fm.beginTransaction().add(R.id.main, LoginFragment.newInstance()).commit();
        }

        //create gallery view if it doesn't exist, Gallery View Fragment will be loaded after successful login using loadGalleryFragment(), which is called inside the LoginFragment.
        if (galleryViewFragment == null) {
            galleryViewFragment = new GalleryViewFragment();
            Bundle bundle = new Bundle();
            // bundle.putParcelableArrayList("array",images);
            bundle.putStringArray("array", arrPath);
            galleryViewFragment.setArguments(bundle);

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

        settingsButton = menu.findItem(R.id.settingsButton); // get instance of settings button
        searchButton = menu.findItem(R.id.searchButton); //get instance of search button.
        //if settings button clicked
        settingsButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

               // FragmentManager fm = getSupportFragmentManager();  <---- I COMMENTED THIS OUT AS WELL, AND USING A GLOBAL fm (James Coolen, 11:42PM, 3/11/2021)


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
                            .add(R.id.main, settingsFragment)
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
        }//end switch
    }//end onRequestPermissionsResult()


    @Override
    public void loadGalleryFragment() {
        fm.beginTransaction().replace(R.id.main, GalleryViewFragment.newInstance(arrPath)).commit();
        //Only show settings and search button after logging in. This method is only called upon succesful login.
        searchButton.setVisible(true);
        settingsButton.setVisible(true);
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
                .add(R.id.main, singlePhotoViewFragment)
                //.replace(R.id.main,singlePhotoViewFragment)
                .addToBackStack(null)
                .commit();
    }
}//end class

