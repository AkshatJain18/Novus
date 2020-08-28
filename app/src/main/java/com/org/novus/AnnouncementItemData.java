package com.org.novus;


import java.util.ArrayList;

public class AnnouncementItemData {
    String Title;
    ArrayList<SeenBy> SeenBy;
    String Time,id;
    String Description,File,URL,extension;

    public AnnouncementItemData(String title, String time, String description,String file,String url,String ex,ArrayList<SeenBy> seenBy,String id1 ) {
        Title = title;
        Time = time;
        Description = description;
        File=file;
        URL=url;
        extension=ex;
        SeenBy=seenBy;
        id=id1;
    }
}
