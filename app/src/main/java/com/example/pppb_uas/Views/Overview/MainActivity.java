package com.example.pppb_uas.Views.Overview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.pppb_uas.R;
import com.example.pppb_uas.Views.Authentications.AuthenticationsActivity;
import com.example.pppb_uas.Views.Dashboard.DashboardActivity;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sessions;

    public static final String sharedpreferences = "sharedpreferences";
    public static final String userLoggedIn = "user";
    public static final String userPassword = "password";
    public static final boolean sessions_loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // get user sessions
        sessions = getSharedPreferences(sharedpreferences, Context.MODE_PRIVATE);
        boolean userSession = sessions.getBoolean(String.valueOf(sessions_loggedIn), false);

        // check user sessions
        if (userSession) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        } else {
            // set content view to overview activity
            setContentView(R.layout.activity_main);

            //display overview sliders
            displayOverviewPager();

            Button btn_suggest = findViewById(R.id.btn_suggest);
            btn_suggest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, AuthenticationsActivity.class));
                    finish();
                }
            });
        }
    }

    public void displayOverviewPager() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pg_pagerview);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}