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

    static float minConfidenceScore = 0.7f;
    static int rotation = 0;
    static String[] autoLabels = new String[10];

    static ImageLabelerOptions options = new ImageLabelerOptions.Builder()
            .setConfidenceThreshold(minConfidenceScore)
            .build();

    static ImageLabeler labeler = ImageLabeling.getClient(options);


    /**
     *
     * @param bitmap
     * @return String[] of possible labels for the image
     *
     * for preparing the bitmap image and labeler as well as using callbacks to send data to singlePhotoView
     */
    public static void labelBitmap(Bitmap bitmap){

        //prepare image
        InputImage inputImage = InputImage.fromBitmap(bitmap, rotation);

        //utilize callback interface to catch labels being returned by MLKit
        findLabels(inputImage, labeler, new LabelCallback() {
            @Override
            public void onCallback(String value) {
                SinglePhotoViewFragment.addTag(value);
            }
        });
    }

    /**
     *
     * @param bitmap
     * @param labeler
     *
     * for preparing
     */
    public static void autoLabelBitmap(Bitmap bitmap, ImageLabeler labeler){

        //prepare image
        InputImage inputImage = InputImage.fromBitmap(bitmap, rotation);

        //utilize callback interface to catch labels being returned by MLKit
        findLabels(inputImage, labeler, new LabelCallback() {
            @Override
            public void onCallback(String value) {
                autoAddLabel(value);
            }
        });
    }


    /**
     * @param inputImage
     * @param labeler
     * @param labelCallback
     *
     * Starts the processing tasks to return the image labels from MLKit
     */
    public static void findLabels(InputImage inputImage, ImageLabeler labeler, LabelCallback labelCallback){
        Task<List<ImageLabel>> result = labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() { //this asynchronus stuff is kicking my ass
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // For each label:get the text, send text to callback function
                        for(ImageLabel label : labels) {
                            String text = label.getText();
                            labelCallback.onCallback(text);
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
     * @return void : Callbacks are utilized to manage the asyncronous returns from MLKit
     *                  for image processing and label recognition so no return value is needed
     *      -Currently only supports bitmap
     */
    public static void labelImage(Bitmap bitmap){
        labelBitmap(bitmap);
    }


    /**
     *
     * @param bitmapArr
     *
     *      For automatically applying ML Kit suggested labels to a collection of images
     */
    public static void autoLabelImage(Bitmap[] bitmapArr){
        //only need to load the image labeler once

        for(int i = 0; i < bitmapArr.length; i++){
            autoLabelBitmap(bitmapArr[i], labeler);
        }
    }


    public static void autoLabelImage(Bitmap bitmap){
        //only need to load the image labeler once

        autoLabelBitmap(bitmap, labeler);
    }




    public static void autoAddLabel(String tag){
                //if tag is not in the database under this user for this photo
                //send the tag to the database under this photo for this user
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();// no clue what im doing
        //ref = ref.child("Users").child("Photos").child((Photo.id).toString()).child("Tags");//I think because of this information requirement
                                                                                            //this function should be in the Photo Class where
                                                                                            //the callback will call (refering to "Photo.id")
        //ref.setValue(tag);
    }



    //database stuff i dont wanna forget
    //DatabaseReference myRef = database.getReference("tag").childOf(User).childOf(Photo)
    //((DatabaseReference) myRef).setValue(text);


}
