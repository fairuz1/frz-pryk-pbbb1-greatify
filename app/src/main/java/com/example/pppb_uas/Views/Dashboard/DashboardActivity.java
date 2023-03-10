package com.example.pppb_uas.Views.Dashboard;
import static com.example.pppb_uas.Views.Overview.MainActivity.sessions_loggedIn;
import static com.example.pppb_uas.Views.Overview.MainActivity.sharedpreferences;
import static com.example.pppb_uas.Views.Overview.MainActivity.userLoggedIn;
import static com.example.pppb_uas.Views.Overview.MainActivity.userPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pppb_uas.Model.ComplaintData;
import com.example.pppb_uas.R;
import com.example.pppb_uas.Views.Modify.ModifyActivity;
import com.example.pppb_uas.Views.Overview.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sessions;

    public List<ComplaintData> complaintArray = new ArrayList<>();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // get sessions and intent
        sessions = getSharedPreferences(sharedpreferences, Context.MODE_PRIVATE);
        Intent data = getIntent();
        String from = data.getStringExtra("redirectFrom");
        String statusRedirect = data.getStringExtra("redirectStatus");

//        if ("addData".equals(from) && "success".equals(statusRedirect)) {
//            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//            View PopUp = inflater.inflate(R.layout.activity_alert_success, null);
//
//            int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            boolean focusable = true;
//            final PopupWindow popupWindow = new PopupWindow(PopUp, width, height, focusable);
//            RelativeLayout view = new RelativeLayout(this.getApplicationContext());
//
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//            PopUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    popupWindow.dismiss();
//                }
//            });
//
//            LinearLayout ll_mainLayout = findViewById(R.id.ll_mainLayout);
//            TextView tv_alertText = PopUp.findViewById(R.id.tv_alertText);
//            tv_alertText.setText("Successfully Adedd new Suggestion Data");
//
//        } else if ("signin".equals(from) && "success".equals(statusRedirect)) {
//            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//            View PopUp = inflater.inflate(R.layout.activity_alert_success, null);
//
//            int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            boolean focusable = true;
//            final PopupWindow popupWindow = new PopupWindow(DashboardActivity.this);
//            RelativeLayout view = new RelativeLayout(this.getApplicationContext());
//
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//            PopUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    popupWindow.dismiss();
//                }
//            });
//
//            LinearLayout ll_mainLayout = findViewById(R.id.ll_mainLayout);
//            TextView tv_alertText = PopUp.findViewById(R.id.tv_alertText);
//            tv_alertText.setText("Welcome back " + sessions.getString(userLoggedIn, "") + "!");
//        }

        // get data
        // display complaint data
        displayData();

        Button btn_user = findViewById(R.id.btn_user);
        ImageButton ib_signout = findViewById(R.id.ib_signoutButton);
        ImageButton ib_addData = findViewById(R.id.ib_addData);

        btn_user.setText(sessions.getString(userLoggedIn, ""));

        ib_addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirect = new Intent(DashboardActivity.this, ModifyActivity.class);
                redirect.putExtra("status", "addData");
                startActivity(redirect);
            }
        });

        ib_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSessions();
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    public void resetSessions() {
        sessions = getSharedPreferences(sharedpreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor removeSessions = sessions.edit();
        removeSessions.putString(userLoggedIn, "user");
        removeSessions.putString(userPassword, "password");
        removeSessions.putBoolean(String.valueOf(sessions_loggedIn), false);
        removeSessions.apply();
    }

    public void displayData() {
        db.child("data_complaint").orderByChild(sessions.getString(userLoggedIn, "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear data in complaintArray to prevent duplicate
                complaintArray.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    ComplaintData data = item.getValue(ComplaintData.class);
                    if (data != null && sessions.getString(userLoggedIn, "").equals(data.getSender())) {
                        complaintArray.add(0, data);
                    } else if (data != null && !sessions.getString(userLoggedIn, "").equals(data.getSender())){
                        complaintArray.add(data);
                    } else {
                        Log.d("NULL", "NULL");
                    }
                }

                // check array of data
                RecyclerView recycler = findViewById(R.id.dataRecycler);
                if (complaintArray.size() > 0) {
                    RecyclerView dataRecycler = findViewById(R.id.dataRecycler);
                    ComplaintAdapter adapter = new ComplaintAdapter(DashboardActivity.this, complaintArray);
                    dataRecycler.setAdapter(adapter);
                    dataRecycler.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                    dataRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", "error while taking handlerCustomerStockData");
            }
        });
    }
}