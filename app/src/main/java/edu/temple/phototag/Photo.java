package edu.temple.phototag;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Photo {
    public String id;
    public String path;
    public Date date;
    public Location location;
    public ArrayList<String> tags;
    public String name;
    private callbackInterface listener;
    private View view;

    /**
     * Photo constructor initializes the Photo class with the current tags from the database
     * associated with that id
     * @param path the unique identifier of the photo in the database and local storage
     * @param date the date the photo was taken
     * @param location the location the photo was taken
     * @param name the name assigned to the photo in local storage
     * @param listener interface used to make the callback function required to update textView
     * @param view the view that holds the textView for the tags
     */
    public Photo(String path, Date date, Location location, String name, callbackInterface listener, View view) {
        this.path = path;
        this.id = encodeForFirebaseKey(this.path);
        this.date = date;
        this.location = location;
        this.name = name;
        this.tags = new ArrayList<>();
        this.listener = listener;
        this.view = view;

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
          
            Object object = myRef.child("testUsername").child("Photos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Object object = task.getResult().getValue();
                        HashMap<String, HashMap<String, ArrayList<String>>> hashMap =
                                (HashMap<String, HashMap<String, ArrayList<String>>>) object;
                        ArrayList<String> arrayList;
                        for (String key : hashMap.keySet()) {
                            if (key.equals(id)) {
                                arrayList = hashMap.get(key).get("photo_tags");
                                if (arrayList != null) {
                                    setTags(arrayList);
                                }
                            }
                        }
                        listener.updateView(view, getTags());
                    }
                }
            });

        } catch (DatabaseException databaseException) {
            Log.e("Photo.constructor", "An error occurred while accessing Firebase database: ", databaseException);
        }
    }

    /**
     * A constructor for the Photo class that does not require the callback interface or the view
     * to be passed in
     * @param path
     * @param date
     * @param location
     * @param name
     */
    public Photo(String path, Date date, Location location, String name) {
        this.path = path;
        this.id = encodeForFirebaseKey(this.path);
        this.date = date;
        this.location = location;
        this.name = name;
        this.tags = new ArrayList<>();



        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("Android").child(User.getInstance().getEmail()).child("Photos").child(this.id).child("photo_tags");
            Object object = myRef.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot photoObject = task.getResult();
                    for (DataSnapshot child : photoObject.getChildren()) {
                        tags.add(child.getKey());
                    }
                }
            });
        } catch (DatabaseException databaseException) {
            Log.e("Photo.constructor", "An error occurred while accessing Firebase database: ", databaseException);
        }
    }

    public Photo(String path){
        this.path = path;
        this.id = encodeForFirebaseKey(this.path);
        this.tags = new ArrayList<>();
        this.name = null;
        this.date = null;
        this.location = null;

        String dateTimeDig = "";

        /* test variables for finding how to retrieve photo metadata
        int rotation = 0;
        String dateTimeInfo = "";
        String dateTimeOri = "";
        String lat = "";
        String latRef = "";
        String longNorm = "";
        String longRef = "";
        double[] latLong = new double[2];
         */

        try {
            ExifInterface exif = new ExifInterface(path);

            //Get Rotation - should be used to make sure photos are displayed correctly in gallery/single photo view
            //rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            //rotation = exifToDegrees(rotation);
            //Log.d("Photo-Rotation", "Rotation: " + rotation);

            //Get DateTime Info
            dateTimeDig = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US);
            if(dateTimeDig != null){
                try {
                    Date photoDate = simpleDateFormat.parse(dateTimeDig);
                    this.date = photoDate;
                    //Timestamp dateData = new Timestamp(dateTimeDig);
                }catch(ParseException e){
                    Log.d("Photo-Failure", "Date Failure: " + e);
                    this.date = null;
                }
            }
            //Log.d("Photo-DateTime", "DateTimeDigital: " + dateTimeDig);

            //datetime seems to return somethinf different sometimes
            //datetime original seems to always be the same as datetime digital
            //dateTimeInfo = exif.getAttribute(ExifInterface.TAG_DATETIME);
            //dateTimeOri = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
            //Log.d("Photo-DateTime", "DateTime: " + dateTimeInfo);
            //Log.d("Photo-DateTime", "DateTimeOriginal: " + dateTimeOri);

            /* unable to get location information this way at this time
            //Get Location Information
            lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            longNorm = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            longRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            Log.d("Photo-Location", "Lat: " + lat);
            Log.d("Photo-Location", "LatRef: " + latRef);
            Log.d("Photo-Location", "Long: " + longNorm);
            Log.d("Photo-Location", "LongRef: " + longRef);

            try{
                latLong = exif.getLatLong();
                Log.d("Photo-Location", "getLat: " + latLong[0]);
                Log.d("Photo-Location", "getLong: " + latLong[1]);
            }catch(Exception e){
                Log.d("Photo-Failure", "getLatLong: " + e);
            }
             */
        }catch(IOException e){
            Log.d("Photo-Failure", "MetaData Fail: " + e);
            this.date = null;
        }

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("Android").child(User.getInstance().getEmail()).child("Photos").child(id);
            Object object = myRef.child("photo_tags").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot photoObject = task.getResult();
                    for (DataSnapshot child : photoObject.getChildren()) {
                        tags.add(child.getKey());
                    }
                }
            });


            if(date != null){
                Log.d("Photo-Date","set Date: " + date.toString());
                DatabaseReference dateRef = database
                        .getReference()
                        .child("Android")
                        .child(User.getInstance().getEmail())
                        .child("Photos")
                        .child(id)
                        .child("DateTime");

                dateRef.setValue(date.toString());
            }
        } catch (DatabaseException databaseException) {
            Log.e("Photo.constructor", "An error occurred while accessing Firebase database: ", databaseException);
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


    /**
     * getID returns the id of the Photo object
     * @return id of the calling Photo object
     */
    public String getID() {
        return this.id;
    }

    /**
     * getDate returns the date of the Photo object
     * @return date of the calling Photo object
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * getLocation returns the location of the Photo object
     * @return location of the calling Photo object
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * getTags returns the list of tags of the Photo object
     * @return ArrayList of tags of the calling Photo object
     */
    public ArrayList<String> getTags() {return this.tags;}

    public void setTags(ArrayList<String> array) {
        this.tags.addAll(array);
    }

    /**
     * addTags adds a list of tags to the Photo object as well as to the database
     * @param tags a list of tags to be added to the Photo
     * @return true for a successful addition/ false if an error occurred
     */
    public boolean addTags(List<String> tags) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref = ref.child("Android").child(User.getInstance().getEmail());

            String lowerCaseTag; //temporary tag string reused in loop
            //add each tag passed in to the DB
            for(String tag: tags){
                lowerCaseTag = tag.toLowerCase();

                //sets the tag in the Photos folder
                ref.child("Photos").child(this.id).child("photo_tags").child(lowerCaseTag).setValue(true);
                //sets the tag in the PhotoTags folder
                ref.child("PhotoTag").child(lowerCaseTag).child(this.id).setValue(true);

                //also add the tags to the local photo object's tag list
                if(! this.tags.contains(lowerCaseTag)){
                    this.tags.add(lowerCaseTag);
                }
            }

            //update the view with the newly-added tags
            if (this.view != null)
                this.listener.updateView(this.view, getTags());

        } catch(DatabaseException databaseException) {
            Log.e("Photo.addTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
        }
        return true;
    }


    /**
     * addTag adds a tag to the Photo object as well as to the database
     * @param tag a tag to be added to the Photo
     * @return true for a successful addition/ false if an error occurred
     */
    public boolean addTag(String tag) {
        if (!tag.contains(".") && !tag.contains("#") && !tag.contains("$") && !tag.contains("[") && !tag.contains("]")) {
            String finalTag = tag.toLowerCase();
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                //root -> android -> username -> photos & phototags
                DatabaseReference ref = myRef.child("Android").child(User.getInstance().getEmail());

                //These two lines actually add the tag to both locations in the DB
                ref.child("PhotoTags").child(finalTag).child(this.id).setValue(true);
                ref.child("Photos").child(this.id).child("photo_tags").child(finalTag).setValue(true);
                //ref.child("Photos").child(this.id).child("AutoTagged").setValue(false);


                if (!this.tags.contains(finalTag)) {
                    this.tags.add(finalTag);
                    if (this.view != null) {
                        this.listener.updateView(this.view, getTags());
                    }
                }
            } catch (DatabaseException databaseException) {
                Log.e("Photo.addTag", "An error occurred while accessing Firebase database: ", databaseException);
                return false;
            }
        }
        return true;
    }

    /**
     * removeTag removes a tag from the Photo object as well as from the database
     * @param tag a tag to be removed from the Photo
     * @return true for a successful removal/ false if an error occurred
     */
    public boolean removeTag(String tag) {
        try{
            //remove the tag from the arraylist of tags
            this.tags.remove(tag);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

            //remove the tag from the photo object in the DB
            ref = ref.child("Android").child(User.getInstance().getEmail()).child("Photos").child(this.id).child("photo_tags").child(tag);
            ref.removeValue();
        } catch (DatabaseException databaseException) {
            Log.e("Photo.removeTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
        }
        return true;
    }

    //from https://stackoverflow.com/questions/19132867/adding-firebase-data-dots-and-forward-slashes/39561350#39561350
    public static String encodeForFirebaseKey(String s) {
        return s
                .replace("_", "__")
                .replace(".", "_P")
                .replace("$", "_D")
                .replace("#", "_H")
                .replace("[", "_O")
                .replace("]", "_C")
                .replace("/", "_S");
    }
}

interface callbackInterface {
    void updateView(View view, ArrayList<String> tags);
}