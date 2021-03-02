package edu.temple.phototag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Class to display images in a gallery and interact with them
 */

public class GalleryViewFragment extends Fragment {

    GridView gridView; // instance of GridView
    String [] arrPath; // array of image paths
    GalleryViewListener listener; //listener for interface methods
    CustomAdapter customAdapter; //instance of adapter for gridview


    /**
     *
     * @param context
     *
     * for attaching fragment to activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof GalleryViewListener) {
            listener = (GalleryViewListener) context;
        } else {
            throw new RuntimeException("You must implement GalleryViewListener to attach this fragment");
        }

    }


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *
     * for creating the views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gallery_view, container, false);

        gridView = v.findViewById(R.id.gridView);

        //get arguments from activity
        Bundle bundle = getArguments();
        //get string array
        arrPath = bundle.getStringArray("array");

        //create instance of adapter
        customAdapter = new CustomAdapter();
        //set adapter to gridview
        gridView.setAdapter(customAdapter);


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
            Bitmap bitmap = BitmapFactory.decodeFile(arrPath[position],options);
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            imageView.setImageBitmap(bitmap);

            return view;
        }

    }

    /**
     * for interacting with an activity
     */
    public interface GalleryViewListener{

    }
}