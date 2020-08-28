package com.org.novus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SubmitIdeasActivity extends AppCompatActivity {

    EditText IdeaContent,IdeaTitle;
    String name;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_ideas);
        Intent intent=getIntent();
        name=intent.getStringExtra("name");


        IdeaContent=(EditText)findViewById(R.id.IdeaContent);
        IdeaTitle=(EditText)findViewById(R.id.IdeaTitle);
        progressDialog=new ProgressDialog(SubmitIdeasActivity.this);
        alertDialog=new AlertDialog.Builder(SubmitIdeasActivity.this);
    }

    int count=0;
    public void Validate(View view)
    {

                if(IdeaTitle.getText().toString().equals("") || IdeaContent.getText().toString().equals(""))
                {
                    alertDialog.setMessage("Please fill all the fields");
                    alertDialog.setPositiveButton("Ok",null);
                    alertDialog.show();
                }
                else if(IdeaTitle.getText().toString().length()<20)
                {
                    IdeaTitle.setError("Please enter at least 20 characters");
                }
                else if(IdeaContent.getText().toString().length()<50)
                {
                    IdeaContent.setError("Please enter at least 50 characters");
                }
                else
                {
                    RetreiveName();
                }




    }

    public void RetreiveName()
    {
        final String uid=FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference=databaseReference.child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                SubmitIdea();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void SubmitIdea()
    {
        progressDialog.setMessage("Submitting your Idea!");
        progressDialog.show();
        String IdeaContentdata=IdeaContent.getText().toString();
        String IdeaTitledata=IdeaTitle.getText().toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String Uid=FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String id=databaseReference.push().getKey();

        databaseReference.child("ideas").child(Uid).child(id).child("Idea Title").setValue(IdeaTitledata);
        databaseReference.child("ideas").child(Uid).child(id).child("Idea Description").setValue(IdeaContentdata);
        databaseReference.child("ideas").child(Uid).child(id).child("Idea given by").setValue(name);
        databaseReference.child("ideas").child(Uid).child(id).child("Time").setValue(formattedDate);
        databaseReference.child("ideas").child(Uid).child(id).child("Top").setValue("false");
        databaseReference.child("ideas").child(Uid).child(id).child("Likes").setValue("0");

        progressDialog.dismiss();
        alertDialog.setMessage("Idea Submitted Succesfully!");
        alertDialog.show();
    }
}
