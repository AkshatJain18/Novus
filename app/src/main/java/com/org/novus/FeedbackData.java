package com.org.novus;

import java.io.Serializable;

public class FeedbackData implements Serializable {
    String Name,Uid,Feedback,Time;

    public FeedbackData(String name, String uid, String feedback, String time) {
        Name = name;
        Uid = uid;
        Feedback = feedback;
        Time = time;
    }
}
