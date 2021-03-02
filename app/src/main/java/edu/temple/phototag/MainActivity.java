package edu.temple.phototag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GalleryViewFragment.GalleryViewListener {

    ArrayList<Bitmap> images;
    GalleryViewFragment galleryViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new ArrayList<>();

        //test images
        images.add(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.taco1));
        images.add(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.taco2));
        //test images

        FragmentManager fm = getSupportFragmentManager();

        galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.gallery);

        if(galleryViewFragment == null){
            galleryViewFragment = new GalleryViewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("array",images);
            galleryViewFragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.gallery,galleryViewFragment)
                    .commit();
        }
    }
}