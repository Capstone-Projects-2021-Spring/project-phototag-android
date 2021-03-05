package edu.temple.phototag;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static final int  RC_SIGN_IN = 0; //for google sign in

    SignInButton googleButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    //tags
    final String TAG1 = "GOOGLE_SIGNIN" ;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment LoginFragment.
     */

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //****** Google Sign In BEGIN ******

        //Google Sign In Options, Followed--> (https://developers.google.com/identity/sign-in/android/sign-in)
        Log.d(TAG1, "GoogleSignInOptions starting.");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        Log.d(TAG1, "GoogleSignInOptions complete.");

        //Google Sign In Client, Followed--> (https://developers.google.com/identity/sign-in/android/sign-in)
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        //****** Google Sign In END ******
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }


    public interface LoginInterface {
        void successfulLogin();
    }//end interface


}//end class