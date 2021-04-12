package edu.temple.phototag;
import android.os.Debug;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    //User variables
    private String username;
    private String email;
    private ArrayList<String> imagePaths;
    private HashMap<String, Photo> map;

    //we need a single object of type User to work with. This is below
    private static User userInstance = null;

    //this function only gets called once, and it happens in getInstance.
    private User(){
        this.username = "";
        this.email = "";
        this.map = new HashMap<>();
        this.imagePaths = new ArrayList<>();
    }

    //when we need to access the instance of the User object, we do so with getInstance()
    public static User getInstance(){
        if(userInstance == null){
            userInstance = new User();
        }
        return userInstance;
    }

    /* Setters */
    public void setUsername(String un){ this.username = encodeForFirebaseKey(un); }
    public void setEmail(String em){ this.email = em; }
    public void setImagePaths(ArrayList<String> paths){ this.imagePaths = paths; }
    public void setMap(HashMap<String, Photo> map) { this.map = map; }

    /* Getters */
    public String getUsername() { return username; }
    public String getEmail() { return encodeForFirebaseKey(email); }
    public ArrayList<String> getImagePaths() { return imagePaths; }
    public HashMap<String, Photo> getMap() { return map; }
    //getter for photo object given path
    public Photo getPhoto(String path) { return map.get(path); }

    //getter for all photo objects in map
    public Photo[] getAllPhotoObjects(){
        Photo[] list = new Photo[imagePaths.size()];

        //for each photo in the map, add it to the array
        int i=0;
        for(String key: map.keySet()){
            list[i] = map.get(key);
            i++;
        }//list now contains all the photo objects in the map
        return list;
    }

    //function to add a photo to the user object
    public void addPhoto(Photo p){
        this.map.put(p.path, p);
        this.imagePaths.add(p.path);
    }

    //synchronize the user class with the Firebase DB
    public void syncWithFirebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("Android").child(this.getEmail()).child("Photos");
        //Sync with db:
        DatabaseReference finalRef = ref;
        Object object = ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("User.syncWithFirebase", "Error getting data", task.getException());
            } else {
                DataSnapshot photoObject = task.getResult();
                //child is each photo object in the DB
                for (DataSnapshot photo : photoObject.getChildren()) {
                    //Photo exists in DB but not locally
                    Log.d("Debug", photo.getKey());
                    if(! imagePaths.contains(MainActivity.decodeFromFirebaseKey(photo.getKey()))){
                        finalRef.child(photo.getKey()).removeValue();
                    }
                }
            }
        });
    }

    //private function that modifies string to account for firebase's illegal character rules
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

    public void printImagePaths() {
        for(int i = imagePaths.size() - 1 ; i>=0; i--) {
            Log.d("PATHS", imagePaths.get(i));
        }
    }

}