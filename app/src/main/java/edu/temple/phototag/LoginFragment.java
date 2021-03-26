package edu.temple.phototag;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    //UI variables
    SignInButton signInButton;
    //Button signoutButton;
    //Google Client & firebase auth variables
    private FirebaseAuth mAuth;
    private static final int  RC_SIGN_IN = 0; //for google sign in
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    LoginInterface interfaceListener;
    boolean signedIn;
    //Tags
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

    //******************** ACTIVITY LIFECYCLE METHODS SECTION BEGIN ********************
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase authentication instance.
        mAuth = FirebaseAuth.getInstance();
        //****** Google Sign In BEGIN ******

        //Google Sign In Options, Followed--> (https://developers.google.com/identity/sign-in/android/sign-in)
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Google Sign In Client, Followed--> (https://developers.google.com/identity/sign-in/android/sign-in)
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //****** Google Sign In END ******
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check for existing user
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    //******************** ACTIVITY LIFECYCLE METHODS SECTION END ********************


    //******************** GENERAL ACTIVITY/FRAGMENT METHODS SECTION BEGIN ********************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //Set up signInButton dimensions
        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        //Button on click listener for signing in
        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        //Sign out button
        /*
        signoutButton = view.findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    // ...
                    case R.id.signout_button:
                        signOut();
                        break;
                    // ...
                }
            }
        }); */

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof LoginInterface){
            interfaceListener = (LoginInterface)context;
        }else{
            throw new RuntimeException(context + "need to implement loginInterface");
        }
    }

    //******************** GENERAL ACTIVITY/FRAGMENT METHODS SECTION ENDS ********************

    //******************** GOOGLE API LOGIN/LOGOUT METHODS SECTION BEGIN ********************

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AUTH", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTH", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });
    }

    /**
     * This method is called in the onActivityResult() method when a signIn is requested. The
     * method pattern is as follows : sign_in_button.onClick() --> signIn() --> onActivityResult() --> handleSignInResult()
     * @param completedTask Task<GoogleSignInAccount></GoogleSignInAccount>
     * @return void
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (acct != null) {
                Log.d(TAG1, "information acquired");

                //add the retrieved values from Google account to local User class
                User userReference = User.getInstance();
                userReference.setUsername(acct.getDisplayName());
                userReference.setEmail(acct.getEmail());

                // Signed in successfully, show authenticated UI.
                signedIn = true;
                //Attempt to authenticate w/firebase
                try {
                    firebaseAuthWithGoogle(account.getIdToken());
                    Log.d("AUTH", "Authentication w/ Firebase was successful.");
                }catch (Exception e) {
                    Log.d("AUTH", "Firebase authentication failed. Check error log.");
                    Log.e("AUTH", e.getMessage());
                }
                interfaceListener.loadGalleryFragment(mGoogleSignInClient);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG1, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /*private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        signedIn = false;

                    }
                });
    }*/
    //******************** GOOGLE API LOGIN/LOGOUT METHODS SECTION END ********************


    public interface LoginInterface {
        void loadGalleryFragment(GoogleSignInClient mGoogleSignInClient);
    }//end interface


}//end class