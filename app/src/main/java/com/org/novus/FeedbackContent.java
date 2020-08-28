package com.org.novus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackContent extends AppCompatActivity {

    TextView feedback,Time,GivenBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_content);

        feedback=(TextView)findViewById(R.id.ContentTextView);

        Time=(TextView)findViewById(R.id.Date);

        GivenBy=(TextView)findViewById(R.id.GivenBy);

        FeedbackData feedbackData=(FeedbackData)getIntent().getSerializableExtra("FeedbackObject");

//        Toast.makeText(getApplicationContext(),feedbackData.Feedback,Toast.LENGTH_LONG).show();

        feedback.setText(feedbackData.Feedback);

        Time.setText(feedbackData.Time);

        GivenBy.setText(feedbackData.Name);

    }
}
