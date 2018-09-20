package com.example.sunny.up_education_board;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sunny on 5/21/2017.
 */

public class addstudent  extends Fragment {

    View myView;

    EditText name , id , pass;

    Button submit;
    ProgressBar viewProgressBar;
    ProgressDialog myown;
    int couter = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.addstudent , container , false);


    //    Moniter mm = new Moniter();

        viewProgressBar = (ProgressBar)myView.findViewById(R.id.progressBar1);
        myown = MyProgressDialog();
        name = (EditText)myView.findViewById(R.id.input_name);
        id = (EditText)myView.findViewById(R.id.input_id);
        pass = (EditText)myView.findViewById(R.id.input_password);

        submit = (Button)myView.findViewById(R.id.btn_signup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProgressBar.setVisibility(View.VISIBLE);
                myown.show();
                if(name.getText().toString().length()==0){
                    name.setError("Username is Required");
                    name.requestFocus();
                    myown.dismiss();
                    return;
                }
                else if(id.getText().toString().length()==0){
                    id.setError("Id is Required");
                    id.requestFocus();
                    myown.dismiss();
                    return;
                }
                else if(pass.getText().toString().length() < 3){
                    pass.setError("Password must be greater than 3");
                    pass.requestFocus();
                    myown.dismiss();
                    return;
                }
                else{

                    add( name.getText().toString() , id.getText().toString() , pass.getText().toString() );
                    name.setText("");
                    id.setText("");
                    pass.setText("");
                    name.requestFocus();
                }

            }
        });
        return myView;
    }
    void add(String name, String id, String pass)
    {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database;
        String key = usersRef.push().getKey();
        Map<String, String> users = new HashMap<String, String>();
        users.put(pass,  LoginActivity.schoolcode);
        users.put("name",name);

        usersRef.child("user").child(id).setValue(users);
      Task<Void> ss =  usersRef.child(LoginActivity.schoolcode).child("TL").child(key).setValue(id);

        myown.dismiss();
        viewProgressBar.setVisibility(View.GONE);

        Toast.makeText(getActivity(), "user profile is added", Toast.LENGTH_SHORT).show();
    }
    public ProgressDialog MyProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading ...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }
}
