package com.example.sunny.up_education_board;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private DatabaseReference mPostReference;
    static public String scode;
    static public String sname;

    private  Button button;
    EditText editText;
    static  public String ch;
    private Query myRef;
    DatabaseReference mDatabase;
    // UI references.
    public static AutoCompleteTextView Username;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

 //   public SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    SimpleDateFormat postFormater = new SimpleDateFormat("MMM d yyyy hh:mm:ss a");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        Username = (AutoCompleteTextView) findViewById(R.id.name);

        mPasswordView = (EditText) findViewById(R.id.password);

        DatabaseReference timeRef = FirebaseDatabase.getInstance().getReference("time-test");

        timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long time = dataSnapshot.getValue(Long.class); //HH:mm:ss

                    Manage.tdate = postFormater.format(new Date(time));
                    Log.v("ABCD login", "onDataChange: " + Manage.tdate);
                    Manage.tdate = Manage.tdate.replace("-"," ");
                    Log.v("ABCD login", "onDataChange: " + Manage.tdate);
                } else {
                    Log.v("ABCD", "onDataChange: No data");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        timeRef.setValue(ServerValue.TIMESTAMP);

        Button Login = (Button) findViewById(R.id.email_sign_in_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.v("ABCD","XYZ");
        Login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               ProgressDialog pd = new ProgressDialog(LoginActivity.this);


                pd = new ProgressDialog(LoginActivity.this,R.style.MyTheme);
                pd.setProgressStyle(ProgressDialog.BUTTON_NEUTRAL);

                // Set the progress dialog title and message
            //    pd.setTitle("Please Wait");
                pd.setMessage("Loading......               ");

                // Set the progress dialog background color
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

                pd.setIndeterminate(false);

               pd.setCancelable(true);
        //        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //    pd.setMessage("Loding .... :) ");
             //   pd.setProgressStyle(android.R.style.Theme_Material_Light_Dialog_Presentation);
                pd.show();

                attemptLogin(pd);
                ///////////////////////////////////////

                ///////////////////////////////////////
            }
        });

    }

    public void disms(ProgressDialog pd)
    {
        pd.dismiss();
    }

    private void attemptLogin(ProgressDialog pd) {

        // Reset errors.
        Username.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = Username.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) ) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            Username.setError(getString(R.string.error_field_required));
            focusView = Username;
            cancel = true;
        }

            GetValue(email,password,pd);


        /////////////////////

    }
    public void GetValue(final String email, final String password , final ProgressDialog pd)
    {
        this.myRef = FirebaseDatabase.getInstance().getReference().child("user");
        this.myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   Object key = dataSnapshot.getValue();

                    Map<String ,Object> key = (Map<String, Object>) dataSnapshot.getValue();
                    sname = (String) key.get(email);
                if(key.get(email) == null) {
               //     pd.dismiss();
                    Getme(email,password,pd);
                    return;
                }
                    if(sname.equals(password))
                    {
                        disms(pd);
                        Intent intent = new Intent("com.example.sunny.up_education_board.Manage");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                  else{
                        Getme(email,password,pd);
                        return;
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.v("XYZ", "Failed to read value.", error.toException());
            }
        });
    }
    public void Getme(final String email, final String password , final ProgressDialog pd)
    {
        this.myRef = FirebaseDatabase.getInstance().getReference().child("user1");
        this.myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   Object key = dataSnapshot.getValue();

                Map<String ,Object> key = (Map<String, Object>) dataSnapshot.getValue();
                sname = (String) key.get(email);
                Log.v("XYZ ", "Value is: " + password);
                Log.v("ABCD ", "Value is: " + key.get(email));
                if(key.get(email) == null) {
                    //     pd.dismiss();
                    disms(pd);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("Check username or password");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    return;
                }
                if(sname.equals(password))
                {
                    disms(pd);
                    Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();

                }
                else{
                    disms(pd);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("Check username or password");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    return;
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.v("XYZ", "Failed to read value.", error.toException());
            }
        });
    }

    ////////remain login

}
