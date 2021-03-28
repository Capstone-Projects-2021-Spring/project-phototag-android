package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
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
import com.google.firebase.auth.FirebaseAuth;
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsInterface, GalleryViewFragment.GalleryViewListener, SearchViewFragment.SearchViewListener, LoginFragment.LoginInterface{

    //General variables
    String[] names, paths; //initiate array of paths
    ArrayList<String> paths2, paths3, input2;
    FragmentManager fm;
    private static final int PERMISSION_REQUEST = 0; //request variable
    //Fragment variables
    GalleryViewFragment galleryViewFragment; //initiate fragment
    LoginFragment loginViewFragment; //initiate fragment
    SettingsFragment settingsFragment;
    SinglePhotoViewFragment singlePhotoViewFragment;
    SearchViewFragment searchViewFragment, searchViewFragment2;
    //UI variables
    MenuItem settingsButton;
    MenuItem searchButton;
    //Google
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount acct;
    User userReference; //keeps track of the user object


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

        /*
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
        //names = new String[count];

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

         */

        fm = getSupportFragmentManager();

        loginViewFragment = (LoginFragment) fm.findFragmentById(R.id.main);
        //galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.main);

        //create login view  fragment if it doesn't exist, then load.
        if(loginViewFragment == null) {
            fm.beginTransaction().add(R.id.main, LoginFragment.newInstance()).commit();
        }

        //}

        /* AutoTagging
        //get preferences
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Handle auto tagging on device but not auto tagging off device
            if(shPref.getBoolean("autoTagSwitch", false) && !shPref.getBoolean("serverTagSwitch", false)) {
            Photo[] photos = new Photo[count]; //photo array to hold corrosponding arrPath information
            for (int i = 0; i < count; i++) {  //for each path/photo
                //String[] idArray = arrPath[i].split("/");
                Photo photo = new Photo(arrPath[i], null, null, null);
                photos[i] = photo;  //add photo to array
            }
            //send photos/paths to be labeled automatically
            MLKitProcess.autoLabelPhotos(photos, arrPath);
        }
        */

        //create gallery view if it doesn't exist, Gallery View Fragment will be loaded after successful login using loadGalleryFragment(), which is called inside the LoginFragment.
        /*if (galleryViewFragment == null) {
            galleryViewFragment = new GalleryViewFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArray("array", arrPath);
            galleryViewFragment.setArguments(bundle);

        }*/

    }//end onCreate()

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


                //get db reference
                DatabaseReference ref;
                ref = FirebaseDatabase.getInstance().getReference();

                //delimiter used to divide tags
                Scanner input = new Scanner(query).useDelimiter(",");

                //lists for data
                input2 = new ArrayList<>();
                paths2 = new ArrayList<>();
                paths3 = new ArrayList<>();
              
                //separate tags by delimeter and add to array list
                while(input.hasNext()){

                    input2.add(input.next().toLowerCase());
                }

                //loop through tags
                for(int i = 0; i < input2.size();i++) {

                    int finalI = i;//reference to tag position in arraylist
                    int mod = i % 2;//mod to tell if position is odd or even

                    if (!query.contains(".") && !query.contains("#") && !query.contains("$") && !query.contains("[") && !query.contains("]")) {
                        //query db with tag

                        ref.child("Android").child(User.getInstance().getEmail()).child("PhotoTags").child(input2.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {

                                    //if not first position, no result, and odd clear the list
                                    if (finalI > 0 && task.getResult().getValue() == null && mod == 0) {
                                        paths2.clear();
                                    }

                                    //same as above but for even
                                    if (finalI > 0 && task.getResult().getValue() == null && mod == 1) {
                                        paths3.clear();
                                    }

                                    //if tag has results put paths into array and create search view fragment
                                    if (task.getResult().getValue() != null) {

                                        //put path results in array
                                        HashMap<String, Boolean> resultMap = (HashMap<String, Boolean>) task.getResult().getValue();
                                        ArrayList<String> temp = new ArrayList<>(resultMap.keySet());

                                        paths = new String[temp.size()];
                                        paths = temp.toArray(new String[temp.size()]);

                                        //first loop only
                                        if (finalI == 0) {
                                            for (int i = 0; i < paths.length; i++) {

                                                //decode encoded path
                                                paths[i] = decodeFromFirebaseKey(paths[i]);

                                                //variable to check if it exists on device
                                                File file = new File(paths[i]);

                                                //if it does exist add it to list
                                                if (file.exists() && !paths2.contains(paths[i])) {

                                                    paths2.add(paths[i]);

                                                }
                                            }
                                        }

                                        //every loop that is odd
                                        if (finalI > 0 && mod == 0) {
                                            for (int i = 0; i < paths.length; i++) {

                                                if (i == 0) {
                                                    paths2.clear();
                                                }

                                                paths[i] = decodeFromFirebaseKey(paths[i]);
                                                File file = new File(paths[i]);

                                                if (file.exists() && paths3.contains(paths[i])) {
                                                    paths2.add(paths[i]);
                                                }
                                            }
                                        }

                                        //every loop that is even
                                        if (finalI > 0 && mod == 1) {
                                            for (int i = 0; i < paths.length; i++) {

                                                if (i == 0) {
                                                    paths3.clear();
                                                }
                                              
                                                paths[i] = decodeFromFirebaseKey(paths[i]);
                                                File file = new File(paths[i]);

                                                if (file.exists() && paths2.contains(paths[i])) {
                                                    paths3.add(paths[i]);
                                                }
                                            }
                                        }
                                    }

                                    //for every odd # tags that gets results display results
                                    if (finalI == input2.size() - 1 && !paths2.isEmpty() && mod == 0) {
                                        searchButton.setVisible(false);

                                        searchViewFragment = new SearchViewFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putStringArrayList("search", paths2);
                                        searchViewFragment.setArguments(bundle);

                                        FragmentManager fm = getSupportFragmentManager();
                                        fm.beginTransaction()
                                                .replace(R.id.main, searchViewFragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }

                                    //for every even # tags that gets results display results
                                    if (finalI == input2.size() - 1 && !paths3.isEmpty() && mod == 1) {
                                        searchButton.setVisible(false);
                                        Log.d("paths", paths3.toString());
                                      
                                        paths2.clear();
                                        paths2 = paths3;

                                        searchViewFragment2 = new SearchViewFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putStringArrayList("search", paths2);
                                        searchViewFragment2.setArguments(bundle);

                                        FragmentManager fm = getSupportFragmentManager();

                                        fm.beginTransaction()
                                                .replace(R.id.main, searchViewFragment2)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }
                            }
                        });
                    }
                }
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

    /**
     * to hide search option unless on gallery fragment
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
           searchButton.setVisible(true);
        }
    }
    //LOGIN INTERFACE IMPLEMENTATIONS BELOW ****************

    /**
     * This LoginFragment Interface method should be called after a successful login. This method will load
     * the galleryFragment, and display the search and settings buttons. The User object is also created within this method call.
     * @param mGoogleSignInClient holds the data needed for Google sign in and sign out.
     */
    @Override
    public void loadGalleryFragment(GoogleSignInClient mGoogleSignInClient) {
        this.mGoogleSignInClient = mGoogleSignInClient;

        //User authenticated, set the values of the User singleton.
        acct = GoogleSignIn.getLastSignedInAccount(this);
        userReference = User.getInstance();
        userReference.setUsername(acct.getEmail());
        userReference.setEmail(acct.getEmail());
        userReference.setMap(new HashMap<>());
        userReference.setImagePaths(new ArrayList<>());

        userReference.syncWithFirebase();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();

        //loop through images on device and add paths to array
        //also create a photo object for each path it finds
        Photo[] photos = new Photo[count]; //photo array to hold corresponding arrPath information
        Log.d("Debug", "The list of paths as they are being found and created on the device is below: ");
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            // THIS IS WHERE THE PHOTOS ARE ACTUALLY CREATED
            Photo p = new Photo(cursor.getString(dataColumnIndex), null, null, null);
            Log.d("Debug: P's path is", p.path);

            //lastly, we want to add this photo object to the user object.
            userReference.addPhoto(p);
        }
        cursor.close();

        //-Start-   Perform Auto Tagging
        //get preferences
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Check If either Auto Tagging is on and if so perform the user chosen autotagging method
        if(shPref.getBoolean("autoTagSwitch", false) && !shPref.getBoolean("serverTagSwitch", false)) {
            //Do On Device Photo Tagging
            MLKitProcess.autoLabelPhotos(userReference.getAllPhotoObjects());
        }
        if(shPref.getBoolean("autoTagSwitch", false) && shPref.getBoolean("serverTagSwitch", false)) {
            //Do Server Auto Tagging Here
        }
        //-End-     Perform Auto Tagging

        String[] keyArray = userReference.getImagePaths().toArray(new String[userReference.getMap().keySet().size()]);
        fm.beginTransaction().replace(R.id.main, GalleryViewFragment.newInstance(keyArray)).commit();
        //Only show settings and search button after logging in. This method is only called upon succesful login.
        searchButton.setVisible(true);
        settingsButton.setVisible(true);
    }

    //LOGIN INTERFACE IMPLEMENTATIONS END****************

    /**
     * @param position for viewing individual photos in full size
     */
    @Override
    public void viewPhoto(int position) {
            //set search button to invisible
            searchButton.setVisible(false);

            //get instance of fragment manager
            FragmentManager fm = getSupportFragmentManager();

            //get instance of single photo view fragment
            SinglePhotoViewFragment singlePhotoViewFragment = new SinglePhotoViewFragment();

            //create instance of bundle
            Bundle bundle = new Bundle();

            //put image path in bundle
            String path = userReference.getImagePaths().get(position);
            Log.d("Debug", "path: " + path);
            bundle.putString("photo", userReference.getImagePaths().get(position));

            //set the bundle to fragment
            singlePhotoViewFragment.setArguments(bundle);

            //begin fragment
            fm.beginTransaction()
                    .replace(R.id.main, singlePhotoViewFragment)
                    .addToBackStack(null)
                    .commit();
    }

    @Override
    public void viewPhoto2(int position) {
            //set search button to invisible
            searchButton.setVisible(false);

            //get instance of fragment manager
            FragmentManager fm = getSupportFragmentManager();

            //get instance of single photo view fragment
            SinglePhotoViewFragment singlePhotoViewFragment = new SinglePhotoViewFragment();

            //create instance of bundle
            Bundle bundle = new Bundle();

            //put image path in bundle
            //bundle.putString("photo", paths2[position]);
            bundle.putString("photo", paths2.get(position));

            //set the bundle to fragment
            singlePhotoViewFragment.setArguments(bundle);

            //begin fragment
            fm.beginTransaction()
                    // .hide(searchViewFragment)
                    // .add(R.id.main, singlePhotoViewFragment)
                    .replace(R.id.main, singlePhotoViewFragment)
                    .addToBackStack(null)
                    .commit();
    }

    //SETTINGS INTERFACE IMPLEMENTATIONS BELOW ****************

    /**
     * This Settings interface method should be called within the activity when the user is attempting to sign out. For example
     * when the user presses a sign out button, this method should be called and executed. GoogleSignInClient is needed.
     *
     */
    @Override
    public void signOut() {
        //Clear the back stack.
        while(fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
            Log.d("SIGNOUT", "Fragment Stack Count: " + fm.getBackStackEntryCount());
        }
        fm.beginTransaction()
                .replace(R.id.main,LoginFragment.newInstance())
                .remove(settingsFragment)
                .commit();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        settingsButton.setVisible(false);
                        searchButton.setVisible(false);
                        FirebaseAuth.getInstance().signOut();
                        Log.d("SIGNOUT", "logged out.");
                        Log.d("SIGNOUT", "firebase signed out");
                    }
                });
    }//end signOut

    //SETTINGS INTERFACE IMPLEMENTATIONS END ****************

    //from https://stackoverflow.com/questions/19132867/adding-firebase-data-dots-and-forward-slashes/39561350#39561350
    public static String decodeFromFirebaseKey(String s) {
        int i = 0;
        int ni;
        String res = "";
        while ((ni = s.indexOf("_", i)) != -1) {
            res += s.substring(i, ni);
            if (ni + 1 < s.length()) {
                char nc = s.charAt(ni + 1);
                if (nc == '_') {
                    res += '_';
                } else if (nc == 'P') {
                    res += '.';
                } else if (nc == 'D') {
                    res += '$';
                } else if (nc == 'H') {
                    res += '#';
                } else if (nc == 'O') {
                    res += '[';
                } else if (nc == 'C') {
                    res += ']';
                } else if (nc == 'S') {
                    res += '/';
                } else {
                    // this case is due to bad encoding
                }
                i = ni + 2;
            } else {
                // this case is due to bad encoding
                break;
            }
        }
        res += s.substring(i);
        return res;
    }

}//end class

