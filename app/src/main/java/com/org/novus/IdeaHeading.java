package com.org.novus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IdeaHeading {
    String Heading,pushId;
    String Time;
    String Description,Givenby,CheckBoxStatus,ID;
    int Likes;
    ArrayList LikedByKeysList;

    public IdeaHeading(String heading, String time, String description, String givenBy, String checkBoxStatus, String id, int likes, ArrayList likedByKeyList,String PushId) {
        Heading = heading;
        Time = time;
        Description=description;
        Givenby=givenBy;
        CheckBoxStatus=checkBoxStatus;
        ID=id;
        Likes=likes;
        LikedByKeysList=likedByKeyList;
        pushId=PushId;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
