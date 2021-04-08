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
    Object[] tags,tags2;
    GridView tagGrid,tagGrid2;
    static CustomAdapter customAdapter;
    static CustomAdapter2 customAdapter2;

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
        tagGrid = v.findViewById(R.id.tagGrid); //instance of grid for added tags
        tagGrid2 = v.findViewById(R.id.tagGrid2); //instance of grid for suggested tags

        //initialize objects
        customAdapter = new CustomAdapter();
        customAdapter2 = new CustomAdapter2();
        tags = new Object[0];
        tags2 = new Object[0];

        //set adapters to grids
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

        //handle removing tags with editor action
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

        //clear autotag arraylist
        autoTags.clear();

        //handle removing suggested tags when get tags starts off with tags
        if (!photo.getTags().isEmpty()) {
           // ((TextView) v.findViewById(R.id.tags)).setText(photo.getTags().toString());
            tags =  photo.getTags().toArray();
            customAdapter = new CustomAdapter();
            tagGrid.setAdapter(customAdapter);

            tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Log.d("HERE","here");
                    Log.d("Debug", "position: " + position);
                    photo.removeTag(tags[position].toString());
                    tags =  photo.getTags().toArray();
                    customAdapter.notifyDataSetChanged();

                }
            });
        }

        //get and apply tags from ML Kit
        MLKitProcess.labelImage(photo);

        //handle all cases of adding suggested tag
        tagGrid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photo.addTag(tags2[position].toString());
                tags =photo.getTags().toArray();
                customAdapter.notifyDataSetChanged();
            }
        });

        //handle removing tag outside of other cases
        tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Log.d("HERE","here");
                Log.d("Debug", "position: " + position);
                photo.removeTag(tags[position].toString());
                tags =  photo.getTags().toArray();
                customAdapter.notifyDataSetChanged();

            }
        });

        //make server request
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(shPref.getBoolean("serverTagSwitch", false)){
            //Do On Device server suggested tagging Here
            

            //serverTags.setText();
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
     * Adds added tags to gridview
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

        public void addItem(String tag){
            if(tags != null) {
                Object[] newTags = new Object[tags.length + 1];
                for (int i = 0; i < tags.length; i++) {
                    newTags[i] = tags[i];
                }
                newTags[newTags.length - 1] = tag;
                tags = newTags;
            }else{
                tags = new Object[]{tag};
            }
            //Log.d("SinglePhotoView.CustomAdapter.addItem","Tags: " + tags[0].toString());
        }
    }

    /**
     * Adds suggested tags to gridview
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

        //necessary to get tags from MLKit into SingleViewFragment
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
            //Log.d("SinglePhotoView.CustomAdapter.addItem","Tags: " + tags[0].toString());
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



