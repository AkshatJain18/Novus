package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddEvent extends AppCompatActivity {

    TextView EventNameHeading,EventDateHeading,EventTimeHeading,VenueHeading;
    EditText StartDate,Time,EventName,VenueEditText;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
     TimePickerDialog.OnTimeSetListener timeSetListener;
    AlertDialog.Builder alertDialog;
    String EventId;
    String EventName1,Date1,Time1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        EventNameHeading=(TextView)findViewById(R.id.EventNameHeading);
        EventDateHeading=(TextView)findViewById(R.id.EventDateHeading);
        EventTimeHeading=(TextView)findViewById(R.id.EventTimeHeading);
        VenueHeading=(TextView)findViewById(R.id.meetingVenueHeading);
        StartDate=(EditText)findViewById(R.id.FromDateEditText);
        Time=(EditText)findViewById(R.id.TimeSpinner);
        EventName=(EditText)findViewById(R.id.EventNameEditText);
        VenueEditText=(EditText)findViewById(R.id.VenueEditText);
        Typeface t=Typeface.createFromAsset(getAssets(),"Caviar_Dreams_Bold.ttf");
        EventNameHeading.setTypeface(t);
        EventTimeHeading.setTypeface(t);
        EventDateHeading.setTypeface(t);
        VenueHeading.setTypeface(t);
        alertDialog=new AlertDialog.Builder(this);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                StartDate.setText(date);
            }
        };

     timeSetListener=new TimePickerDialog.OnTimeSetListener() {
         @Override
         public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
             String Time1=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
             Time.setText(Time1);
         }
     };

    }

    public void onDateClick(View view)
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AddEvent.this,
                android.R.style.Widget_DatePicker,
                mDateSetListener,
                year,month,day);
        dialog.setTitle("Select Date");
        dialog.show();

    }

    public void onTimeClick(View view)
    {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddEvent.this,android.R.style.Widget_Material_Light_TimePicker,timeSetListener,hour,minute,false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
    boolean flag=false;
    String Venue;
    public void SaveEvent(View view) {


        //ProgressDialog progressDialog = new ProgressDialog(this);

        Venue=VenueEditText.getText().toString();
        EventName1 = EventName.getText().toString();

        Time1 = Time.getText().toString();

        Date1 = StartDate.getText().toString();

        if (EventName1.equals("") || Time1.equals("") || Date1.equals("") || Venue.equals("")) {
            alertDialog.setTitle("Empty fields");
            alertDialog.setMessage("Please fill all the details");
            alertDialog.show();
        } else {
            Confirm();
        }
    }

    public void SaveData() {

        final ProgressDialog progressDialog=new ProgressDialog(AddEvent.this);
        progressDialog.setMessage("Adding Event!");
        progressDialog.setCancelable(false);
        progressDialog.show();




        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        String id1=databaseReference.push().getKey();
        databaseReference.child(id1).child("Event Name").setValue(EventName1);
        databaseReference.child(id1).child("Event Date").setValue(Date1);
        databaseReference.child(id1).child("Event Venue").setValue(Venue);
        databaseReference.child(id1).child("Event Time").setValue(Time1+" "+"onwards");
        //


        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 5000);
        progressDialog.dismiss();

        showAlert();

     }

     public void showAlert()
     {
         AlertDialog.Builder alertDialog1=new AlertDialog.Builder(AddEvent.this);
         alertDialog1.setMessage("Event Added Succesfully!");
         alertDialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 Intent intent=new Intent(getApplicationContext(),Events.class);
                 startActivity(intent);
             }
         });
         alertDialog1.show();
     }

     void Confirm()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveData();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }
}
