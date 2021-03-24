package edu.temple.phototag;


import android.util.Log;

import java.util.HashMap;

public class User {

    //User variables.
    static String  username;
    static String email;
    static String[] imagePaths;
    HashMap<String, Photo> map ;

    private static User userInstance;

    private User() {

    }//end User(String u, String e)


    //Singleton
    public static User getInstance() {
        if(userInstance == null) {
            userInstance = new User();
        }
        return userInstance;
    }//end getInstance()

    public void setData(String u, String e, String[] p) {
        username = u;
        email = e;
        imagePaths = p;
        createMap();
    }//end setData()

    private void createMap() {
        for(int i = 0 ; i < imagePaths.length ; i++) {
            if(!map.containsKey(imagePaths[i])) {
                map.put(imagePaths[i], new Photo(imagePaths[i], null, null, null));
            }
        }
    }

    public Photo getPhoto(String p) {
        return map.get(p);
    }

}//end class
