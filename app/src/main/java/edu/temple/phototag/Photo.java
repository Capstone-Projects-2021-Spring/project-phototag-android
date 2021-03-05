package edu.temple.phototag;

import android.location.Location;
import android.media.Image;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Date;

public class Photo {
    public int id;
    public Date date;
    public Location location;
    public Array tags;
    public Image UIImage;

    public int getID() {
        return this.id;
    }

    public Date getStartDate() {
        return this.date;
    }

    public Date getEndDate() {
        return this.date;
    }

    public Location getLocation() {
        return this.location;
    }

    public Array getTags() {
        return this.tags;
    }

    public boolean addTag(String tag) {
        try {
            FileInputStream serviceAccount =new FileInputStream("C:/Users/16097/Desktop/project-phototag-android/.idea/modules/firebase_key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://phototag-6ec4a-default-rtdb.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);

        } catch (FileNotFoundException err) {
            Log.e("FIREBASE", err.getMessage());
            return false;
        }
        return true;
    }

    public boolean removeTag(String tag) {
        FileInputStream serviceAccount =
                new FileInputStream("path/to/serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://phototag-6ec4a-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        return false;
    }
}
