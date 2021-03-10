package edu.temple.phototag;

import android.location.Location;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Photo {
    public int id;
    public Date date;
    public Location location;
    public ArrayList<String> tags;
    public String name;

    /**
     * Photo constructor initializes the Photo class with the current tags from the database
     * associated with that id
     * @param id the unique identifier of the photo in the database and local storage
     * @param date the date the photo was taken
     * @param location the location the photo was taken
     * @param name the name assigned to the photo in local storage
     */
    public Photo(int id, Date date, Location location, String name) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.name = name;

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");
            final ArrayList<String> temp_tags = new ArrayList<>();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Object value = snapshot.getValue();
                    ArrayList<String> arrayList = (ArrayList<String>) value;
                    temp_tags.addAll(arrayList);
                    Log.d("getTags", "Value is: " + value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("getTags", "Failed to read value.", error.toException());
                }
            });
            this.tags = temp_tags;
        } catch (DatabaseException databaseException) {
            Log.e("Photo.constructor", "An error occurred while accessing Firebase database: ", databaseException);
        }
    }

    /**
     * getID returns the id of the Photo object
     * @return id of the calling Photo object
     */
    public int getID() {
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

    /**
     * addTags adds a list of tags to the Photo object as well as to the database
     * @param tags a list of tags to be added to the Photo
     * @return true for a successful addition/ false if an error occurred
     */
    public boolean addTags(List<String> tags) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");

            DatabaseReference child = myRef.child(String.valueOf(this.id));
            this.tags = getTags();
            this.tags.addAll(tags);
            child.setValue(this.tags);
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
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");

            DatabaseReference child = myRef.child(String.valueOf(this.id));
            this.tags = getTags();
            this.tags.add(tag);
            child.setValue(this.tags);
        } catch(DatabaseException databaseException) {
            Log.e("Photo.addTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");

            DatabaseReference child = myRef.child(String.valueOf(this.id));
            this.tags = getTags();
            this.tags.remove(tag);
            child.setValue(this.tags);
        } catch (DatabaseException databaseException) {
            Log.e("Photo.removeTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
        }
        return true;
    }
}
