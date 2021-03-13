package edu.temple.phototag;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;


/**
 * Class to display single image in full along with tags
 */

public class SinglePhotoViewFragment extends Fragment {

    static TextView textView;
    static String[] autoTags = new String[10];//MLKit only returns 10 tags by defualt


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *
     * for creating views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_photo_view, container, false);

        ImageView imageView = v.findViewById(R.id.imageView); //instance of image view
        textView = v.findViewById(R.id.tags); //instance of text view


        //
        //get bundle from activity
        Bundle bundle = getArguments();

        //get path of photo from bundle
        assert bundle != null;
        String path = bundle.getString("photo");

        //display photo in image view
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));

        //Clear Tag Array for new tags
        Arrays.fill(autoTags, null);

        //get tags from ML Kit
        //MLKitProcess.labelImage(BitmapFactory.decodeFile(path));
        MLKitProcess.labelImage(path);

        return v;
    }

}

