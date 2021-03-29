package edu.temple.phototag;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
import java.util.Arrays;
import java.util.List;

public class MLKitProcess {

    static float minConfidenceScore = 0.7f;
    static ImageLabelerOptions options = new ImageLabelerOptions.Builder()
            .setConfidenceThreshold(minConfidenceScore)
            .build();

    static ImageLabeler labeler = ImageLabeling.getClient(options);

    /**
     *
     * @param bitmap
     * @return void : uses callbacks to send data to singlePhotoView
     *
     * for preparing the bitmap image and labeler
     * rotating and labeling the image in all 4 rotation orientations
     * and displays suggested labels in singlePhotoView
     */
    private static void labelBitmap(Bitmap bitmap){
        for(int r = 0; r < 360; r+=90) {
            //prepare image
            InputImage inputImage = InputImage.fromBitmap(bitmap, r);

            //utilize callback interface to catch labels being returned by MLKit
            findLabels(inputImage, labeler, new LabelCallback() {
                @Override
                public void onCallback(String value) {
                    //create string array to hold tags
                    String[] tagArr = SinglePhotoViewFragment.autoTags;

                    //for each item in the string array
                    for (int i = 0; i < tagArr.length; i++) {
                        if (tagArr[i] == null) {
                            tagArr[i] = value;
                            //trim null values
                            String[] out = Arrays.copyOfRange(tagArr, 0, i + 1);
                            String tags = String.join(",", (out));

                            //display the updated array of tags
                            SinglePhotoViewFragment.sugTag.setText(tags);
                            break;
                        } else {
                            if (tagArr[i].equals(value)) {
                                //tag in suggestions already
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * @param photo
     * @param path
     * @param labeler
     * @return void : uses callbacks to add suggested label when received
     *
     * for preparing the input image
     * finding the labels in the image
     * when those labels return, they are applied to that photo object
     * given that the suggested label is not already a tag for the photo
     * the photo in the database is updated to reflect that it has been auto tagged
     */
    private static void autoLabelBitmap(Photo photo, String path, ImageLabeler labeler){
        //prepare image
        InputImage inputImage = InputImage.fromBitmap(BitmapFactory.decodeFile(path), 0);

        //utilize callback interface to catch labels being returned by MLKit
        findLabels(inputImage, labeler, new LabelCallback() {
            @Override
            public void onCallback(String value) {
                //if the tag is not already applied to the photo
                if (! photo.getTags().contains(value)) {
                    //apply the tag
                    photo.addTag(value);
                }
            }
        });

        //set the flag for auto-tagged to true for the photo object stored in the DB
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference()
                .child("Android")
                .child(User.getInstance().getEmail())
                .child("Photos")
                .child(photo.id)
                .child("AutoTagged");

        myRef.setValue(true);
    }

    /**
     * @param inputImage
     * @param labeler
     * @param labelCallback
     * @return void
     *
     * Starts the processing tasks to return the image label suggestions from MLKit
     */
    private static void findLabels(InputImage inputImage, ImageLabeler labeler, LabelCallback labelCallback){
        Task<List<ImageLabel>> result = labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
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
                        Log.d("MLKit-findLabels","Failed to get labels from MLKit: "+e.getMessage());
                    }
                });
    }


    /**
     *
     * @param photo
     * @return void
     *
     *      Used for SinglePhotoViewFragment
     *
     *      For getting labels for a photo being displayed in single photo view
     *      uses callbacks to apply the label suggestions as they are recieved
     */
    public static void labelImage(Photo photo){
        labelBitmap(BitmapFactory.decodeFile(photo.path));
    }



    /**
     *
     * @param photos
     *
     *      Used for onDevice Auto Tagging in MainActivity and SettingsFragmnent
     *
     *      For auto labeling an array of photo objects with MLKit suggested labels
     */
    public static void autoLabelPhotos(Photo[] photos){
        for(int i = 0; i < photos.length; i++){
            autoLabelBitmap(photos[i], photos[i].path, labeler);
        }
    }
}
