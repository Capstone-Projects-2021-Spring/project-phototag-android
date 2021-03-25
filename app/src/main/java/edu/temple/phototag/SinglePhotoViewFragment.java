package edu.temple.phototag;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import edu.temple.phototag.Photo;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import java.util.ArrayList;

/**
 * Class to display single image in full along with tags
 */
class callback implements callbackInterface {
    @Override
    public void updateView(View view, ArrayList<String> tags) {
        Log.d("db", "called callback");
        if (!tags.isEmpty()) {
            ((TextView) view.findViewById(R.id.tags)).setText(tags.toString());
        }
    }
}

public class SinglePhotoViewFragment extends Fragment {

    TextView textView;
    TextView sugTag;
    String[] autoTags = new String[10];//MLKit only returns 10 tags by defualt


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
        sugTag = v.findViewById(R.id.tagSug);

        //
        //get bundle from activity
        Bundle bundle = getArguments();

        //get path of photo from bundle
        assert bundle != null;
        String path = bundle.getString("photo");
        //String[] idArray = path.split("/");
        //String id = idArray[idArray.length - 1].substring(0, idArray[idArray.length - 1].length() - 4);
        /*
        String id = null;
        try {
            id = URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
         */
        //String id = encodeForFirebaseKey(path);
        //Log.d("debugging_id",id);
        callback obj = new callback();

        //display photo in image view
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        Photo photo = User.getInstance().getPhoto(path);

        EditText input = v.findViewById(R.id.custom);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Perform your Actions here.
                    if (photo.addTag(input.getText().toString())) {
                        handled = true;
                    }
                }
                return handled;
            }

        });

      if (!photo.getTags().isEmpty()) {
            ((TextView)v.findViewById(R.id.tags)).setText(photo.getTags().toString());
        }
        //Clear Tag Array for new tags
        Arrays.fill(autoTags, null);

        //get and apply tags from ML Kit
        MLKitProcess.labelImage(photo);

        return v;
    }

    public static String encodeForFirebaseKey(String s) {
        return s
                .replace("_", "__")
                .replace(".", "_P")
                .replace("$", "_D")
                .replace("#", "_H")
                .replace("[", "_O")
                .replace("]", "_C")
                .replace("/", "_S")
                ;
    }
}