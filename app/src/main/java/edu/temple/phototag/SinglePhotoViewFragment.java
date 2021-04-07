package edu.temple.phototag;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.ArrayList;

public class SinglePhotoViewFragment extends Fragment {

    static ArrayList<String> autoTags = new ArrayList<>(); //MLKit only returns 10 tags by defualt
    Object[] tags,tags2; //arrays to hold tags to pass to adapters
    GridView tagGrid,tagGrid2; //gridviews to hold textviews
    static CustomAdapter customAdapter; //reference to adapter for added tags
    static CustomAdapter2 customAdapter2; //reference to adapter for suggested tags

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
        tagGrid = v.findViewById(R.id.tagGrid);
        tagGrid2 = v.findViewById(R.id.tagGrid2);

        customAdapter = new CustomAdapter();
        customAdapter2 = new CustomAdapter2();
        tags = new Object[0];
        tags2 = new Object[0];
        tagGrid.setAdapter(customAdapter);
        tagGrid2.setAdapter(customAdapter2);

        //get bundle from activity
        Bundle bundle = getArguments();

        //get path of photo from bundle
        assert bundle != null;
        String path = bundle.getString("photo");

        //display photo in image view
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        User userReference = User.getInstance();
        Photo photo = userReference.getPhoto(path);

        //on click listener for suggested tags to add suggested tags to added tags
        tagGrid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photo.addTag(tags2[position].toString());
                tags =photo.getTags().toArray();
                customAdapter.notifyDataSetChanged();
            }
        });

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

                    tags = photo.getTags().toArray();

                    //set custom adapter and on click listener if null to edit added tags
                    if(customAdapter == null){
                        customAdapter = new CustomAdapter();
                        tagGrid.setAdapter(customAdapter);
                        tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Log.d("HERE","here");
                                photo.removeTag(tags[position].toString());
                                tags =  photo.getTags().toArray();
                                customAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                    customAdapter.notifyDataSetChanged();
                }
                return handled;
            }
        });

        autoTags.clear();

        //get tags and set up custom adapter with on click listener to edit added tags if tags are not empty
        if (!photo.getTags().isEmpty()) {
            tags =  photo.getTags().toArray();
            customAdapter = new CustomAdapter();
            tagGrid.setAdapter(customAdapter);

            tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Debug", "position: " + position);
                    photo.removeTag(tags[position].toString());
                    tags =  photo.getTags().toArray();
                    customAdapter.notifyDataSetChanged();

                }
            });
        }

        //get and apply tags from ML Kit
        MLKitProcess.labelImage(photo);

        //make server request
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(shPref.getBoolean("serverTagSwitch", false)){
            //Do On Device server suggested tagging Here

        }

        return v;
    }

    /**
     * For adding a tag suggestestion from MLKit to the UI
     * @param tag: tag suggestion to be added to a list of them
     * Needs to be
     */
    public static void addSugTag(String tag, CustomAdapter customAdapter){
        Log.d("SinglePhotoView.addSugTag","Tag: " + tag);
        autoTags.add(tag);
        //mlkitTags.setText(autoTags.toString());
        customAdapter2.addItem(tag);
        customAdapter2.notifyDataSetChanged();
    }

    /**
     * For adding added tags to grid view
     */
    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() { return tags.length; }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.tag_item, null);

            TextView textView = view.findViewById(R.id.tags);
            textView.setText(tags[position].toString());

            return view;
        }
    }

    /**
     * For adding suggested tags to grid view
     */
    private class CustomAdapter2 extends BaseAdapter {

        @Override
        public int getCount() { return tags2.length; }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.tag_item_2, null);

            TextView textView = view.findViewById(R.id.tags2);
            textView.setText(tags2[position].toString());

            return view;
        }

        /**
         * To get and use tag strings from MLKitProcess within this fragment
         * @param tag
         */
        public void addItem(String tag){
            if(tags2 != null) {
                Object[] newTags = new Object[tags2.length + 1];
                for (int i = 0; i < tags2.length; i++) {
                    newTags[i] = tags2[i];
                }
                newTags[newTags.length - 1] = tag;
                tags2 = newTags;
            }else{
                tags2 = new Object[]{tag};
            }
        }
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
            Log.d("HERE","here3");


        }
    }
}



