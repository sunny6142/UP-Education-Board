package com.example.sunny.up_education_board;


import android.app.ActionBar;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.example.sunny.up_education_board.listeners.OnPictureCapturedListener;
import com.example.sunny.up_education_board.listeners.OngetLocation;
import com.example.sunny.up_education_board.services.PictureService;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


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
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class Manage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , OnPictureCapturedListener, ActivityCompat.OnRequestPermissionsResultCallback, OngetLocation {

    ////Navi
    private AppBarLayout mAppBarLayout;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;

    //End Navi

    public android.location.LocationListener locationListener;
    public LocationManager locationManager;
    public MyServices myService;
    public boolean bound = false;
    public static Double results = null;
    public ProgressDialog pd;
    public static String tdate;
    public static double serlatit, serlong;
    SimpleDateFormat postFormater = new SimpleDateFormat("MMM d yyyy hh:mm:ss a");
    String tarea = null;
    String tlat = null;
    String tlong = null;
    String tname = null;
    String timein = null;
    String vtimein = null;
    String vtimeout = null;
    String timereq = null;
    String pno = null;
    Thread t;
    int counter = 1;
    String settime = null;
    boolean FirstEntry = true, FirstIn = true;
    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    TextView intime, outtime, currentloc, yourintime, yourouttime, settoday, lowtime;
    Button startbut, stopbut;
    int start = 0;
    public double storelat = '\0', storelong = '\0';
    int Task = 0;
    // creating an instance of Firebase Storage
    private StorageReference mStorageRef;
    int pstart = 0;
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    Calendar calendar3 = Calendar.getInstance();
    Date x;
    PieGraph pie;
    //creating a storage reference. Replace the below URL with your Firebase storage URL.
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    protected Restart app;
    private FirebaseDatabase Database;
    boolean pass;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        ///Navi
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Section 1");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_man);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Force English
        mCompactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });

        // Set current date to today
        setCurrentDate(new Date());

        final android.widget.ImageView arrow = (android.widget.ImageView) findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = (RelativeLayout) findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                    mAppBarLayout.setExpanded(false, true);
                    isExpanded = false;
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                    mAppBarLayout.setExpanded(true, true);
                    isExpanded = true;
                }
            }
        });
        //End Navi
     //   setTitle(LoginActivity.sname);


      //  username.setText(LoginActivity.sname.toString());
        Database = FirebaseDatabase.getInstance();
        Task = 1;

        View header=navigationView.getHeaderView(0);
        TextView username = (TextView)header.findViewById(R.id.uname);
        TextView userpno = (TextView)header.findViewById(R.id.pno);
        userpno.setText("School code : " + LoginActivity.schoolcode);


        Log.v("Task -", " " + Task);
        tname = LoginActivity.sname;

       username.setText(tname);

        intime = (TextView) findViewById(R.id.intime);
        outtime = (TextView) findViewById(R.id.outtime);
        currentloc = (TextView) findViewById(R.id.currentloc);
        yourintime = (TextView) findViewById(R.id.yourintime);
        yourouttime = (TextView) findViewById(R.id.yourouttime);
        settoday = (TextView) findViewById(R.id.date);
        lowtime = (TextView) findViewById(R.id.Lowtime);
        startbut = (Button) findViewById(R.id.startclass);
        stopbut = (Button) findViewById(R.id.stopclass);
        pie = (PieGraph) findViewById(R.id.pie_chart);


        startbut.setVisibility(View.VISIBLE);
        startbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        Manage.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Put mobile camera facing toward class and teacher at same time then press ok, App will start taking Images" +
                        " to confirm the class ,images will be taken at random time");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        startbut.setVisibility(View.GONE);
                        stopbut.setVisibility(View.VISIBLE);
                        start = 1;
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        stopbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startbut.setVisibility(View.VISIBLE);
                stopbut.setVisibility(View.GONE);
                start = 0;
            }
        });
        Calendar cl = Calendar.getInstance();

        pd = new ProgressDialog(Manage.this);

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set the progress dialog title and message
        pd.setCancelable(false);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading Data......               ");

        //   pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

        pd.setIndeterminate(false);

        pd.show();

        GDATE();
        GetValue();
    }

    ////Navi
    public void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        TextView datePickerTextView = (TextView) findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_man);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
        //    super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.nav_attendence) {
           RelativeLayout home=(RelativeLayout)this.findViewById(R.id.home);
           home.setVisibility(LinearLayout.GONE);

           FrameLayout fram =(FrameLayout)this.findViewById(R.id.content_manage_frame);
           fram.setVisibility(LinearLayout.VISIBLE);

           FragmentManager fragmentManager = getFragmentManager();
           fragmentManager.beginTransaction()
                   .replace(R.id.content_manage_frame,new Attendence()).commit();
        //   .replace(R.id.content_manage_frame,new Camera2BasicFragment()).commit();
        }
       else if (id == R.id.nav_home) {
           FrameLayout fram =(FrameLayout)this.findViewById(R.id.content_manage_frame);
           fram.setVisibility(LinearLayout.GONE);

           RelativeLayout home=(RelativeLayout)this.findViewById(R.id.home);
           home.setVisibility(LinearLayout.VISIBLE);
       }else if (id == R.id.nav_share) {
            Intent i = new Intent("com.example.sunny.up_education_board.Developer");
           startActivity(i);

        }

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_man);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    ///End Navi
    //////////////////////////////////////////////////
    private void showToast(final String text) {

    }

    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("capturing  photos!");
            return;
        }
        showToast("No camera detected!");
    }

    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] requiredPermissions = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String p : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    p) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(p);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            MyServices.LocalBinder binder = (MyServices.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(Manage.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void doSomething() {

    }

    /////////////////////////////////////////////////



    public void onPreExecute() {
       pd.dismiss();
        //tarea , tlat , tlong , tname,timein , timeout , locationname;
        Log.v("Task -", " " + Task);


        float[] data = {0, 0, 0, 0, 0};
        pd = new ProgressDialog(Manage.this);

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set the progress dialog title and message
        pd.setCancelable(false);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading Location......               ");

        //   pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

        pd.setIndeterminate(false);
        pd.show();
        onPostExecute();
    }


    void in() {
        if (results > 20.0) {
            toneGen1.stopTone();
            toneGen1.startTone(ToneGenerator.TONE_CDMA_REORDER, 10000); //TONE_CDMA_REORDER
        } else {
            toneGen1.stopTone();
        }
    }

    public void GetValue() //tarea , tlat , tlong , tname,timein , timeout , locationname;
    {
        Query myRef;
        DatabaseReference mDatabase;
        myRef = Database.getReference().child(LoginActivity.schoolcode);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(pstart == 0) {
                    Map<String, Object> key = (Map<String, Object>) dataSnapshot.child("location").getValue();
                    //      tarea , tlat , tlong , tname,timein , timeout , locationname;
                    tarea = (String) key.get("area");
                    tlat = (String) key.get("lat");
                    tlong = (String) key.get("long");
                    Map<String, Object> key1 = (Map<String, Object>) dataSnapshot.child("time").getValue();
                    vtimein = (String) key1.get("start");
                    vtimeout = (String) key1.get("stop");
                    timereq = (String) key1.get("required");
                    intime.setText(vtimein);
                    outtime.setText(vtimeout);
                }
                String parts[] = tdate.split(" ");
                if (dataSnapshot.child(parts[0] + " " + parts[1] + " " + parts[2]).hasChild(LoginActivity.sname)) {
                    Map<String, String> key2 = (Map<String, String>) dataSnapshot.child(parts[0] + " " + parts[1] + " " + parts[2]).child(LoginActivity.sname).getValue();
                    timein = (String) key2.get("timein");
                    settime = (String) key2.get("settime");
                } else {
                    timein = tdate;
                    settime = "0";

                }
                Log.v("ABCD",tlat);
                if(pstart == 0) {
                    pstart = 1;
                    onPreExecute();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Manage.this);
                builder1.setMessage("Network Error");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                dialog.cancel();
                                startActivity(i);
                                finish();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                Log.v("XYZ", "Failed to read value.", error.toException());
            }
        });
    }

    public void gettime() {
        Query myRef;
        DatabaseReference mDatabase;
        String parts[] = tdate.split(" ");
        myRef = Database.getReference().child(LoginActivity.schoolcode).child(parts[0] + " " + parts[1] + " " + parts[2]);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(tname)) {
                    Map<String, Object> key = (Map<String, Object>) dataSnapshot.child(tname).getValue();
                    //      tarea , tlat , tlong , tname,timein , timeout , locationname;

                    settime = (String) key.get("settime");
                } else {
                    settime = "0";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Manage.this);
                builder1.setMessage("Check username or password");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                dialog.cancel();
                                startActivity(i);
                                finish();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }
    DatabaseReference timeRef;
    public void GDATE() {
        DatabaseReference ref = Database.getReference("timetest");
        String key = ref.push().getKey();
         timeRef = Database.getReference("timetest").child(key).child(key).child("time");
        timeRef.setValue(ServerValue.TIMESTAMP);
        timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long time = dataSnapshot.getValue(Long.class);

                    tdate = postFormater.format(new Date(time));
                    tdate = tdate.replace("-"," ");
                    tdate = tdate.replace(".","");
                    String part2[] = tdate.split(" ");
                    tdate = part2[0] +" " +part2[1]+" "+part2[2]+" "+part2[3]+" "+part2[4].toUpperCase();
                    Log.v("ABCD 0 ", "onDataChange: " + tdate);
                    Log.v("ABCD get tdate", "onDataChange: " + tdate);
                } else {
                    Log.v("ABCD", "onDataChange: No data");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Manage.this);
                builder1.setMessage("Check username or password");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                dialog.cancel();
                                startActivity(i);
                                finish();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        Log.v("ABCD", ":xx " + tdate);

    }

    String tout = null;
    public void onPostExecute() {

        if (true) {
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        Manage.this);
                alert.setTitle("Alert!!");
                alert.setMessage("To make your attendence count turn on your location");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();

                    }
                });
                alert.show();
            }

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {

                    if(vtimeout != null && vtimein != null)
                       CheckTimeisinbetween();
                    storelat = (double) location.getLatitude();
                    storelong = (double) location.getLongitude();

                 //   Log.v("ABCD-", " - " + storelat + " |+| " + storelong);
                    Location locat = new Location("");

                        locat.setLatitude(Double.parseDouble(tlat));//(25.43702079);
                        locat.setLongitude(Double.parseDouble(tlong));//(81.77977906);
                        Location loc2 = new Location("");
                        loc2.setLatitude(storelat);
                        loc2.setLongitude(storelong);

                        results = Double.valueOf(loc2.distanceTo(locat));
                    Log.v("ABCD_results-", " - " + results);
/////////////////////////////////////////////////////////////////////////////////////////
                    if (results < Double.parseDouble(tarea) && pass == true) {
                        if (!t.isAlive()) {
                           t.start();
                        }
                    } else {

                        t.interrupted();
                        pd.dismiss();
                        settoday.setText("Not In School Or School Time up");
                        DatabaseReference ref = Database.getReference().child(LoginActivity.schoolcode);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String parts[] = tdate.split(" ");

                                if (dataSnapshot.child(parts[0] + " " + parts[1] + " " + parts[2]).hasChild(tname)) {
                                    Map<String, Object> key = (Map<String, Object>) dataSnapshot.child(parts[0] + " " + parts[1] + " " + parts[2]).child(tname).getValue();
                                    timein = (String) key.get("timein");
                                     tout = (String) key.get("timeout");

                                } else {
                                    yourintime.setText("in");
                                    yourouttime.setText("out");
                                }
                                if(tout != null && pass == false)
                                {
                                    yourintime.setText(timein);
                                    yourouttime.setText(tout);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(Manage.this);
                                builder1.setMessage("We ran in problem");
                                builder1.setCancelable(true);
                                builder1.setPositiveButton(
                                        "Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                                dialog.cancel();
                                                startActivity(i);
                                                finish();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        });

                        int a = Integer.parseInt(settime);
                        float[] data = {(Float.parseFloat(timereq) * 60) - a, a, 0, 0, 0};
                        lowtime.setText(String.valueOf((int) a / 60) + " : " + String.valueOf((int)a % 60) + "  hr:min ");
                        pie.setData(data);
                    }

                    try {
                        Geocoder geocoder;
                        List<android.location.Address> addresses = null;
                        geocoder = new Geocoder(Manage.this, Locale.getDefault());

                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String address;
                        currentloc.setText("unknown location !");
                        if(addresses != null && !addresses.isEmpty() && addresses.size() > 0)
                        {
                            address = addresses.get(0).getAddressLine(0);
                            currentloc.setText(address);
                        };
                    } catch (IOException e) {
                        currentloc.setText("unknown location !");
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {
                    if (!FirstEntry) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(
                                Manage.this);
                        alert.setTitle("Alert!!");
                        alert.setMessage("To make your attendence count turn on your location");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do your work here
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                }
            };

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
         //   locationManager.requestLocationUpdates("gps", 36000000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER  , 1000, 0, locationListener);

//////////////////////////////////////////////////////////////////////////////////////////////350000
                t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            while (!isInterrupted()) {

                             //   Thread.sleep(60000);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       // GDATE();
                                  //      timeRef.setValue(ServerValue.TIMESTAMP);
                                        gettime();
                                             CheckTimeisinbetween();
                                        //   timein = tdate;
                                        counter++;
                                        pd.dismiss();
                                        if(pass == true) {
                                        DatabaseReference ref = Database.getReference().child(LoginActivity.schoolcode);
                                            if (start == 1 && counter %10 == 0) {
                                                counter = 1;
                                                new PictureService().startCapturing(Manage.this, Manage.this);
                                            }
                                        //      String key = ref.push().getKey(); // this will create a new unique key
                                        if (timein != null) {
                                            int a = Integer.parseInt(settime) + 1;
                                            float[] data = {(Float.parseFloat(timereq) * 60) - a, a, 0, 0, 0};
                                            lowtime.setText((int) a / 60 + " : " + a % 60 + " hr : min ");
                                            pie.setData(data);

                                            Map<String, Object> value = new HashMap<>();
                                            value.put("lat", storelat);
                                            value.put("long", storelong);
                                            value.put("settime", String.valueOf(a));
                                            value.put("timein", timein);
                                            value.put("timeout", tdate);

                                            String parts[] = tdate.split(" ");
                                            ref.child(parts[0] + " " + parts[1] + " " + parts[2]).child(tname).setValue(value);

                                            String timeinp[] = timein.split(" ");

                                            yourintime.setText(timeinp[3] + " " + timeinp[4]);

                                            yourouttime.setText("In-Session");


                                            String tpart[] = parts[3].split(":");
                                            settoday.setText(parts[0] + " " + parts[1] + " " + parts[2] + " , " + tpart[0] + ":" + tpart[1] + " " + parts[4]);

                                        } else {

                                            AlertDialog.Builder alert = new AlertDialog.Builder(Manage.this);
                                            alert.setTitle("Alert!!");
                                            alert.setMessage("Check your Internet Connection ");
                                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //do your work here
                                                    //Exit Application
                                                    dialog.dismiss();
                                                    //       android.os.Process.killProcess(android.os.Process.myPid());
                                                }
                                            });
                                            alert.show();
                                        }
                                    }
                                    }
                                });Thread.sleep(60000);
                            }
                        } catch (InterruptedException e) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    Manage.this);
                            alert.setTitle("Alert!!");
                            alert.setMessage("App not responding");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do your work here
                             //       Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                               //     startActivity(i);
                                 //   finish();
                                }
                            });
                            alert.show();
                        }
                    }
                };
                //////////////////////////////////////////////////////////////////////////////////
            }
        }

    private void CheckTimeisinbetween() {
        timeRef.setValue(ServerValue.TIMESTAMP);
        String string1 = vtimein;
        String string2 = vtimeout;

        String prts[] = tdate.split(" ");
        Log.v("TIME ",String.valueOf(tdate));
        String someRandomTime = prts[3] + " " + prts[4];
        String string3 = someRandomTime;

        Timemachine tm1 = new Timemachine();
        String part1[] = string1.split(" ");
        String part11[] = part1[0].split(":");
        tm1.hr = Integer.parseInt(part11[0]);
        tm1.min = Integer.parseInt(part11[1]);
        tm1.clock = part1[1];

        Timemachine tm2 = new Timemachine();
        String part2[] = string2.split(" ");
        String part21[] = part2[0].split(":");
        tm2.hr = Integer.parseInt(part21[0]);
        tm2.min = Integer.parseInt(part21[1]);
        tm2.clock = part2[1];

        Timemachine tm3 = new Timemachine();
        String part3[] = string3.split(" ");
        String part31[] = part3[0].split(":");
        tm3.hr = Integer.parseInt(part31[0]);
        tm3.min = Integer.parseInt(part31[1]);
        tm3.clock = part3[1];
        pass = false;

        Log.v("tm1",""+tm1.hr);
        Log.v("tm1",""+tm1.min);
        Log.v("tm1",""+tm1.clock);

        Log.v("tm2",""+tm2.min);
        Log.v("tm2",""+tm2.hr);
        Log.v("tm2",""+tm2.clock);

        Log.v("tm3",""+tm3.hr);
        Log.v("tm3",""+tm3.min);
        Log.v("tm3",""+tm2.clock);
        if(tm1.clock.equals(tm3.clock) && tm1.clock.equals(tm2.clock))
        {
            if(tm1.hr < tm3.hr && tm2.hr > tm3.hr)
            {
                Log.v("ABCD","PASS");
                pass = true;
            }
            else if(tm1.hr == tm3.hr && tm1.min <= tm3.min)
            {
                Log.v("ABCD","PASS");
                pass = true;
            }
            else if(tm2.hr == tm3.hr && tm2.min >= tm3.min)
            {
                Log.v("ABCD","PASS");
                pass = true;
            }

        }
    }
}