package com.example.sunny.up_education_board;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sunny on 5/22/2017.
 */

public class dataadd {

    String  name , id , pass;
    public dataadd(String name, String id, String pass) {
        this.name = name;
        this.id = id;
        this.pass = pass;
        FirebaseDatabase.getInstance().goOnline();
        add();
    }

    void add()
    {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database;
        String key = usersRef.push().getKey();
        Map<String, String> users = new HashMap<String, String>();
        users.put(pass,  LoginActivity.schoolcode);
        users.put("name",name);

        usersRef.child("user").child(id).setValue(users);
        usersRef.child(LoginActivity.schoolcode).child("TL").child(key).setValue(id);
        addstudent ad = new addstudent();

    }
}
