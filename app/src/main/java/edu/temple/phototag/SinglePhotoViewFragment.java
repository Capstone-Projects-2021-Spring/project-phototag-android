package edu.temple.phototag;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.ArrayList;

public class SinglePhotoViewFragment extends Fragment {

    TextView addedTags;
    static TextView mlkitTags;
    TextView serverTags;
    static String[] autoTags = new String[10]; //MLKit only returns 10 tags by defualt

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return for creating views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_photo_view, container, false);

        ImageView imageView = v.findViewById(R.id.imageView); //instance of image view
        addedTags = v.findViewById(R.id.tags); //instance of text view
        mlkitTags = v.findViewById(R.id.tagSug);
        serverTags = v.findViewById(R.id.serverLabel);

        //get bundle from activity
        Bundle bundle = getArguments();

        //get path of photo from bundle
        assert bundle != null;
        String path = bundle.getString("photo");

        //display photo in image view
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        User userReference = User.getInstance();
        Photo photo = userReference.getPhoto(path);

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
                    addedTags.setText(photo.getTags().toString());
                }
                return handled;
            }
        });

        Log.d("Debug", photo.getTags().toString());
        if (!photo.getTags().isEmpty()) {
            ((TextView) v.findViewById(R.id.tags)).setText(photo.getTags().toString());
        }
        //Clear Tag Array for new tags
        Arrays.fill(autoTags, null);

        //get and apply tags from ML Kit
        MLKitProcess.labelImage(photo);

        //make server request
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(shPref.getBoolean("serverTagSwitch", false)){
            //Do On Device server suggested tagging Here
            

            //serverTags.setText();
        }

        return v;
    }
}


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