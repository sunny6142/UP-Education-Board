package com.example.sunny.up_education_board;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.sunny.up_education_board.StoreData.Absent;
import static com.example.sunny.up_education_board.StoreData.Out;
import static com.example.sunny.up_education_board.StoreData.Present;

public class Moniter extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    public RecyclerView recList;
    private RecyclerView.LayoutManager mLayoutManager;
    int one = 0;
    int two = 0;
    // private
    public static ChildEventListener chev;
    private static Query myRef1;
    private static  String date;
    private static String Homework;
    public ContactAdapter ca;
    public static String value , DD, ci_surname , ci_email , ci_title;
    String tmdate;
    private AppBarLayout mAppBarLayout;

    SimpleDateFormat postFormater = new SimpleDateFormat("MMM d yyyy");
    SimpleDateFormat postFormater2 = new SimpleDateFormat("MMM d yyyy hh:mm:ss a");
    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;
    List<ContactInfo> result = new ArrayList<ContactInfo>();
    private ProgressDialog progressDialog ;
    private Context targetCtx ;
    ProgressDialog myownpd;
    String r[] = new String[100000];
    long i;
    int timepass = 1;
    String moniterdate;
    public static  String transfername = null;
    public FirebaseDatabase MDb;
    int mark = 0;
    int holy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moniter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        MDb = FirebaseDatabase.getInstance();
    //    MDb.goOnline();
        if(Out == 1)
        {
            GDATE();
        }

        if(DD == null)
        {
            DD = StoreData.DD;
            Log.v("ABCD","Tesminated ..........................................................");
         Intent i = new Intent(Moniter.this , LoginActivity.class);
            startActivity(i);
            finish();
        }
        if(DD != null){
            String parts[] = DD.split(" ");
            DD = parts[0]+" "+parts[1]+" "+parts[2];
            setTitle("Select Date");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header=navigationView.getHeaderView(0);
  //      TextView username = (TextView)header.findViewById(R.id.uname);
  //      TextView userpno = (TextView)header.findViewById(R.id.pno);
 //       userpno.setText("School code : " + LoginActivity.schoolcode);

        recList = (RecyclerView)findViewById(R.id.cardList);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Force English
        mCompactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
//                MDb.goOnline();
                one = 0;
                two = 0;
                setSubtitle(postFormater.format(dateClicked));
              //  Log.v("ABCD",postFormater.format(dateClicked));

                DD = postFormater.format(dateClicked);
                String parts[] = DD.split(" ");
                DD = parts[0]+" "+parts[1]+" "+parts[2];
                Log.v("DD*",postFormater.format(dateClicked));
                if(holy == 1) return;
                myownpd = MyProgressDialog();
                ///////
                result = new ArrayList<ContactInfo>();
             /*   Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); */

                ca = new ContactAdapter(getApplicationContext(),createList(30));
                ca.SetOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, final int layoutPosition, boolean b) {
                        // Log.v("done",layoutPosition +""+ result.get(layoutPosition).title);
                        if(mark == 1){
                        //    GDATE();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timetest");
                            String key = ref.push().getKey();
                            timeRef = FirebaseDatabase.getInstance().getReference("timetest").child(key).child(key).child("time");
                            timeRef.setValue(ServerValue.TIMESTAMP);
                            timeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        long time = dataSnapshot.getValue(Long.class);
                                        tmdate = postFormater2.format(new Date(time));
                                        tmdate = tmdate.replace("-"," ");
                                        tmdate = tmdate.replace(".","");
                                        String part2[] = tmdate.split(" ");
                                        tmdate = part2[0] +" " +part2[1]+" "+part2[2]+" "+part2[3]+" "+part2[4].toUpperCase();
                                        Log.v("ABCD 0 ", "onDataChange: " + tmdate);
                                        Log.v("ABCD get tdate", "onDataChange: " + tmdate);
                                        while(tmdate == null){
                                            try {
                                                wait(100);
                                            } catch (InterruptedException e) {
                                            }
                                        }
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(LoginActivity.schoolcode);
                                        Map<String, Object> value = new HashMap<>();
                                        int a = 1;
                                        value.put("settime", String.valueOf(a));
                                        value.put("timein","10:00:00 AM");
                                        value.put("timeout", tmdate);

                                        String parts[] = tmdate.split(" ");
                                        ref.child(parts[0] + " " + parts[1] + " " + parts[2]).child(result.get(layoutPosition).email).setValue(value);
                                        Toast.makeText(Moniter.this,result.get(layoutPosition).email + "is Present " , Toast.LENGTH_SHORT).show();

                                        timeRef.removeEventListener(this);
                                    } else {
                                        Log.v("ABCD", "onDataChange: No data");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                                    builder1.setMessage("Not able to update data, Check your internet connection");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                                    dialog.dismiss();
                                                    startActivity(i);
                                                    finish();

                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            });
                      //      Log.v("ABCD", ":xx " + tmdate);

                            return;
                        }

                        transfername = result.get(layoutPosition).email;
                        StoreData.DD = DD;
                        StoreData.transfername = transfername;
                        LinearLayout mainLayout=(LinearLayout)findViewById(R.id.lire);
                        mainLayout.setVisibility(LinearLayout.GONE);
                        Intent intent = new Intent("com.example.sunny.up_education_board.ImageView");
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onClick(View view) {

                    }
                });
         //imo       MDb.goOffline();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(postFormater.format(firstDayOfNewMonth));
            }
        });

        // Set current date to today
        setCurrentDate(new Date());

        final ImageView arrow = (ImageView) findViewById(R.id.date_picker_arrow);

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
        /////////////////////////////////work
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(Moniter.this);

        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setLayoutManager(llm);
        myownpd = MyProgressDialog();
        ca = new ContactAdapter(getApplicationContext(),createList(30));
        ca.SetOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, final int layoutPosition, boolean b) {
                // Log.v("done",layoutPosition +""+ result.get(layoutPosition).title);
        //        MDb.goOnline();
                // Log.v("done",layoutPosition +""+ result.get(layoutPosition).title);
                if(mark == 1){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timetest");
                    String key = ref.push().getKey();
                    timeRef = FirebaseDatabase.getInstance().getReference("timetest").child(key).child(key).child("time");
                    timeRef.setValue(ServerValue.TIMESTAMP);
                    timeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                long time = dataSnapshot.getValue(Long.class);
                                tmdate = postFormater2.format(new Date(time));
                                tmdate = tmdate.replace("-"," ");
                                tmdate = tmdate.replace(".","");
                                String part2[] = tmdate.split(" ");
                                tmdate = part2[0] +" " +part2[1]+" "+part2[2]+" "+part2[3]+" "+part2[4].toUpperCase();
                                Log.v("ABCD 0 ", "onDataChange: " + tmdate);
                                Log.v("ABCD get tdate", "onDataChange: " + tmdate);
                                while(tmdate == null){
                                    try {
                                        wait(100);
                                    } catch (InterruptedException e) {
                                    }
                                }
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(LoginActivity.schoolcode);
                                Map<String, Object> value = new HashMap<>();
                                int a = 1;
                                value.put("settime", String.valueOf(a));
                                value.put("timein","10:00:00 AM");
                                value.put("timeout", tmdate);

                                String parts[] = tmdate.split(" ");
                                ref.child(parts[0] + " " + parts[1] + " " + parts[2]).child(result.get(layoutPosition).email).setValue(value);
                                Toast.makeText(Moniter.this,result.get(layoutPosition).email + "is Present " , Toast.LENGTH_SHORT).show();

                                timeRef.removeEventListener(this);
                            } else {
                                Log.v("ABCD", "onDataChange: No data");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                            builder1.setMessage("Not able to update data, Check your internet connection");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                            dialog.dismiss();
                                            startActivity(i);
                                            finish();

                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    });
                    Log.v("ABCD", ":xx " + tmdate);
                    return;
                }
                transfername = result.get(layoutPosition).email;
                StoreData.DD = DD;
                StoreData.transfername = transfername;
                LinearLayout mainLayout=(LinearLayout)findViewById(R.id.lire);
                mainLayout.setVisibility(LinearLayout.GONE);

                //    FragmentManager fragmentManager = getFragmentManager();
                //    fragmentManager.beginTransaction()
                //           .replace(R.id.content_frame,new ViewData()).commit();
                Intent intent = new Intent("com.example.sunny.up_education_board.ImageView");
                startActivity(intent);
                //imo        MDb.goOffline();
                finish();
            }

            @Override
            public void onClick(View view) {

            }
        });
        //  recList.setAdapter(ca);

        //imo     MDb.goOffline();
    }

    ////Navi

    ///End Navi
    public void setCurrentDate(Date date) {
        setSubtitle(postFormater.format(date));
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

   ///////////////////

//////////////////////////////////

    ///////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_admin_pannel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_1) {
            LoginActivity.schoolcode = "DIET14A";
            StoreData.schoolcode = "DIET14A";
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_2) {
            LoginActivity.schoolcode = "DIET14B";
            StoreData.schoolcode = "DIET14B";
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_3) {
            LoginActivity.schoolcode = "DIET15A";
            StoreData.schoolcode = "DIET15A";
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_4) {
            LoginActivity.schoolcode = "DIET15B";
            StoreData.schoolcode = "DIET15B";
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_5) {
            mark = 1;
            Toast.makeText(Moniter.this, "Ready to take Attendance !...", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Present = 0;
            Absent = 0;
            Out = 0;
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }else if (id == R.id.nav_present) {
            Present = 1;
            Absent = 0;
            Out = 0;
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_ouside) {
            Present = 0;
            Absent = 0;
            Out = 1;
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_absent) {
            Present = 0;
            Absent = 1;
            Out = 0;
            Intent intent = new Intent("com.example.sunny.up_education_board.Moniter");
            startActivity(intent);
            finish();
        }
        else if(id == R.id.nav_add_student)
        {
            //imo      MDb.goOffline();
            RelativeLayout home=(RelativeLayout)this.findViewById(R.id.content_home);
            home.setVisibility(RelativeLayout.GONE);

            FrameLayout fram =(FrameLayout)this.findViewById(R.id.content_frame);
            fram.setVisibility(FrameLayout.VISIBLE);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,new addstudent()).commit();

        }
        else if(id == R.id.nav_holiday)
        {
            //imo      MDb.goOffline();
            RelativeLayout home=(RelativeLayout)this.findViewById(R.id.content_home);
            home.setVisibility(RelativeLayout.GONE);

            FrameLayout fram =(FrameLayout)this.findViewById(R.id.content_frame);
            fram.setVisibility(FrameLayout.VISIBLE);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,new holiday()).commit();

        }
        else if(id == R.id.nav_developer)
        {
            //imo      MDb.goOffline();
            Intent intent = new Intent("com.example.sunny.up_education_board.Developer");
            startActivity(intent);

        }
        else if(id == R.id.nav_school_code)
        {
            //imo      MDb.goOffline();
            Intent intent = new Intent("com.example.sunny.up_education_board.School_code");
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ProgressDialog MyProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(Moniter.this);
        progressDialog.setMessage("loading ...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    private List<ContactInfo> createList(int size) {
        myownpd.show();
        Go(result);
        Log.v("ABCD##", "Return222");
        return result ;
    }


    private List<ContactInfo> Go(final List<ContactInfo> result) {

        myRef1 = MDb.getReference().child(StoreData.schoolcode).child("TL");

   //     myRef1.keepSynced(true);
    //    myownpd.show();
        myRef1.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (one == 0) {
                    one = 1;
                    i = 0;
                    final long[] pendingLoadCount = {dataSnapshot.getChildrenCount()};
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//imp                        myownpd.show();
                        Log.v("ABCDP", itemSnapshot.getValue().toString());
                        value = itemSnapshot.getValue().toString();
                        //    String parts[] = value.split("#");

                        r[(int) i] = value;
                        i++;

                        pendingLoadCount[0] = pendingLoadCount[0] - 1;

                        if (pendingLoadCount[0] == 0) {
                            Log.v("ABCD", "Nothing found");
                       //     myRef1.removeEventListener(this);
                        }
                    }
                }
            //    myRef1.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //           myownpd.cancel();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                builder1.setMessage("Not able to update data, Check your network connection");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                dialog.dismiss();
                                startActivity(i);
                                finish();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        ///////////////////////////////////////
        final Query myRef2 = MDb.getReference().child(StoreData.schoolcode).child(DD);
      //  timeRef = MDb.getReference("timetest").child("moniter").child(LoginActivity.sname).child("time");
//        myRef2.keepSynced(true);
        if(Present == 0 && Absent == 0 && Out == 0) {
            Log.v("ABCD","HOME");
            myRef2.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(two == 0) {
                        two = 1;
                        final int[] j = {0};
                        for ( j[0] = 0; j[0] < i ; /*DataSnapshot itemSnapshot: dataSnapshot.getChildren() */){
                            final ContactInfo ci = new ContactInfo();
                            ci.email  = r[j[0]];
                            Log.v("ABCDa", ci.email);
                            if(dataSnapshot.hasChild(r[j[0]]))
                            {
                                Map<String ,Object> key = (Map<String, Object>) dataSnapshot.child(r[j[0]]).getValue();
                                ci.name  = r[j[0]];
                                //      Log.v("ABCD", dataSnapshot.getValue().toString());
                                Log.v("ABCD", ci.email);
                                ci.surname = "In-Time : " + (String) key.get("timein");
                                if(key.get("settime") != null)
                                    ci.name = "Today Total time spend in School : " + Integer.parseInt((String) key.get("settime"))/60 +" : "+Integer.parseInt((String) key.get("settime"))%60 + " hr";
                                ci.title = "Out-Time : " +(String) key.get("timeout");


                            } else{
                                ci.title = "Absent";
                            } j[0] = j[0] +1;  result.add(ci);
                            if(j[0] == i-1){
                         //       myRef2.removeEventListener(this);
                            }
                        }
                        myownpd.dismiss();
                        recList.setAdapter(ca);

                    }
                    myRef2.keepSynced(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                    builder1.setMessage("Not able to update data, Check your network connection");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                    dialog.dismiss();
                                    startActivity(i);
                                    finish();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }
        else if(Present == 1){
            Log.v("ABCD","PRESENT");
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(two == 0) {
                        two = 1;
                        final int[] j = {0};
                        for ( j[0] = 0; j[0] < i ; /*DataSnapshot itemSnapshot: dataSnapshot.getChildren() */){
                            final ContactInfo ci = new ContactInfo();
                            ci.email  = r[j[0]];
                            Log.v("ABCDa", ci.email);
                            if(dataSnapshot.hasChild(r[j[0]]))
                            {
                                Map<String ,Object> key = (Map<String, Object>) dataSnapshot.child(r[j[0]]).getValue();

                                ci.name  = r[j[0]];
                                //      Log.v("ABCD", dataSnapshot.getValue().toString());
                                Log.v("ABCD", ci.email);
                                ci.surname = "In-Time : " + (String) key.get("timein");
                                if(key.get("settime") != null)
                                    ci.name = "Today Total time spend in School till now : " + Integer.parseInt((String) key.get("settime"))/60 +" : "+Integer.parseInt((String) key.get("settime"))%60 + " hr";
                                ci.title = "Out-Time : " +(String) key.get("timeout");
                                result.add(ci);
                            } j[0] = j[0] +1;
                        }
                        myownpd.dismiss();
                        recList.setAdapter(ca);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                    builder1.setMessage("Not able to update data, Check your network connection");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                    dialog.dismiss();
                                    startActivity(i);
                                    finish();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }
        else if(Absent == 1 ) {
            Log.v("ABCD","ABSENT");
            myRef2.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(two == 0) {
                        two = 1;
                        final int[] j = {0};
                        for ( j[0] = 0; j[0] < i ; /*DataSnapshot itemSnapshot: dataSnapshot.getChildren() */ ){
                            final ContactInfo ci = new ContactInfo();
                            ci.email  = r[j[0]];
                            Log.v("ABCDa", ci.email);
                            if(!dataSnapshot.hasChild(r[j[0]]))
                            {
                                ci.title = "Absent";
                                result.add(ci);
                            }j[0] = j[0] +1;
                        }
                        myownpd.dismiss();
                        recList.setAdapter(ca);
        //                myRef2.removeEventListener(this);
                    }
       //             myRef2.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                    builder1.setMessage("Not able to update data, Check your network connection");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                    dialog.dismiss();
                                    startActivity(i);
                                    finish();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }
        else if(Out == 1){
            Log.v("ABCD","OUTIDE");
         //   GDATE();
            myRef2.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(two == 0) {
                        two = 1;
                        final int[] j = {0};
                        for ( j[0] = 0; j[0] < i ; /*DataSnapshot itemSnapshot: dataSnapshot.getChildren() */){
                            final ContactInfo ci = new ContactInfo();
                            ci.email  = r[j[0]];
                            Log.v("ABCD#@", String.valueOf(j[0]));
                      //      Log.v("ABCDa", ci.email);
                            if(dataSnapshot.hasChild(r[j[0]]))
                            {
                                Map<String ,Object> key = (Map<String, Object>) dataSnapshot.child(r[j[0]]).getValue();
                                ci.name  = r[j[0]];
                                //      Log.v("ABCD", dataSnapshot.getValue().toString());
                                Log.v("ABCD", ci.email);
                                ci.surname = "In-Time : " + (String) key.get("timein");
                                if(key.get("settime") != null)
                                    ci.name = "Today Total time spend in School : " + Integer.parseInt((String) key.get("settime"))/60 +" : "+Integer.parseInt((String) key.get("settime"))%60 + " hr";
                                ci.title = "Out-Time : " +(String) key.get("timeout");

                                String string2 = (String) key.get("timeout");

                                if(string2 != null) {

                                    String prts[] = tmdate.split(" ");
                                    Log.v("TIME ",String.valueOf(tmdate));
                                    String someRandomTime = prts[3] + " " + prts[4];
                                    String string3 = someRandomTime;

                                    Timemachine tm2 = new Timemachine();
                                    String part2[] = string2.split(" ");
                                    String part21[] = part2[3].split(":");
                                    Log.v("TIME ",part2[3] +" "+ part2[4]);
                                    tm2.hr = Integer.parseInt(part21[0]);
                                    tm2.min = Integer.parseInt(part21[1]);
                                    tm2.clock = part2[4];

                                    Timemachine tm3 = new Timemachine();
                                    String part3[] = string3.split(" ");
                                    String part31[] = part3[0].split(":");
                                    tm3.hr = Integer.parseInt(part31[0]);
                                    tm3.min = Integer.parseInt(part31[1]);
                                    tm3.clock = part3[1];
                                    int min = tm2.min - tm3.min;
                                    int hr = tm2.hr - tm3.hr;
                                    if(min < 0) min = min * -1;
                                    if(hr < 0) hr = hr * -1;
                                    if(hr > 0 || min > 3)
                                        result.add(ci);
                                    Log.v("ABCD## MIN", String.valueOf(min));
                                }
                            } Log.v("ABCD##", String.valueOf(j[0])); j[0] = j[0] +1;
                        }
                        myownpd.dismiss();
                        recList.setAdapter(ca);
         //               myRef2.removeEventListener(this);
                    } //     myRef2.removeEventListener(this);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                    builder1.setMessage("Not able to update data, Check your network connection");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                    dialog.dismiss();
                                    startActivity(i);
                                    finish();

                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        } Log.v("ABCD##", "Return");
        ///////////////////////////////////////
        return result;
    }

    DatabaseReference timeRef;
    private FirebaseDatabase Database;
    public void GDATE() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timetest");
        String key = ref.push().getKey();
        timeRef = FirebaseDatabase.getInstance().getReference("timetest").child(key).child(key).child("time");
        timeRef.setValue(ServerValue.TIMESTAMP);
        timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long time = dataSnapshot.getValue(Long.class);
                    tmdate = postFormater2.format(new Date(time));
                    tmdate = tmdate.replace("-"," ");
                    tmdate = tmdate.replace(".","");
                    String part2[] = tmdate.split(" ");
                    tmdate = part2[0] +" " +part2[1]+" "+part2[2]+" "+part2[3]+" "+part2[4].toUpperCase();
                    Log.v("ABCD 0 ", "onDataChange: " + tmdate);
                    Log.v("ABCD get tdate", "onDataChange: " + tmdate);
                    while(tmdate == null){
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                        }
                    }
                    timeRef.removeEventListener(this);
                } else {
                    Log.v("ABCD", "onDataChange: No data");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Moniter.this);
                builder1.setMessage("Not able to update data, Check your network connection");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent("com.example.sunny.up_education_board.Manage");
                                dialog.dismiss();
                                startActivity(i);
                                finish();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        Log.v("ABCD", ":xx " + tmdate);
    }

}
