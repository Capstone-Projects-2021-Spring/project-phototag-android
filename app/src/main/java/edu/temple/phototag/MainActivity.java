package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsInterface, GalleryViewFragment.GalleryViewListener, SearchViewFragment.SearchViewListener, LoginFragment.LoginInterface{

    //General variables
    String[] arrPath, names, paths; //initiate array of paths
    FragmentManager fm;
    private static final int PERMISSION_REQUEST = 0; //request variable
    //Fragment variables
    GalleryViewFragment galleryViewFragment; //initiate fragment
    LoginFragment loginViewFragment; //initiate fragment
    SettingsFragment settingsFragment;
    SinglePhotoViewFragment singlePhotoViewFragment;
    SearchViewFragment searchViewFragment;
    //UI variables
    MenuItem settingsButton;
    MenuItem searchButton;
    //Google
    GoogleSignInClient mGoogleSignInClient;

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
            Log.d("arrpath",arrPath[i]);
            // names[i] = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

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


        //get preferences
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Handle auto tagging on device but not auto tagging off device
        if(shPref.getBoolean("autoTagSwitch", false) && !shPref.getBoolean("serverTagSwitch", false)) {
            Photo[] photos = new Photo[count]; //photo array to hold corrosponding arrPath information
            for (int i = 0; i < count; i++) {  //for each path/photo
                Photo photo = new Photo(arrPath[i].substring(29, arrPath[i].length() - 4), null, null, null);
                photos[i] = photo;  //add photo to array
            }
            //send photos/paths to be labeled automatically
            MLKitProcess.autoLabelPhotos(photos, arrPath);
        }


        FragmentManager fm = getSupportFragmentManager();



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
        SearchView searchView = (SearchView) menu.findItem(R.id.searchButton).getActionView();
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



                /*
                //do not allow more than one settings fragment to be added
                if(fm.getBackStackEntryCount() > 1){
                    fm.popBackStack();
                }

                 */

                if(!settingsFragment.isVisible()) {
                    //add settings fragment
                    fm.beginTransaction()
                            //.hide(galleryViewFragment)
                            //.add(R.id.main, settingsFragment)
                            .replace(R.id.main, settingsFragment)
                            .addToBackStack(null)
                            .commit();
                }

                return true;
            }
        });


        //search event listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //query tag
            @Override
            public boolean onQueryTextSubmit(String query) {


                DatabaseReference ref;
                ref = FirebaseDatabase.getInstance().getReference();

                //get results based on query
                ref.child("photoTags").child(query).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {

                            //if tag has results put paths into array and create search view fragment
                            if(task.getResult().getValue() != null) {
                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                ArrayList<String> temp = (ArrayList<String>) task.getResult().getValue();
                                paths = new String[temp.size()];
                                paths = temp.toArray(new String[temp.size()]);

                                searchViewFragment = new SearchViewFragment();
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("search", paths);
                                searchViewFragment.setArguments(bundle);

                                FragmentManager fm = getSupportFragmentManager();

                                fm.beginTransaction()
                                        //.hide(galleryViewFragment)
                                        //.add(R.id.main, searchViewFragment)
                                        .replace(R.id.main,searchViewFragment)
                                        .addToBackStack(null)
                                        .commit();

                            }
                        }
                    }
                });

                return true;
            }

            //not needed at the moment
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
    public void loadGalleryFragment(GoogleSignInClient mGoogleSignInClient) {
        Log.d("Works","here");
        this.mGoogleSignInClient = mGoogleSignInClient;
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
                    //.hide(galleryViewFragment)
                    //.add(R.id.main, singlePhotoViewFragment)
                    .replace(R.id.main,singlePhotoViewFragment)
                    .addToBackStack(null)
                    .commit();


    }

    @Override
    public void viewPhoto2(int position) {

        //get instance of fragment manager
        FragmentManager fm = getSupportFragmentManager();

        //get instance of single photo view fragment
        SinglePhotoViewFragment singlePhotoViewFragment = new SinglePhotoViewFragment();

        //create instance of bundle
        Bundle bundle = new Bundle();

        //put image path in bundle
        bundle.putString("photo", paths[position]);

        //set the bundle to fragment
        singlePhotoViewFragment.setArguments(bundle);


        //begin fragment
        fm.beginTransaction()
               // .hide(searchViewFragment)
               // .add(R.id.main, singlePhotoViewFragment)
                .replace(R.id.main,singlePhotoViewFragment)
                .addToBackStack(null)
                .commit();


    }

    //Setting Interface method.
    @Override
    public void signOut() {
        Log.d("SIGNOUT", "called");
        fm.beginTransaction()
                // .hide(searchViewFragment)
                // .add(R.id.main, singlePhotoViewFragment)
                .replace(R.id.main,LoginFragment.newInstance())
                .remove(settingsFragment)
                //.addToBackStack(null)
                .commit();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        settingsButton.setVisible(false);
                        searchButton.setVisible(false);
                        Log.d("SIGNOUT", "logged out.");
                    }
                });
    }//end signOut
}//end class

