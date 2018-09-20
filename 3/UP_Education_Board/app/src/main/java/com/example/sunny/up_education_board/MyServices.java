package com.example.sunny.up_education_board;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.sunny.up_education_board.listeners.OnPictureCapturedListener;
import com.example.sunny.up_education_board.listeners.OngetLocation;
import com.example.sunny.up_education_board.services.PictureService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.example.sunny.up_education_board.Manage.MY_PERMISSIONS_REQUEST_ACCESS_CODE;
import static com.example.sunny.up_education_board.Manage.serlatit;
import static com.example.sunny.up_education_board.Manage.serlong;

/**
 * Created by Sunny on 4/15/2017.
 */

public class MyServices extends Service {
    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private OngetLocation serviceCallbacks;


    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        MyServices getService() {
            // Return this instance of MyService so clients can call public methods
            return MyServices.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(OngetLocation callbacks) {
        serviceCallbacks = callbacks;
    }
}

