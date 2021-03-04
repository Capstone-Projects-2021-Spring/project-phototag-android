package edu.temple.phototag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements GalleryViewFragment.GalleryViewListener {

    private static final int PERMISSION_REQUEST = 0; //request variable
    GalleryViewFragment galleryViewFragment; //initiate fragment
    LoginFragment loginViewFragment; //initiate fragment
    String[] arrPath; //initiate array of paths
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    //tags
    final String TAG1 = "GOOGLE_SIGNIN" ;

    /**
     *
     * @param savedInstanceState
     *
     * for creating the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //****** Google Sign In BEGIN ******

        //Google Sign In Options, Followed--> (https://developers.google.com/identity/sign-in/android/sign-in)
        Log.d(TAG1, "GoogleSignInOptions starting.");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        Log.d(TAG1, "GoogleSignInOptions complete.");

        //Google Sign In Client, Followed--> (https://developers.google.com/identity/sign-in/android/sign-in)
        Log.d(TAG1, "GoogleSignInClient starting.");
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.d(TAG1, "GoogleSignInClient complete.");

        //****** Google Sign In END ******

        //Get permission to device library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
        } else{

            //callback
        }

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();

        //Log.i("COUNT", "" + count);

        //Create an array to store path to all the images
        arrPath = new String[count];

        //loop through images on device and add paths to array
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i]= cursor.getString(dataColumnIndex);
            //Log.i("PATH", arrPath[i]);
        }
        cursor.close();

        FragmentManager fm = getSupportFragmentManager();

        loginViewFragment = (LoginFragment) fm.findFragmentById(R.id.gallery);
        galleryViewFragment = (GalleryViewFragment) fm.findFragmentById(R.id.gallery);

        //create login view if it doesn't exist.
        if(loginViewFragment == null) {
            fm.beginTransaction().add(R.id.gallery, LoginFragment.newInstance(mGoogleSignInClient, gso)).commit();
        }

        //create gallery view if it doesn't exist
        if(galleryViewFragment == null){
            fm.beginTransaction()
                    .add(R.id.gallery, GalleryViewFragment.newInstance(arrPath))
                    .commit();
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * for asking user permission to device storage. makes app synchronous.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    finish();
                }
        }//end switch
    }//end onRequestPermissionsResult()


}//end class