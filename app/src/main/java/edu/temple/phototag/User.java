package edu.temple.phototag;
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
    public String getEmail() { return email; }
    public ArrayList<String> getImagePaths() { return imagePaths; }
    public HashMap<String, Photo> getMap() { return map; }
    //getter for photo object given path
    public Photo getPhoto(String path) { return map.get(path); }
    //getter for all photo objects in map
    public Photo[] getAllPhotoObjects(){
        ArrayList<Photo> list = new ArrayList<>();

        //for each photo in the map, add it to the list
        for(String key: map.keySet()){
            list.add(map.get(key));
        }//list now contains all the photo objects in the map

        return (Photo[]) list.toArray();
    }

    //function to add a photo to the user object
    public void addPhoto(Photo p){
        this.map.put(p.path, p);
        this.imagePaths.add(p.path);
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
}