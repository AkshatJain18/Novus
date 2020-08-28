package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DateWiseAttendanceRecords extends AppCompatActivity {

    Spinner TimeSpinner;
    EditText DateEditText;
    TextView RecordFoundTextView;
    ListView AttendanceListView;
    ProgressDialog progressDialog;
    AlertDialog.Builder alert;
    ArrayAdapter<AttendanceData> arrayAdapter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    ArrayList<AttendanceData> attendenceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_wise_attendance_records);

        IntialiseViews();


        addItemsOnSpinner2();
    }
    public void addItemsOnSpinner2() {
        progressDialog.setMessage("Fetching Meetings!");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(false);
        final List<String> list = new ArrayList<String>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("meetings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String Meeeting=dataSnapshot1.child("Meeting Name").getValue().toString();
                    list.add(Meeeting);
                }
                progressDialog.dismiss();
                Collections.reverse(list);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                TimeSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void IntialiseViews()
    {
        TimeSpinner=(Spinner)findViewById(R.id.TimeSpinner);
        DateEditText=(EditText)findViewById(R.id.DateEditText);
        AttendanceListView=(ListView)findViewById(R.id.AttendanceListView);
        progressDialog=new ProgressDialog(DateWiseAttendanceRecords.this);
        alert=new AlertDialog.Builder(DateWiseAttendanceRecords.this);
        RecordFoundTextView=(TextView)findViewById(R.id.RecordFoundTextView);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
                DateEditText.setText(date);
            }
        };
        attendenceList=new ArrayList<>();
        arrayAdapter=new ArrayAdapter(this,R.layout.date_wise_attendance_list_item,R.id.MemberName,attendenceList)
        {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                TextView MemberName=(TextView)view.findViewById(R.id.MemberName);

                TextView AttendanceStatus=(TextView)view.findViewById(R.id.AttendanceStatus);

                MemberName.setText(attendenceList.get(position).name);

                AttendanceStatus.setText(attendenceList.get(position).AttendanceStatus);

                return view;
            }
        };
        AttendanceListView.setAdapter(arrayAdapter);
    }
    public void DisplayAttendanceList(View view)
    {
        attendenceList.clear();
        String date=DateEditText.getText().toString();
        String Time=String.valueOf(TimeSpinner.getSelectedItem());
        if(date.equals("") || Time.equals(""))
        {
            alert.setMessage("Please fill all the details");
            alert.setPositiveButton("Ok",null);
            alert.show();
        }
        else {
            progressDialog.setMessage("Fetching Record!");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance Records");
            databaseReference = databaseReference.child(date).child(Time);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String name = dataSnapshot1.child("name").getValue().toString();
                        String AttendanceStatus = dataSnapshot1.child("status").getValue().toString();
                        String Uid = dataSnapshot1.getKey();
                        attendenceList.add(new AttendanceData(name, Uid, AttendanceStatus,"NA","NA"));
                    }
                     if (attendenceList != null) {
                        RecordFoundTextView.setVisibility(View.INVISIBLE);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    AttendanceListView.setAdapter(arrayAdapter);
                    AttendanceListView.requestFocus();
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
    public void onDateClick(View view)
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                DateWiseAttendanceRecords.this,
                android.R.style.Widget_DatePicker,
                mDateSetListener,
                year,month,day);
        dialog.setTitle("Select Date");
        dialog.show();

    }
}

