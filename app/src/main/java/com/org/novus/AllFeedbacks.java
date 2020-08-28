package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class AllFeedbacks extends AppCompatActivity {

    ListView listView;
    ArrayList<FeedbackData> feedbacks;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_feedbacks);
        feedbacks=new ArrayList<>();
        listView=(ListView)findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter(this, R.layout.listrow, R.id.HeadingTextView, feedbacks) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView Heading = (TextView) view.findViewById(R.id.HeadingTextView);
                TextView Time = (TextView) view.findViewById(R.id.DateTextView);
                TextView New = (TextView) view.findViewById(R.id.NewTextView);
                TextView ShortDescription = (TextView) view.findViewById(R.id.ShortDescriptionTextView);

                New.setVisibility(View.GONE);
                Heading.setVisibility(View.GONE);
                Time.setText(feedbacks.get(position).Time);
                if(feedbacks.get(position).Feedback.length()>20) {

                    ShortDescription.setText(feedbacks.get(position).Feedback.substring(0, 20)+"...");
                }
                else
                {
                    ShortDescription.setText(feedbacks.get(position).Feedback);
                }

                Button ViewButton;

                ViewButton=(Button)view.findViewById(R.id.ViewButton);


                ViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FeedbackData ih=feedbacks.get(position);

                        Intent intent = new Intent(getApplicationContext(), FeedbackContent.class);
                        intent.putExtra("FeedbackObject",ih);
                        startActivity(intent);
                    }
                });
                return view;
            }
        };


        listView.setAdapter(arrayAdapter);
        DisplayFeedbacks();

    }
    String name,feedback,time;
    public void DisplayFeedbacks()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("feedbacks");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String Uid=dataSnapshot1.getKey();
                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren())
                    {
                         name=dataSnapshot2.child("name").getValue().toString();
                         feedback=dataSnapshot2.child("feedback").getValue().toString();
                         time=dataSnapshot2.child("time").getValue().toString();
                         feedbacks.add(new FeedbackData(name,Uid,feedback,time));
                    }

                }
                Collections.reverse(feedbacks);
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
