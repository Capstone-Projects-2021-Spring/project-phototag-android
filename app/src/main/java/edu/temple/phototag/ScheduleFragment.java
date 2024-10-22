package edu.temple.phototag;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment {

    //Interface
    ScheduleInterface interfaceListener;
    //UI variables
    Button next1;
    Button next2;
    Button next3;
    Button confirmSched_Btn;
    EditText scheduleName_Input;
    EditText scheduleTags_text;
    TextView endDate_Confirm;
    TextView startDate_Confirm;
    TextView scheduleNameLabel;
    TextView realSchedule_Text;
    TextView realstartdate_text;
    TextView realenddate_text;
    TextView scheduleName_Header;
    TextView startDate_Header;
    TextView uniqueScheduleText;
    TextView endDate_Header;
    DatePicker datePicker1;
    DatePicker datePicker2;

    //Schedule Name
    String scheduleName_data;
    //Data for start date and end date
    Calendar cal;
    int monthData;
    int dayData;
    int yearData;
    long startEpochTime;
    long endEpochTime;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal = Calendar.getInstance();

    }//end onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_schedule, container, false);
        //Set up UI variables.
        scheduleName_Header = v.findViewById(R.id.scheduleName_Header);
        scheduleName_Input = v.findViewById(R.id.scheduleName_Input);
        datePicker1 = v.findViewById(R.id.datePicker1);
        datePicker2 = v.findViewById(R.id.datePicker2);
        startDate_Header = v.findViewById(R.id.startDate_Header);
        endDate_Header = v.findViewById(R.id.endDate_Header);
        scheduleTags_text = v.findViewById(R.id.scheduleTags_text);
        endDate_Confirm = v.findViewById(R.id.endDate_Confirm);
        startDate_Confirm = v.findViewById(R.id.startDate_Confirm);
        scheduleNameLabel = v.findViewById(R.id.scheduleNameLabel);
        realSchedule_Text = v.findViewById(R.id.realSchedule_Text);
        realstartdate_text = v.findViewById(R.id.realstartdate_text);
        realenddate_text = v.findViewById(R.id.realenddate_text);
        uniqueScheduleText = v.findViewById(R.id.uniqueSchedule_Text);
        //Next button that shows under Schedule Name.
        next1 = v.findViewById(R.id.next1);
        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the schedule name input.
                scheduleName_data = scheduleName_Input.getText().toString();
                realSchedule_Text.setText(scheduleName_data);
                hideScheduleNameDisplay();
                showStartDateDisplay();

            }
        });

        //Next button that shows under start date.
        next2 = v.findViewById(R.id.next2);
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the startDate input.
                monthData = datePicker1.getMonth();
                dayData = datePicker1.getDayOfMonth();
                yearData = datePicker1.getYear();
                cal.set(yearData, monthData, dayData, 0,0,0);
                startEpochTime = cal.getTimeInMillis()/1000;
                realstartdate_text.setText(String.valueOf(monthData+1) + "/" + String.valueOf(dayData) + "/" + String.valueOf(yearData));
                hideStartDateDisplay();
                showEndDateDisplay();
            }
        });

        //Next button that shows under end date.
        next3 = v.findViewById(R.id.next3);
        next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the endDate input
                monthData = datePicker2.getMonth();
                dayData = datePicker2.getDayOfMonth();
                yearData = datePicker2.getYear();
                cal.set(yearData, monthData, dayData, 23, 59 ,59);
                endEpochTime = cal.getTimeInMillis()/1000;
                realenddate_text.setText(String.valueOf(monthData+1) + "/" + String.valueOf(dayData) + "/" + String.valueOf(yearData));
                hideEndDateDisplay();
                showConfirmDisplay();
            }
        });

        //Confirm Schedule button.
        confirmSched_Btn = v.findViewById(R.id.confirmSched_Btn);
        confirmSched_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This will create the dataset in DB.(actual code for creation is in Main)
                List<String> tags = new ArrayList<>();
                //Format the list.
                tags = Arrays.asList(scheduleTags_text.getText().toString().split(","));
                interfaceListener.saveSchedule(scheduleName_data, startEpochTime, endEpochTime, tags);
                interfaceListener.checkSchedules();
                Toast toast = Toast.makeText(getContext(), "Schedule Saved.", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        return v;
    }//end OnCreateView()

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //Set up interface.
        if(context instanceof LoginFragment.LoginInterface){
            interfaceListener = (ScheduleFragment.ScheduleInterface)context;
        }else{
            throw new RuntimeException(context + "need to implement loginInterface");
        }
    }

    // ***************** UI METHODS BEGIN *****************
    private void hideScheduleNameDisplay() {
        scheduleName_Header.setVisibility(View.GONE);
        scheduleName_Input.setVisibility(View.GONE);
        next1.setVisibility(View.GONE);
        uniqueScheduleText.setText("");
        uniqueScheduleText.setVisibility(View.GONE);
    }

    private void hideStartDateDisplay() {
        datePicker1.setVisibility(View.GONE);
        startDate_Header.setVisibility(View.GONE);
        next2.setVisibility(View.GONE);
    }

    private void hideEndDateDisplay() {
        datePicker2.setVisibility(View.GONE);
        endDate_Header.setVisibility(View.GONE);
        next3.setVisibility(View.GONE);
    }

    private void showStartDateDisplay() {
        datePicker1.setVisibility(View.VISIBLE);
        startDate_Header.setVisibility(View.VISIBLE);
        next2.setVisibility(View.VISIBLE);
    }

    private void showEndDateDisplay() {
        datePicker2.setVisibility(View.VISIBLE);
        endDate_Header.setVisibility(View.VISIBLE);
        next3.setVisibility(View.VISIBLE);
    }

    private void showConfirmDisplay() {
        scheduleNameLabel.setVisibility(View.VISIBLE);
        realSchedule_Text.setVisibility(View.VISIBLE);
        startDate_Confirm.setVisibility(View.VISIBLE);
        endDate_Confirm.setVisibility(View.VISIBLE);
        realstartdate_text.setVisibility(View.VISIBLE);
        realenddate_text.setVisibility(View.VISIBLE);
        scheduleTags_text.setVisibility(View.VISIBLE);
        confirmSched_Btn.setVisibility(View.VISIBLE);
    }
    // ***************** UI METHODS END *****************

    public interface ScheduleInterface {
        void saveSchedule(String s, long startD, long endD, List<String> tags);
        void checkSchedules();
        //boolean checkUniqueSchedule(String s);
    }

}//end class