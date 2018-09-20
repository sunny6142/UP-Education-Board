package com.example.sunny.up_education_board;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


/**
 * Created by Sunny on 5/21/2017.
 */

public class addstudent  extends Fragment {

    View myView;

    EditText name , id , pass;

    Button submit;
    ProgressDialog myown;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.addstudent , container , false);

        Moniter mm = new Moniter();


        myown = MyProgressDialog();
        name = (EditText)myView.findViewById(R.id.input_name);
        id = (EditText)myView.findViewById(R.id.input_id);
        pass = (EditText)myView.findViewById(R.id.input_password);

        submit = (Button)myView.findViewById(R.id.btn_signup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

    //            myownpd.show();
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
                 //   DatabaseReference ref =
               //             FirebaseDatabase.getInstance().getReference().child("user").child(id.getText().toString()).setValue("abc");
             //       FirebaseDatabase.getInstance().getReference().child("user").child("AAA").setValue("abc");

                 //   ref.setValue("ABCD");
                   dataadd daad = new dataadd(name.getText().toString() , id.getText().toString() ,pass.getText().toString());
               //     FirebaseDatabase.getInstance().goOnline();
                /*    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usersRef = database.child("users");

                    Map<String, String> users = new HashMap<String, String>();
                    users.put("alanisawesome",  "June 23, 1912");
                    users.put("gracehop", "December 9, 1906");

                    usersRef.setValue(users);
                    FirebaseDatabase.getInstance().goOffline(); */
      //              myownpd.dismiss();
                }

            }
        });
        return myView;
    }

    public ProgressDialog MyProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading ...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }
}
