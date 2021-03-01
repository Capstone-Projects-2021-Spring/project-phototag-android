package edu.temple.phototag;

import android.content.Context;
import android.graphics.Bitmap;
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


public class GalleryViewFragment extends Fragment {

    GridView gridView;
    ArrayList<Bitmap> images;
    GalleryViewListener listener;
    CustomAdapter customAdapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof GalleryViewListener) {
            listener = (GalleryViewListener) context;
        } else {
            throw new RuntimeException("You must implement GalleryViewListener to attach this fragment");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gallery_view, container, false);

        gridView = v.findViewById(R.id.gridView);

        Bundle bundle = getArguments();

        assert bundle != null;
        images = bundle.getParcelableArrayList("array");

        customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);

        //Toast.makeText(getContext(), "here", Toast.LENGTH_LONG).show();


        return v;
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

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

            imageView.setImageBitmap(images.get(position));
            return view;
        }

    }

    public interface GalleryViewListener{

    }
}