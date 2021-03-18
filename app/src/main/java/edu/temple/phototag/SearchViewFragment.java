package edu.temple.phototag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Class to display the search results in gallery
 */
public class SearchViewFragment extends Fragment {

    GridView gridView; // instance of GridView
    String [] arrPath; // array of image paths
    SearchViewListener listener; //listener for interface methods
    CustomAdapter customAdapter; //instance of adapter for searchview


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param arrayPath The array of paths pointing to user's photos
     * @return A new instance of GalleryViewFragment, with the bundle already set.
     */
    public static GalleryViewFragment newInstance(String[] arrayPath) {
        GalleryViewFragment galleryViewFragment = new GalleryViewFragment();
        Bundle bundle = new Bundle();
        // bundle.putParcelableArrayList("array",images);
        bundle.putStringArray("array", arrayPath);
        galleryViewFragment.setArguments(bundle);

        return galleryViewFragment;
    }


    /**
     *
     * @param context
     *
     * for attaching fragment to activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SearchViewListener) {
            listener = (SearchViewFragment.SearchViewListener) context;
        } else {
            throw new RuntimeException("You must implement SearchViewListener to attach this fragment");
        }

    }


    /**
     *
     * @param inflater The LayoutInflater object that can be used
     *                to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's
     *                  UI should be attached to.  The fragment should not add the view itself,
     *                  but this can be used to generate the LayoutParams of the view
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The view created by the inflater
     *
     * Creates and returns the view hierarchy associated with the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search_view, container, false);

        gridView = v.findViewById(R.id.gridView2);

        //get arguments from activity
        Bundle bundle = getArguments();
        //get string array
        arrPath = bundle.getStringArray("search");


        //create instance of adapter
        customAdapter = new CustomAdapter();
        //set adapter to gridview
        gridView.setAdapter(customAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    listener.viewPhoto2(position);



            }
        });



        return v;
    }

    /**
     * for adding images to GridView
     */
    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() { return arrPath.length; }

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
            View view = getLayoutInflater().inflate(R.layout.grid_item, null);

            ImageView imageView = view.findViewById(R.id.image);


            //compress and display bitmap images from path
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = 200;
            options.outHeight = 200;
            Bitmap bitmap = BitmapFactory.decodeFile(arrPath[position], options);
            if(bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            imageView.setImageBitmap(bitmap);
            }

            return view;
        }

    }

    /**
     * for interacting with an activity
     */
    public interface SearchViewListener{

        void viewPhoto2(int position);

    }
}