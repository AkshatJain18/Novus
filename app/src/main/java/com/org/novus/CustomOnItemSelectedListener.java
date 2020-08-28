package com.org.novus;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        //Toast.makeText(this,"sef",len)
    }

    @Override

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
