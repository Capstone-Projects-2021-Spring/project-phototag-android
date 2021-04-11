package edu.temple.phototag;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.util.Log;
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
    SharedPreferences preferences;

    //UI Variables
    Button signoutButton;
    Button scheduleButton;
    //Interface Listener
    SettingsInterface interfaceListener;
    FragmentManager fm;

    /**
     * This fragment creates an interactable view that allows the user to turn some settings on and off
     * @param inflater generates the layout for the fragment
     * @param container is the group of views that hold the contents of the fragment
     * @param savedInstanceState bundle that holds data from the parent
     * @return view that holds the generated layout with the contents of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);

        Switch serverSwitch = v.findViewById(R.id.serverSwitch); //get instance of server switch
        Switch autotagSwitch = v.findViewById(R.id.autoTaggingSwitch); //get instance of auto tag switch

        //Start of Settings Retrieval and display
        //if on device auto tagging is turned on
        if(preferences.getBoolean("autoTagSwitch", false)){
            autotagSwitch.setChecked(true);
            serverSwitch.setVisibility(View.VISIBLE);
        }else{
            autotagSwitch.setChecked(false);
            serverSwitch.setVisibility(View.GONE);
        }
        //if server auto tagging is turned on
        if(preferences.getBoolean("serverTagSwitch", false)){
            serverSwitch.setChecked(true);
        }else{
            serverSwitch.setChecked(false);
        }
        //End of Settings Retrieval and display
        
        //if on device auto tagging is switched
        autotagSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //change server switch to visible if ondevice auto tagging is on
                if(isChecked){
                    serverSwitch.setVisibility(View.VISIBLE);
                    //save pref to have autoTag on
                    preferences.edit().putBoolean("autoTagSwitch", true).apply();
                    //perform on device auto tagging
                    MLKitProcess.autoLabelPhotos(User.getInstance().getAllPhotoObjects());
                }
                //hide server auto tagging switch if on device auto tagging is turned off
                if(!isChecked){
                    serverSwitch.setVisibility(View.GONE);
                    serverSwitch.setChecked(false);
                    //save pref to have autoTag off
                    preferences.edit().putBoolean("autoTagSwitch", false).apply();
                }
            }
        });

        //if server auto tagging is switched
        serverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //change server switch to visible if autotag switch checked
                if(isChecked){
                    serverSwitch.setVisibility(View.VISIBLE);
                    //save pref to have autoTag on
                    preferences.edit().putBoolean("serverTagSwitch", true).apply();
                    Thread thread = new Thread(() -> {
                        for (Photo photo : User.getInstance().getAllPhotoObjects()) {
                            MainActivity.connectServer(photo, User.getInstance().getUsername());
                        }
                    });
                    thread.start();
                }
                if(!isChecked){
                    //save pref to have autoTag off
                    preferences.edit().putBoolean("serverTagSwitch", false).apply();
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

        //Tag schedule button
        scheduleButton = v.findViewById(R.id.scheduleTags_Button);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call the load loadScheudleFragment() which is in main.
                interfaceListener.loadScheudleFragment();
            }
        });

        return v;
    }//end onCreateView()

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);//get context for preferences

        if(context instanceof LoginFragment.LoginInterface){
            interfaceListener = (SettingsFragment.SettingsInterface)context;
        }else{
            throw new RuntimeException(context + "need to implement loginInterface");
        }
    }


    //Setting Interface
    public interface SettingsInterface {
        void signOut();
        void loadScheudleFragment();
    }//end interface


}//end class