package edu.temple.phototag;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.util.Log;
import android.view.View;
import androidx.exifinterface.media.ExifInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Photo {
    public String id;
    public String path;
    public Date date;
    public Location location;
    public ArrayList<String> tags;
    public String name;
    public boolean MLautoTagged;
    public boolean SautoTagged;
    public int rotation;
    private callbackInterface listener;
    private View view;


    /**
     * Photo Class constructor that only requires the needed arguments
     *      relies on addTag and MLKit.autoLabelBitmap to apply metadata at the correct time
     * @param path
     */
    public Photo(String path){
        this.path = path;
        this.id = encodeForFirebaseKey(this.path);
        this.tags = new ArrayList<>();
        this.name = null;
        this.date = findDate();
        this.location = null;
        this.rotation = findRotation();
        findMLAutoTagged();
        findSAutoTagged();

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
        } catch (DatabaseException databaseException) {
            Log.e("Photo.constructor", "An error occurred while accessing Firebase database: ", databaseException);
        }
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
     * for getting the date of the photo represented as seconds since java epoch
     * @return long of the number of seconds since epoch
     */
    public long getDateFromEpoch(){
        return this.date.toInstant().getEpochSecond();
    }

    /**
     * getLocation returns the location of the Photo object
     * @return location of the calling Photo object
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Get local autoTagged bool value
     * @return bool of local autoTagged variable of this photo
     */
    public boolean getMLAutoTagged(){ return MLautoTagged; }


    public boolean getSAutoTagged(){ return SautoTagged; }

    /**
     * Get the bool from the db to tell if a photo has been autoTagged
     * @return void, sets this photo's autoTagged var
     */
    public void findMLAutoTagged() {
        //Check Database for autotagged
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("Android").child(User.getInstance().getEmail()).child("Photos").child(this.id).child("AutoTagged");

        Object object = ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {

                Log.e("Photo.getMLAutoTagged", "Error getting data", task.getException());
                MLautoTagged = false;
            } else {
                DataSnapshot autoTagBool = task.getResult();

                if (autoTagBool.getValue() != null) {
                    if ((Boolean)autoTagBool.getValue() == true) {
                        MLautoTagged = true;
                        Log.d("Photo.getMLAutoTagged", "Value: " + MLautoTagged + "|Photo: " + this.path);
                    }
                }else{
                        MLautoTagged = false;
                        Log.d("Photo.getMLAutoTagged", "Value: " + MLautoTagged + "|Photo: " + this.path);
                    }
            }
        });
    }

    public void findSAutoTagged() {
        //Check Database for autotagged
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("Android").child(User.getInstance().getEmail()).child("Photos").child(this.id).child("ServerAutoTagged");

        Object object = ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {

                Log.e("Photo.getSAutoTagged", "Error getting data", task.getException());
                SautoTagged = false;
            } else {
                DataSnapshot autoTagBool = task.getResult();

                if (autoTagBool.getValue() != null) {
                    if ((boolean) autoTagBool.getValue()) {
                        SautoTagged = true;
                        Log.d("Photo.getSAutoTagged", "Value: " + SautoTagged + "|Photo: " + this.path);
                    }
                }else{
                    SautoTagged = false;
                    Log.d("Photo.getSAutoTagged", "Value: " + SautoTagged + "|Photo: " + this.path);
                }
            }
        });
    }





    /**
     * getTags returns the list of tags of the Photo object
     * @return ArrayList of tags of the calling Photo object
     */
    public ArrayList<String> getTags() {return this.tags;}

    /**
     * setTags applies the array of tags to the photo object
     * @param array ArrayList of Strings that represent the tags assigned to that photo
     */
    public void setTags(ArrayList<String> array) {
        this.tags.addAll(array);
    }

    /**
     * For retrieving the rotation needed to view the image correctly
     * in degrees from image exif data
     * @return int: number of degrees to rotate to it's correct orientation
     */
    public int findRotation(){
        int rotation = 0;
        try {
            ExifInterface exif = new ExifInterface(this.path);
            //Get Rotation - should be used to make sure photos are displayed correctly in gallery/single photo view
            rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            rotation = exifToDegrees(rotation);
            Log.d("Photo-Rotation", "Rotation: " + rotation);
            return rotation;
        }catch(IOException e){
            Log.d("Photo.getRotation",e.getMessage());
        }
        return rotation;
    }

    /**
     * For converting orientation information from image exif data to degrees
     * @param exifOrientation
     * @return
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    /**
     * For getting a photos image in the correct orientation
     * @return Bitmap: the bitmap for this photo correctly oriented
     */
    public Bitmap getRotatedBitmap(){
        //get the original bitmap and then rotate it according to its metadata
        Bitmap original = BitmapFactory.decodeFile(this.path);
        Matrix matrix = new Matrix();
        matrix.setRotate(this.rotation);
        Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        return rotated;
    }

    /**
     * For getting a photos thumbnail in the correct orientation
     * @return Bitmap: a thumbnail size bitmap of the photo's image in the correct orientation
     */
    public Bitmap getRotatedThumbnail(){
        //Set up option for the thumbnail
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 100;
        options.outHeight = 100;
        Bitmap bitmap = BitmapFactory.decodeFile(this.path,options);
        //rotate the thumbnail bitmap
        Bitmap original = bitmap;
        Matrix matrix = new Matrix();
        matrix.setRotate(this.rotation);
        Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, false);
        //return the correctly orientated thumbnail bitmap
        return rotated;
    }


    /**
     * For finding the Date & Time information from an image file in its' exif data
     * @return Date: the date/time information found in the image exif data
     */
    public Date findDate(){
        String dateTimeDig = "";
        try {
            ExifInterface exif = new ExifInterface(this.path);

            //Get DateTime Info and format it
            dateTimeDig = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US);
            if(dateTimeDig != null){
                try {
                    Log.d("Photo.findDate", "Found Date: " + dateTimeDig);
                    Date photoDate = simpleDateFormat.parse(dateTimeDig);
                    return photoDate;
                }catch(ParseException e){
                    Log.d("Photo.findDate", "Date Format Failure: " + e);
                    return null;
                }
            }
        }catch(IOException e){
            Log.d("Photo.findDate", "Exif Fail: " + e);
            return null;
        }
        return null;
    }

    /**
     * For Adding the date & time information for a photo both locally and to the db
     * @param pDate
     * @return boolean: success = true | failure = false
     */
    public boolean setDate(Date pDate){
        try{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if(pDate != null){
                //Get down to the user in the database
                DatabaseReference dateRef = database
                        .getReference()
                        .child("Android")
                        .child(User.getInstance().getEmail());

                //add date time information to the photo in the db as seconds from epoch
                dateRef.child("Photos")
                        .child(this.id)
                        .child("DateTime")
                        .setValue(pDate.toInstant().getEpochSecond());

                //add date time information to the photo tags in the db as seconds from epoch
                dateRef.child("PhotoTags")
                        .child(Long.toString(pDate.toInstant().getEpochSecond()))
                        .child(this.id)
                        .setValue(true);

                //add date time information to the local photo
                this.date = pDate;
                return true;
            }
        }catch(DatabaseException databaseException){
            Log.d("Photo.setDate","Failed To Update DB Photo Date: " + databaseException);
            return false;
        }
        return false;
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
                //say that this was auto tagged
                ref.child("Photos").child(this.id).child("AutoTagged").setValue(true);

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
                //Add Tag to User's PhotoTags List
                ref.child("PhotoTags").child(finalTag).child(this.id).setValue(true);
                //Add Tag to Photo's Tags
                ref.child("Photos").child(this.id).child("photo_tags").child(finalTag).setValue(true);

                //since the autoTag bool on the photo in the db is set true only after the tag has
                // been added to the photo, this will run regardless of if it was a manual or auto added tag.
                // this way the photos will always have this data in the db
                if(!getMLAutoTagged() || !getSAutoTagged()){
                    setDate(findDate());
                    //setLocation(findLocation());
                }

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

            ref.child("Android").child(User.getInstance().getEmail()).child("Photos").child(this.id).child("photo_tags").child(tag).removeValue();
            ref.child("Android").child(User.getInstance().getEmail()).child("PhotoTags").child(tag).child(this.id).removeValue();
        } catch (DatabaseException databaseException) {
            Log.e("Photo.removeTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
        }
        return true;
    }

    //from https://stackoverflow.com/questions/19132867/adding-firebase-data-dots-and-forward-slashes/39561350#39561350
    /**
     * Function encodes the given string so that the paths can be accepted as Firebase keys
     * @param key the key that will be passed into Firebase
     * @return the key that was passed in but with any illegal character replaced
     */
    public static String encodeForFirebaseKey(String key) {
        return key
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