package edu.temple.phototag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;


/**
 * Class to display user settings and to interact with them
 */
public class SettingsFragment extends Fragment {
    Context context;
    SharedPreferences preferences;

    //UI Variables
    Button signoutButton;
    //Interface Listener
    SettingsInterface interfaceListener;
    FragmentManager fm;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *
     * for creating views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);

        Switch serverSwitch = v.findViewById(R.id.serverSwitch); //get instance of server switch
        Switch autotagSwitch = v.findViewById(R.id.autoTaggingSwitch); //get instance of auto tag switch

        serverSwitch.setVisibility(View.GONE); //set server switch to invisible while auto tag switch is not checked

        autotagSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                //change server switch to visible if autotag switch checked
                if(isChecked){

                    serverSwitch.setVisibility(View.VISIBLE);
                    preferences.edit().putBoolean("autoTagSwitch", true).apply();
                }

                //make server switch invisible again if checked back and toggle server switch off
                if(!isChecked){
                    serverSwitch.setVisibility(View.GONE);
                    serverSwitch.setChecked(false);
                    preferences.edit().putBoolean("autoTagSwitch", false).apply();
                }
            }
        });
        //Signout button
        signoutButton = v.findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call signout method which is in main.
                interfaceListener.signOut();
            }
        });

        return v;
    }//end onCreateView()

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(context instanceof LoginFragment.LoginInterface){
            interfaceListener = (SettingsFragment.SettingsInterface)context;
        }else{
            throw new RuntimeException(context + "need to implement loginInterface");
        }
    }


    //Setting Interface
    public interface SettingsInterface {
        void signOut();
    }//end interface


}//end class