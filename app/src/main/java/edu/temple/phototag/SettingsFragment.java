package edu.temple.phototag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;


/**
 * Class to display user settings and to interact with them
 */
public class SettingsFragment extends Fragment {
    Context context;
    SharedPreferences preferences;
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


        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }



}