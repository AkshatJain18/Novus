package com.org.novus;

import android.view.View;
import android.widget.EditText;

public class Validation {
    String status="OK";
    String data;

    public String CheckEmpty(EditText editText)
    {
         data=editText.getText().toString();
        if(data.equals(""))
        {
            status="Empty";
        }
        else
        {
            status="OK";
        }
        return status;
    }
    public String CheckIfShort(EditText editText)
    {
        data=editText.getText().toString();
        if(data.length()<8)
        {
            status="Short";
        }
        else
        {
            status="OK";
        }
        return status;
    }

}
