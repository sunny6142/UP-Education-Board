package com.example.sunny.up_education_board;

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
    public dataadd(String name , String id , String pass) {
        this.name = name;
        this.id = id;
        this.pass = pass;
        FirebaseDatabase.getInstance().goOnline();
        add();
    }

    void add()
    {

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users");

        Map<String, String> users = new HashMap<String, String>();
        users.put("alanisawesome",  "June 23, 1912");
        users.put("gracehop", "December 9, 1906");

        usersRef.setValue(users);
    }
}
