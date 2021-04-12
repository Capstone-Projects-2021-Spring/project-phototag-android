package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity implements ScheduleFragment.ScheduleInterface, SettingsFragment.SettingsInterface, GalleryViewFragment.GalleryViewListener, SearchViewFragment.SearchViewListener, LoginFragment.LoginInterface{
    //General variables
    String[] paths; //initiate array of paths
    ArrayList<String> paths2, paths3, parsedTags;
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
        fm = getSupportFragmentManager();

        loginViewFragment = (LoginFragment) fm.findFragmentById(R.id.main);
        //galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.main);

        //create login view  fragment if it doesn't exist, then load.
        if(loginViewFragment == null) {
            fm.beginTransaction().add(R.id.main, LoginFragment.newInstance()).commit();
        }
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

                //lists for data
                parsedTags = new ArrayList<>();
                paths2 = new ArrayList<>();
                paths3 = new ArrayList<>();


                //delimiter used to divide tags
                Scanner input = new Scanner(query.toLowerCase()).useDelimiter(",|search for |search |show me |show |a |during |the |in |with |and ");
              
                //separate tags by delimiter and add to array list
                while(input.hasNext()){

                    parsedTags.add(input.next().trim());

                }

                parsedTags.removeIf(String::isEmpty);

                //loop through tags
                for(int i = 0; i < parsedTags.size(); i++) {

                    //Log.d("tags",""+parsedTags.get(i) + "" + i);

                    int finalI = i;//reference to tag position in arraylist
                    int mod = i % 2;//mod to tell if position is odd or even

                    if (!query.contains(".") && !query.contains("#") && !query.contains("$") && !query.contains("[") && !query.contains("]")) {
                        //query db with tag

                        ref.child("Android").child(User.getInstance().getEmail()).child("PhotoTags").child(parsedTags.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                    if (finalI == parsedTags.size() - 1 && !paths2.isEmpty() && mod == 0) {
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
                                    if (finalI == parsedTags.size() - 1 && !paths3.isEmpty() && mod == 1) {
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
     * @param requestCode the code of the permissions request
     * @param permissions the list of permissions being asked for by the app
     * @param grantResults for asking user permission to device storage. makes app synchronous.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Denied to local Photos", Toast.LENGTH_LONG).show();
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
            Photo p = new Photo(cursor.getString(dataColumnIndex));
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
            //Do On Device Auto Tagging Here
            MLKitProcess.autoLabelPhotos(userReference.getAllPhotoObjects());
        }
        if(shPref.getBoolean("autoTagSwitch", false) && shPref.getBoolean("serverTagSwitch", false)) {
            //Do Server Auto Tagging Here
            Thread thread = new Thread(() -> {
                for (Photo photo : userReference.getAllPhotoObjects()) {
                    connectServer(photo, userReference.getUsername());
                }
            });
            thread.start();
        }
        //-End-     Perform Auto Tagging

        String[] keyArray = userReference.getImagePaths().toArray(new String[userReference.getMap().keySet().size()]);
        fm.beginTransaction().replace(R.id.main, GalleryViewFragment.newInstance(keyArray)).commit();
        //Only show settings and search button after logging in. This method is only called upon succesful login.
        searchButton.setVisible(true);
        settingsButton.setVisible(true);

        checkSchedules();
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

    @Override
    public void loadScheudleFragment() {
        //begin fragment
        fm.beginTransaction()
                .replace(R.id.main, ScheduleFragment.newInstance())
                .addToBackStack(null)
                .commit();
        Log.d("SCHEDULE", "Schedule fragment loaded.");
    }

    //SETTINGS INTERFACE IMPLEMENTATIONS END ****************


    //SCHEDULE INTERFACE IMPLEMENTATIONS BELOW ****************

    //This will create the schedule in the DB
    @Override
    public void saveSchedule(String name , long startD, long endD, String tag) {
        //Get DB reference
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();
        DatabaseReference ref = myRef.child("Android").child(User.getInstance().getEmail());
        //Schedule directory in DB , set the startDate in
        ref.child("Schedules").child(name).child("startTime").child(String.valueOf(startD)).setValue(true);
        //Set the endDate
        ref.child("Schedules").child(name).child("endTime").child(String.valueOf(endD)).setValue(true);
        //Set the tag associated w/ schedule
        ref.child("Schedules").child(name).child("Tags").child(tag).setValue(true);
    }//end saveSchedule()

    //This will scan the local photos, starting with the most recent(highest indexed paths), and compare date data w/schedules.
    @Override
    public void checkSchedules() {
        ArrayList<String> localPaths = userReference.getImagePaths();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();
        DatabaseReference ref = myRef.child("Android").child(User.getInstance().getEmail()).child("Schedules");
        Object object = ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("SCHEDULE", "Error getting schedule data", task.getException());
            } else {
                DataSnapshot sObject = task.getResult();
                for (DataSnapshot s : sObject.getChildren()) {
                    //For every schedule in our DB, each local photo for DateTime metadata.
                    for(int i = 0 ; i < localPaths.size() ; i++) {
                        if(userReference.getPhoto(localPaths.get(i)).getDate() != null) {
                            int endIndex = -1;
                            long photoDate = userReference.getPhoto(localPaths.get(i)).getDateFromEpoch();
                            //Substringing results for startTime
                            for(char c : s.child("startTime").getValue().toString().toCharArray()) {
                                endIndex++;
                                if(c == '=') {
                                   break;
                                }
                            }
                            String temp = s.child("startTime").getValue().toString().substring(1,endIndex );
                            long startTime = Long.parseLong(temp);
                            Log.d("abc", temp + " " + String.valueOf(temp));
                            endIndex = -1;
                            //Substringing results for endTime
                            for(char c : s.child("endTime").getValue().toString().toCharArray()) {
                                endIndex++;
                                if(c == '=') {
                                    break;
                                }
                            }//end for(char c : s.child("endTime").getValue().toString().toCharArray())

                            temp = s.child("endTime").getValue().toString().substring(1,endIndex);
                            long endTime = Long.parseLong(temp);
                            Log.d("abc", "endTime" + String.valueOf(i) + ": " + temp + " " + String.valueOf(temp));
                            //Check if the epochTime in current photo is between start and end time.
                            if(photoDate >= startTime && photoDate <= endTime) {

                                //ADD THE TAG ASSOCIATED W/ SCHEDULE TO PHOTO.
                                //Substring it for correct result.
                                endIndex = -1;
                                for(char c: s.child("Tags").getValue().toString().toCharArray()) {
                                    endIndex++;
                                    if(c== '=') {
                                        break;
                                    }
                                }
                                String tagToAdd = s.child("Tags").getValue().toString().substring(1, endIndex);
                                Log.d("abc", tagToAdd);

                                //Add tag to photo.
                                ArrayList<String> tagList = new ArrayList<>();
                                tagList.add(tagToAdd);
                                userReference.getPhoto(localPaths.get(i)).setTags(tagList);
                            }//if(photoDate >= startTime && photoDate <= endTime)

                        }//end if(userReference.getPhoto(localPaths.get(i)).getDate() != null)

                    }//end for(int i = 0 ; i < localPaths.size() ; i++)
                }//end for (DataSnapshot s : sObject.getChildren())
            }
        });

    }//end checkSchedules

    //SCHEDULE INTERFACE IMPLEMENTATIONS END****************


    //from https://stackoverflow.com/questions/19132867/adding-firebase-data-dots-and-forward-slashes/39561350#39561350
    /**
     * Decode the Firebase key so that the illegal characters are put back in the filename
     * @param key Firebase friendly key that needs to be decoded into its original text
     * @return A converted string that holds the actual key with the special characters included
     */
    public static String decodeFromFirebaseKey(String key) {
        int i = 0;
        int ni;
        String res = "";
        while ((ni = key.indexOf("_", i)) != -1) {
            res += key.substring(i, ni);
            if (ni + 1 < key.length()) {
                char nc = key.charAt(ni + 1);
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
        res += key.substring(i);
        return res;
    }

    // Android server connection begins

    /**
     * connectServer function performs the API requesting to the Python server
     */
    static void connectServer(Photo photo, String username){
        String path = photo.path;
        String id = photo.getID();
        String ipv4Address = "api.sebtota.com";
        int portNumber = 5000;
        //String ipv4Address = "127.0.0.1";
        try {
            HttpUrl getUrl = new HttpUrl.Builder().scheme("https")
                    .host(ipv4Address)
                    .port(portNumber)
                    .addPathSegment("uploadImage")
                    .build();
            File file = new File(path);
            RequestBody image = RequestBody.create(MediaType.parse(
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            MimeTypeMap.getFileExtensionFromUrl(path)
                    )
            ), file);
            String PLATFORM = "Android";
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", username)
                    .addFormDataPart("platform", PLATFORM)
                    .addFormDataPart("photo_identifier", id)
                    .addFormDataPart("image", id, image)
                    .build();
            System.out.println(getUrl);

            // MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            // RequestBody getBody = RequestBody.create(mediaType, getBodyJSON.toString());

            postRequest(getUrl, requestBody);
        }catch(NullPointerException e){
            Log.d("Server Autotagging", "connectServer: " + e);
        }
    }

    /**
     * getRequest sends request to the designated url passed into it wit
     * @param postUrl the url of the get request that is meant to be made
     * @param postBody the body of the get request that is meant to be made
     */
    static void postRequest(HttpUrl postUrl, RequestBody postBody) {

        try {

            // create the client used to make the http request
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .writeTimeout(0, TimeUnit.SECONDS)
                    .readTimeout(0, TimeUnit.SECONDS)
                    .build();

            // build out the request with the url, headers, body, and method
            Request request = new Request.Builder()
                    .addHeader("Connection", "close")
                    .url(postUrl)
                    .post(postBody)
                    .build();

            Call call = client.newCall(request);
            try (Response response = call.execute()) {
                Log.d("SERVER", response.code() + ": " + response.message());
                response.body().close();
            } catch (IOException e) {
                Log.d("SERVER ERROR", "" + e);
            }
        }catch(NullPointerException e){
            Log.d("Server Autotagging", "postRequest: " + e);
        }
    }
    //end of server connection

}//end class

