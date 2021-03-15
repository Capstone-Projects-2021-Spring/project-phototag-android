package edu.temple.phototag;

import android.location.Location;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

interface callbackInterface {
    void updateView(View view, ArrayList<String> tags);
}
public class Photo {
    public String id;
    public Date date;
    public Location location;
    public ArrayList<String> tags;
    public String name;
    private callbackInterface listener;
    private View view;

    /**
     * Photo constructor initializes the Photo class with the current tags from the database
     * associated with that id
     * @param id the unique identifier of the photo in the database and local storage
     * @param date the date the photo was taken
     * @param location the location the photo was taken
     * @param name the name assigned to the photo in local storage
     */
    public Photo(String id, Date date, Location location, String name, callbackInterface listener, View view) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.name = name;
        this.tags = new ArrayList<String>();
        this.listener = listener;
        this.view = view;

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
          
            Object object = myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Object object = task.getResult().getValue();
                        HashMap<String, HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>> hashMap =
                                (HashMap<String, HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>>) object;
                        ArrayList<String> arrayList = new ArrayList<>();
                        for (String key : hashMap.get("testUsername").get("Photos").keySet()) {
                            if (key.equals(id)) {
                                arrayList = hashMap.get("testUsername").get("Photos").get(key).get("photo_tags");
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

    public Photo(String id, Date date, Location location, String name) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.name = name;
        this.tags = new ArrayList<String>();

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            Object object = myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Object object = task.getResult().getValue();
                        HashMap<String, HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>> hashMap =
                                (HashMap<String, HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>>) object;
                        ArrayList<String> arrayList = new ArrayList<>();
                        for (String key : hashMap.get("testUsername").get("Photos").keySet()) {
                            if (key.equals(id)) {
                                arrayList = hashMap.get("testUsername").get("Photos").get(key).get("photo_tags");
                                if (arrayList != null) {
                                    setTags(arrayList);
                                }
                            }
                        }
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
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();

            Object object = myRef.child("photoTags").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        HashMap<String, ArrayList<String>> object = (HashMap<String, ArrayList<String>>) task.getResult().getValue();
                        for (String tag : tags) {
                            ArrayList<String> arrayList = object.get(tag);
                            if (arrayList == null) {
                                arrayList = new ArrayList<String>();
                                arrayList.add(id);
                                myRef.child("photoTags").child(tag).setValue(arrayList);
                            } else if (!arrayList.contains(id)) {
                                arrayList.add(id);
                                myRef.child("photoTags").child(tag).setValue(arrayList);
                            }
                        }
                    }
                }
            });

            DatabaseReference child = myRef.child("testUsername").child("Photos").child(this.id);
            for (String tag : tags) {
                DatabaseReference child_tag = myRef.child("photoTags").child(tag);

            }
            this.tags = getTags();
            for (String tag : tags) {
                if (!this.tags.contains(tag)) {
                    this.tags.add(tag);
                }
            }
            child.setValue(this.tags);
        } catch(DatabaseException databaseException) {
            Log.e("Photo.addTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
        }
        this.listener.updateView(this.view, getTags());
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
            DatabaseReference myRef = database.getReference();
            DatabaseReference child = myRef.child("testUsername").child("Photos").child(this.id).child("photo_tags");
            Object object = myRef.child("photoTags").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        HashMap<String, ArrayList<String>> object = (HashMap<String, ArrayList<String>>) task.getResult().getValue();
                        ArrayList<String> arrayList = object.get(tag);
                        if (arrayList == null) {
                            arrayList = new ArrayList<String>();
                            arrayList.add(id);
                            myRef.child("photoTags").child(tag).setValue(arrayList);
                        } else if (!arrayList.contains(id)) {
                            arrayList.add(id);
                            myRef.child("photoTags").child(tag).setValue(arrayList);
                        }
                    }
                }
            });

            this.tags = getTags();
            if (!this.tags.contains(tag)) {
                this.tags.add(tag);
                child.setValue(this.tags);
            }
        } catch(DatabaseException databaseException) {
            Log.e("Photo.addTag", "An error occurred while accessing Firebase database: ", databaseException);
            return false;
        }
        this.listener.updateView(this.view, getTags());
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
            DatabaseReference myRef = database.getReference();

            DatabaseReference child = myRef.child("userName");
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
