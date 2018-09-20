package com.example.sunny.up_education_board;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.StaticLayout;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunny on 4/29/2017.
 */

public class StoreData {

    static String schoolcode , transfername , pno;
    static String DD;

    static int Present = 0;
    static int Absent = 0;
    static int Out= 0;
    String tdate;
    SimpleDateFormat postFormater = new SimpleDateFormat("MMM d yyyy hh:mm:ss a");
    public StoreData() {

    }


}
