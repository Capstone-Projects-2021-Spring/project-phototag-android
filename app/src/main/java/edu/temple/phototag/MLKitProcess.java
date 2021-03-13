package edu.temple.phototag;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
                //SinglePhotoViewFragment.addTagSuggestion(value);
                String[] temp = SinglePhotoViewFragment.autoTags;

                for(int i = 0; i < temp.length ; i++){
                    if(temp[i] == null){
                        temp[i] = value;
                        //trim null values
                        String[] out = Arrays.copyOfRange(temp, 0, i+1);
                        String tags = String.join("," , (out));

                        //update textview with tags
                        SinglePhotoViewFragment.textView.setText(tags);
                        break;
                    }
                }
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
    public static void autoLabelBitmap(String path, ImageLabeler labeler){

        //prepare image
        InputImage inputImage = InputImage.fromBitmap(BitmapFactory.decodeFile(path), rotation);

        //utilize callback interface to catch labels being returned by MLKit
        findLabels(inputImage, labeler, new LabelCallback() {
            @Override
            public void onCallback(String value) {
                //this? photo.addTag(value)
                autoAddLabel(value, path);
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
    public static void labelImage(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        labelBitmap(bitmap);
    }

    public static void labelImage(Bitmap bitmap){
        labelBitmap(bitmap);
    }

    /**
     *
     * @param bitmapArr
     *
     *      For automatically applying ML Kit suggested labels to a collection of images
     */
    public static void autoLabelImage(String[] pathArr){
        //Bitmap bitmap = BitmapFactory.decodeFile(pathArr);
        //labelBitmap(bitmap);

        for(int i = 0; i < pathArr.length; i++){
            autoLabelBitmap(pathArr[i], labeler);
        }
    }


    /*
    public static void autoLabelImage(Photo[] photos){
        for(int i = 0; i < photos.length; i++) {
            autoLabelBitmap(photos[i].getImage(), labeler);
        }
    }
    */



    public static void autoAddLabel(String tag, String path){
                //if tag is not in the database under this user for this photo
                //send the tag to the database under this photo for this user
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();// no clue what im doing
        //ref = ref.child("Users").child(User.id).child("Photos").child(Photo.id).child("photo_tags");//I think because of this information requirement
                                                                                            //this function should be in the Photo Class where
                                                                                            //the callback will call (refering to "Photo.id")
        //String setTags = ref.getValue();
        //check if the tag is already in the set tags
            //if the tag is not already there then record it
                // String tags = setTags + tag
                //ref.setValue(tags);
            //if the tag is already in photo_tags then skip it
    }



    //database stuff i dont wanna forget
    //DatabaseReference myRef = database.getReference("tag").childOf(User).childOf(Photo)
    //((DatabaseReference) myRef).setValue(text);


}
