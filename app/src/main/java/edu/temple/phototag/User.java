package edu.temple.phototag;


import android.util.Log;

import java.util.HashMap;

public class User {

    //User variables.
    String username;
    String email;
    String[] imagePaths;

    public User(String u, String e, String[] p) {
        username = u;
        email = e;
        imagePaths = p;
        Log.d("USER", username);
        Log.d("USER", email);
    }//end User(String u, String e)




}//end class
