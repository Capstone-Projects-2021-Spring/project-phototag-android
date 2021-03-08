package edu.temple.phototag;

import android.location.Location;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class Photo {
    public int id;
    public Date date;
    public Location location;
    public ArrayList<String> tags;
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

    public ArrayList<String> getTags(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                Log.d("getTags", "Value is: " + value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("getTags", "Failed to read value.", error.toException());
            }
        });

        return this.tags;
    }

    public boolean addTag(String tag) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");

        DatabaseReference child = myRef.child(String.valueOf(this.id));
        this.tags = getTags();
        this.tags.add(tag);
        child.setValue(tag);
        return true;
    }

    public boolean removeTag(String tag) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("https://phototag-6ec4a-default-rtdb.firebaseio.com/");

        DatabaseReference child = myRef.child(String.valueOf(this.id));
        child.setValue(tag);
        return true;
    }
}
