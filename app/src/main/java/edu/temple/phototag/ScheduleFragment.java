package edu.temple.phototag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class ScheduleFragment extends Fragment {

    //UI variables
    Button next1;
    Button next2;
    EditText scheduleName_Input;
    TextView scheduleName_Header;
    TextView startDate_Header;
    DatePicker datePicker1;

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
        startDate_Header = v.findViewById(R.id.startDate_Header);
        //Next button that shows under Schedule Name.
        next1 = v.findViewById(R.id.next1);
        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               hideScheduleNameDisplay();
               showStartDateDisplay();
            }
        });

        //Next button that shows under start date.
        next2 = v.findViewById(R.id.next2);
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideStartDateDisplay();
                showEndDateDisplay();
            }
        });

        return v;
    }

    // ***************** UI METHODS BEGIN *****************
    private void hideScheduleNameDisplay() {
        scheduleName_Header.setVisibility(View.GONE);
        scheduleName_Input.setVisibility(View.GONE);
        next1.setVisibility(View.GONE);
    }

    private void hideStartDateDisplay() {
        datePicker1.setVisibility(View.GONE);
        startDate_Header.setVisibility(View.GONE);
        next2.setVisibility(View.GONE);
    }

    private void showStartDateDisplay() {
        datePicker1.setVisibility(View.VISIBLE);
        startDate_Header.setVisibility(View.VISIBLE);
        next2.setVisibility(View.VISIBLE);
    }

    private void showEndDateDisplay() {

    }

    // ***************** UI METHODS END *****************


}//end class