package com.example.pppb_uas.Views.Authentications;
import static com.example.pppb_uas.Views.Overview.MainActivity.sessions_loggedIn;
import static com.example.pppb_uas.Views.Overview.MainActivity.sharedpreferences;
import static com.example.pppb_uas.Views.Overview.MainActivity.userLoggedIn;
import static com.example.pppb_uas.Views.Overview.MainActivity.userPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pppb_uas.Model.UserData;
import com.example.pppb_uas.R;
import com.example.pppb_uas.Views.Dashboard.DashboardActivity;
import com.example.pppb_uas.Views.Modify.ModifyActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthenticationsActivity extends AppCompatActivity {
    EditText et_username, et_password;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //set statusbar to transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // get sessions data
        sessions = getSharedPreferences(sharedpreferences, Context.MODE_PRIVATE);

        // get sign in data
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        TextView tv_registerUser = findViewById(R.id.tv_registerUser);
        Button btn_signin = findViewById(R.id.btn_signin);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                // check sign in data
                if (!"".equals(et_username.getText().toString()) && !"".equals(et_password.getText().toString())) {
                    // verify sign in data
                    verifyUserData(et_username.getText().toString(), et_password.getText().toString());
                } else {
                    // display alert using popup
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View PopUp = inflater.inflate(R.layout.activity_alert_warning, null);

                    int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    final PopupWindow popupWindow = new PopupWindow(PopUp, width, height, focusable);

                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    PopUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    });

                    // customize alert messages
                    LinearLayout ll_mainLayout = findViewById(R.id.ll_mainLayout);
                    TextView tv_alertText = PopUp.findViewById(R.id.tv_alertText);
                    tv_alertText.setText("Some Field Are Not Properly Filled!");

                    if (!"".equals(et_username.getText().toString())) {
                        et_username.setBackgroundResource(R.drawable.et_rounded_warning);
                        et_username.setHintTextColor(getResources().getColor(R.color.alertWarning));
//                        et_username.setHintTextColor(getResources().getColor(R.color.alertWarning));
                    } else if (!"".equals(et_password.getText().toString())) {
                        et_password.setBackgroundResource(R.drawable.et_rounded_warning);
                        et_password.setHintTextColor(getResources().getColor(R.color.alertWarning));
//                        et_password.setHintTextColor(getResources().getColor(R.color.alertWarning));
                    } else if ("".equals(et_username.getText().toString()) && "".equals(et_password.getText().toString())){
                        et_username.setBackgroundResource(R.drawable.et_rounded_warning);
                        et_password.setBackgroundResource(R.drawable.et_rounded_warning);
//                        et_username.setHintTextColor(getResources().getColor(R.color.alertWarning));
//                        et_password.setHintTextColor(getResources().getColor(R.color.alertWarning));
                    }
                }

            }
        });

        tv_registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                View v = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_signup, (RelativeLayout) view.findViewById(R.id.rl_mainLayout));
                builder.setView(v);

                final AlertDialog alertDialog = builder.create();

                if (alertDialog.getWindow() != null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                alertDialog.show();

                RelativeLayout rl_mainLayout = v.findViewById(R.id.rl_mainLayout);

                EditText et_signUpUsername = rl_mainLayout.findViewById(R.id.et_signUpUsername);
                EditText et_signUpEmail = rl_mainLayout.findViewById(R.id.et_signUpEmail);
                EditText et_signUpPassword = rl_mainLayout.findViewById(R.id.et_signUpPassword);
                EditText et_signUpRepeatPassword = rl_mainLayout.findViewById(R.id.et_signUpRepeatPassword);
                Button btn_signup = rl_mainLayout.findViewById(R.id.btn_signup);

                btn_signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (et_signUpPassword.getText().toString().equals(et_signUpRepeatPassword.getText().toString())) {
                            String userid = db.child("user_data").push().getKey();
                            UserData user = new UserData(userid, et_signUpEmail.getText().toString(), et_signUpUsername.getText().toString(), et_signUpPassword.getText().toString());
                            if (userid != null) {
                                db.child("data_user").child(userid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "Successfuly registered!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                        Log.d("SUCCESS", userid);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("ERROR", "Error adding data!");
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Some field are not yet fulfilled!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

//                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                View signupPopUp = inflater.inflate(R.layout.activity_signup, null);
//
//                int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                boolean focusable = true;
//                final PopupWindow signup = new PopupWindow(signupPopUp, width, height, focusable);
//
//                signup.showAtLocation(view, Gravity.CENTER, 0, 0);
//                signupPopUp.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        signup.dismiss();
//                        return true;
//                    }
//                });
//
//                EditText et_signUpUsername = (EditText) signupPopUp.findViewById(R.id.et_signUpUsername);
//                EditText et_signUpEmail = (EditText) signupPopUp.findViewById(R.id.et_signUpEmail);
//                EditText et_signUpPassword = (EditText) signupPopUp.findViewById(R.id.et_signUpPassword);
//                EditText et_signUpRepeatPassword = (EditText) signupPopUp.findViewById(R.id.et_signUpRepeatPassword);
//                Button btn_signup = (Button) signupPopUp.findViewById(R.id.btn_signup);
//
//                btn_signup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (et_signUpPassword.getText().toString().equals(et_signUpRepeatPassword.getText().toString())) {
//                            String userid = db.child("user_data").push().getKey();
//                            UserData user = new UserData(userid, et_signUpEmail.getText().toString(), et_signUpUsername.getText().toString(), et_signUpPassword.getText().toString());
//                            if (userid != null) {
//                                db.child("data_user").child(userid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(getApplicationContext(), "Successfuly registered!", Toast.LENGTH_SHORT).show();
//                                        signup.dismiss();
//                                        Log.d("SUCCESS", userid);
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d("ERROR", "Error adding data!");
//                                    }
//                                });
//                            }
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Some field are not yet fulfilled!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
            }
        });
    }

    public void verifyUserData(String username, String password) {
        db.child("data_user").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    UserData user = item.getValue(UserData.class);
                    if (user.getName().equals(username) && user.getPassword().equals(password)) {
                        SharedPreferences.Editor editorSession = sessions.edit();
                        editorSession.putString(userLoggedIn, username);
                        editorSession.putString(userPassword, password);
                        editorSession.putBoolean(String.valueOf(sessions_loggedIn), true);
                        editorSession.apply();

                        Intent redirect = new Intent(AuthenticationsActivity.this, DashboardActivity.class);
                        redirect.putExtra("redirectStatus", "success");
                        redirect.putExtra("redirectFrom", "signin");
                        startActivity(redirect);
                        finish();
                    }
                }

                if (!sessions.getBoolean(String.valueOf(sessions_loggedIn), false)) {
                    // display alert using popup
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View PopUp = inflater.inflate(R.layout.activity_alert_danger, null);

                    int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    final PopupWindow popupWindow = new PopupWindow(PopUp, width, height, focusable);
                    RelativeLayout view = new RelativeLayout(getApplicationContext());

                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    PopUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    });

                    // customize alert messages
                    LinearLayout ll_mainLayout = findViewById(R.id.ll_mainLayout);
                    TextView tv_alertText = PopUp.findViewById(R.id.tv_alertText);
                    tv_alertText.setText("Wrong Signin Credentials");

                    et_username.setBackgroundResource(R.drawable.et_rounded_danger);
                    et_password.setBackgroundResource(R.drawable.et_rounded_danger);
//                    et_username.setHintTextColor(getResources().getColor(R.color.alertDanger));
//                    et_password.setHintTextColor(getResources().getColor(R.color.alertDanger));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", "An Error when accessing database has occured");
            }
        });
    }
}