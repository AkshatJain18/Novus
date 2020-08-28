package com.org.novus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GiveFeedback extends AppCompatActivity {

    EditText NameEditText,FeedbackEditText;
     String name,feedback;
     AlertDialog.Builder alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_feedback);
        NameEditText=(EditText)findViewById(R.id.NameEditText);
        FeedbackEditText=(EditText)findViewById(R.id.FeedbackEditText);
        alert=new AlertDialog.Builder(this);
        alert.setPositiveButton("Ok",null);
    }

    public void SubmitFeedback(View view)
    {
        name=NameEditText.getText().toString();
        feedback=FeedbackEditText.getText().toString();
        if(name.equals("")||feedback.equals(""))
        {
         alert.setMessage("Please fill all the details!");
         alert.show();
        }
        else if(feedback.length()<40)
        {
            FeedbackEditText.setError("Please enter at least 40 characters");
        }
        else {
            String Uid = FirebaseAuth.getInstance().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            String pushKey=databaseReference.push().getKey();
            databaseReference.child("feedbacks").child(Uid).child(pushKey).child("name").setValue(name);
            databaseReference.child("feedbacks").child(Uid).child(pushKey).child("feedback").setValue(feedback);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
            String currentTime = sdf.format(c.getTime());
            databaseReference.child("feedbacks").child(Uid).child(pushKey).child("time").setValue(String.valueOf(currentTime));
           // databaseReference.child("feedbacks").child(Uid).child(pushKey).child("feedback").setValue(feedback);
            alert.setMessage("Thanks for your feedback!");
            //alert.show();
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AlertDialog.Builder alert1=new AlertDialog.Builder(GiveFeedback.this);
                    alert1.setMessage("Do you want to give one more feedback?");
                    alert1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NameEditText.setText("");
                            FeedbackEditText.setText("");
                        }
                    });
                    alert1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(getApplicationContext(),LandingPage.class);
                            startActivity(intent);
                        }
                    });
                    alert1.show();
                }
            });
            alert.show();
        }
    }
}
