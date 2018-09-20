package com.example.sunny.up_education_board;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NewStudent extends AppCompatActivity {

    EditText name , id , pass;

    Button submit;
    ProgressDialog myownpd;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseDatabase.getInstance().goOnline();
        Moniter mm = new Moniter();
        mm.MDb.goOffline();
        ref = FirebaseDatabase.getInstance().getReference().child("user");
        ref.setValue("ABCD");
      //  ref = FirebaseDatabase.getInstance().getReference().child("user").child(id.getText().toString());

        myownpd = MyProgressDialog();
        name = (EditText)findViewById(R.id.input_name);
        id = (EditText)findViewById(R.id.input_id);
        pass = (EditText)findViewById(R.id.input_password);
        submit = (Button)findViewById(R.id.btn_signup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myownpd.show();
                if(name.getText().toString().length()==0){
                    name.setError("Username is Required");
                    name.requestFocus();
                    myownpd.dismiss();
                    return;
                }
                else if(id.getText().toString().length()==0){
                    id.setError("Id is Required");
                    id.requestFocus();
                    myownpd.dismiss();
                    return;
                }
                else if(pass.getText().toString().length() < 3){
                    pass.setError("Password must be greater than 3");
                    pass.requestFocus();
                    myownpd.dismiss();
                    return;
                }
                else{
               //     ref.goOnline();
                    Log.v("XYZ","A");
                    Map<String, Object> k = new HashMap<>();
                    Log.v("XYZ","B");
                    k.put(pass.getText().toString(), LoginActivity.schoolcode);
                    Log.v("XYZ","c");
                    k.put("name", name.getText().toString());
                    Log.v("XYZ","D");
                    ref.child(id.getText().toString()).setValue(k);
                    Log.v("XYZ","E");
                    myownpd.dismiss();
                    Log.v("XYZ","F");

                }
            }
        });
        Log.v("XYZ","G");

    }
    public ProgressDialog MyProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(NewStudent.this);
        progressDialog.setMessage("loading ...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }
}
