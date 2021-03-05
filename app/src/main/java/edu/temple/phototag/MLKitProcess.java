package edu.temple.phototag;

import android.graphics.Bitmap;
import android.media.Image;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MLKitProcess {

    //min confidence score is a placeholder, if im understanding the diagram correctly that is something that will get passed into
    //the class so I need to figure how to obtain and use that
    static float minConfidenceScore = 0.7f;
    static int LabelArraySize =  10;
    static String[] autoLabels = new String[LabelArraySize];
    static int rotation = 0;


    /**
     *
     * @param bitmap
     * @return String[] of possible labels for the image
     *
     * for bitmap image processing and label recognition
     */
    public static void labelBitmap(Bitmap bitmap){

        //prepare image
        InputImage inputImage = InputImage.fromBitmap(bitmap, rotation);

        //prepare labeler: set custom confidence threshold
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(minConfidenceScore)
                .build();

        ImageLabeler labeler = ImageLabeling.getClient(options);

        findLabels(inputImage, labeler, new LabelCallback() {
            @Override
            public void onCallback(String value) {
                SinglePhotoViewFragment.addLabel(value);
            }
        });
    }


    /**
     * @param inputImage
     * @param labeler
     * @param labelCallback
     *
     * Starts the processing tasks to return the image labels
     */
    public static void findLabels(InputImage inputImage, ImageLabeler labeler, LabelCallback labelCallback){
        Task<List<ImageLabel>> result = labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() { //this asynchronus stuff is kicking my ass
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        for(ImageLabel label : labels) {
                            String text = label.getText();
                            //addLabel(text);
                            labelCallback.onCallback(text);
                            //addLabel(text);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
    }



    /**
     *
     * @param bitmap
     * @return String[] : possible labels for the image
     * for image processing and label recognition
     *      -Currently only supports bitmap
     */
    public static String[] labelImage(Bitmap bitmap){
        Arrays.fill(autoLabels, null);// clear array for new labels to be returned
        labelBitmap(bitmap);
        return autoLabels;
    }




    //an idea of an auto label image function that updates the firebase database directly
    private void AutoLabelBitmap(Bitmap bitmap){

        //since the photo is being grabbed after orientation has been taken care of by the phone itself
        //the rotation argument can be set to 0
        int rotation = 0;

        //prepare image
        InputImage inputImage = InputImage.fromBitmap(bitmap, rotation);

        //prepare database communication
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //prepare labeler: set custom confidence threshold
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(minConfidenceScore)
                .build();

        ImageLabeler labeler = ImageLabeling.getClient(options);

        //check for labels
        Task<List<ImageLabel>> result = labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // record labels: There is a chance that no labels will be written to the
                        // autolabels array in the case that no labels were identified with high enough confidence
                        for(ImageLabel label : labels) {
                            String text = label.getText();
                            //autoLabels[autoLabels.length] = text;
                            DatabaseReference myRef = database.getReference("imagelabels"); //No clue what this path is suppose to look like
                            ((DatabaseReference) myRef).setValue(text);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

        //return autoLabels;
    }


}
