package com.example.pppb_uas.Views.Modify;

import static com.example.pppb_uas.Views.Overview.MainActivity.sessions_loggedIn;
import static com.example.pppb_uas.Views.Overview.MainActivity.sharedpreferences;
import static com.example.pppb_uas.Views.Overview.MainActivity.userLoggedIn;
import static com.example.pppb_uas.Views.Overview.MainActivity.userPassword;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pppb_uas.Model.ComplaintData;
import com.example.pppb_uas.R;
import com.example.pppb_uas.Views.Dashboard.DashboardActivity;
import com.example.pppb_uas.Views.Overview.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ModifyActivity extends AppCompatActivity {
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sessions;
    private static final int NOTIFICATION_ID = 0;
    public String CHANNEL_ID = "0.0.0";

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // views needed for this activities
        TextView title = findViewById(R.id.tv_titleEditAdd);
        TextView tv_dataId = findViewById(R.id.tv_dataId);
        TextView tv_signUpDescriptions = findViewById(R.id.tv_signUpDescriptions);

        EditText et_complaintTitle = findViewById(R.id.et_complaintTitle);
        EditText et_complaintDescriptions = findViewById(R.id.et_complaintDescriptions);
        EditText et_location = findViewById(R.id.et_complaintPlace);
        EditText et_complaintDate = findViewById(R.id.et_complaintDate);

        Button btn_userData = findViewById(R.id.btn_userData);
        Button btn_user = findViewById(R.id.btn_user);
        ImageButton ib_signout = findViewById(R.id.ib_signoutButton);

        sessions = getSharedPreferences(sharedpreferences, Context.MODE_PRIVATE);
        btn_user.setText(sessions.getString(userLoggedIn, ""));

        Intent data = getIntent();
        String status = data.getStringExtra("status");
        if ("addData".equals(status)) {
             title.setText("Add");
             btn_userData.setText("Add Data");
             tv_signUpDescriptions.setText("Add new Suggestion or complaint");

        } else if ("modifyData".equals(status)) {
            String dataId = data.getStringExtra("id");
            String title1 = data.getStringExtra("title");
            String descriptions = data.getStringExtra("descriptions");
            String datehappened = data.getStringExtra("datehappened");
            String location = data.getStringExtra("location");

            title.setText("Modify");
            et_complaintTitle.setText(title1);
            et_complaintDescriptions.setText(descriptions);
            et_location.setText(location);
            et_complaintDate.setText(datehappened);
            tv_dataId.setText(dataId);
        }

        btn_userData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("addData".equals(status)) {
                    String dataId = db.child("data_complaint").push().getKey();
                    ComplaintData complaint = new ComplaintData(dataId, et_complaintTitle.getText().toString(), et_complaintDescriptions.getText().toString(), et_complaintDate.getText().toString(), et_location.getText().toString(), sessions.getString(userLoggedIn, ""));
                    db.child("data_complaint").child(dataId).setValue(complaint).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // give notifications if succeed
                            displayNotification("Data has been successfuly added!", "Adding Data");

                            // redirect to dashboard
                            Intent redirect = new Intent(ModifyActivity.this, DashboardActivity.class);
                            redirect.putExtra("redirectStatus", "success");
                            redirect.putExtra("redirectFrom", "addData");
                            startActivity(redirect);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ERROR", "Error adding data!");
                        }
                    });
                } else if ("modifyData".equals(status)) {
                    Log.d("a", tv_dataId.getText().toString());

                    TextView tv_dataId = findViewById(R.id.tv_dataId);
                    String id = tv_dataId.getText().toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("title", et_complaintTitle.getText().toString());
                    updateData.put("descriptions", et_complaintDescriptions.getText().toString());
                    updateData.put("dateHappened", et_complaintDate.getText().toString());
                    updateData.put("locationHappened", et_location.getText().toString());
                    reference.child("data_complaint").child(id).updateChildren(updateData);

                    // give notifications if succeed
                    displayNotification("Data has been successfuly modified!", "Modifiy Data");

                    // redirect to dashboard
                    startActivity(new Intent(ModifyActivity.this, DashboardActivity.class));
                    finish();
                }
            }
        });

        ib_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSessions();
                startActivity(new Intent(ModifyActivity.this, MainActivity.class));
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My channel 1";
            String description = "Channel for notification test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void displayNotification(String messages, String title) {
        int notificationId = 0;
        Intent notificationIntent = new Intent(this, DashboardActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        createNotificationChannel();
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_applogo)
                .setContentText(messages)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notificationPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, notifyBuilder.build());
    }
}
