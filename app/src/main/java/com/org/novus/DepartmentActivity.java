package com.org.novus;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DepartmentActivity extends AppCompatActivity
        {

    TextView deptTextview, InchargeTextview;
    WebView RolesContent;
    String Department, Incharge;
    String Roles_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_department);


        deptTextview = (TextView) findViewById(R.id.deptTextview);
        InchargeTextview = (TextView) findViewById(R.id.inchargeTextview);
        RolesContent = (WebView) findViewById(R.id.RolesContent);

        Intent intent = getIntent();
        Department = intent.getStringExtra("Department");
        Incharge = intent.getStringExtra("Incharge");
        try {
            DisplayDepartment();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DisplayDepartment() throws IOException {
        deptTextview.setText(Department);
        InchargeTextview.setText(Incharge);

        WebSettings webSetting = RolesContent.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);

        if (Department.equalsIgnoreCase("Technical")) {
            RolesContent.loadUrl("file:///android_asset/Technical.html");
        } else if (Department.equals("Event management")) {
            RolesContent.loadUrl("file:///android_asset/Event.html");
        } else {
            RolesContent.loadUrl("file:///android_asset/HR.html");
        }
    }



}

