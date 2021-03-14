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
        //TextView textView = v.findViewById(R.id.tags); //instance of text view

        //get bundle from activity
        Bundle bundle = getArguments();

        //get path of photo from bundle
        assert bundle != null;
        String path = bundle.getString("photo");
        callback obj = new callback();
        Thread thread = new Thread(() -> {
            Photo photo = new Photo(path.substring(29, path.length() - 4), null, null, null, obj, v);

            //display photo in image view
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));

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
        });
        thread.start();
        return v;
    }

}

