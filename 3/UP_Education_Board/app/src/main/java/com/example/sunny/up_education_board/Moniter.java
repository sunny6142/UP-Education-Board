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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Moniter extends AppCompatActivity {
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

    private AppBarLayout mAppBarLayout;

    SimpleDateFormat postFormater = new SimpleDateFormat("MMM d yyyy");

    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;
    List<ContactInfo> result = new ArrayList<ContactInfo>();
    private ProgressDialog progressDialog ;
    private Context targetCtx ;
    ProgressDialog myownpd;
    String r[] = new String[100000];
    long i;
    public static  String transfername = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moniter);
        FirebaseDatabase.getInstance().goOnline();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      /*  Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                FirebaseDatabase.getInstance().goOnline();
            }
        }, 0, 1800000); */

        // setContentView(R.layout.activity_moniter);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TextView textview = (TextView)findViewById(R.id.goback);

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Moniter.this , School_code.class);
                startActivity(i);
                finish();
            }
        });
        if(DD == null)
        {
         Intent i = new Intent(Moniter.this , LoginActivity.class);
            startActivity(i);
            finish();
        }
        else{
            String parts[] = DD.split(" ");
            DD = parts[0]+" "+parts[1]+" "+parts[2];

            Log.v("DD--",DD);
            Log.v("DD",DD);
            setTitle("Select Date");
        }
        recList = (RecyclerView)findViewById(R.id.cardList);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Force English
        mCompactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                FirebaseDatabase.getInstance().goOnline();
                one = 0;
                two = 0;
                setSubtitle(postFormater.format(dateClicked));
              //  Log.v("ABCD",postFormater.format(dateClicked));

                DD = postFormater.format(dateClicked);
                String parts[] = DD.split(" ");
                DD = parts[0]+" "+parts[1]+" "+parts[2];
                Log.v("DD*",postFormater.format(dateClicked));
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
                    public void onClick(View view, int layoutPosition, boolean b) {
                        // Log.v("done",layoutPosition +""+ result.get(layoutPosition).title);

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
                FirebaseDatabase.getInstance().goOffline();
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
            public void onClick(View view, int layoutPosition, boolean b) {
                // Log.v("done",layoutPosition +""+ result.get(layoutPosition).title);
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
                finish();

            }

            @Override
            public void onClick(View view) {

            }
        });
        //  recList.setAdapter(ca);

        FirebaseDatabase.getInstance().goOffline();
    }

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //     super.onBackPressed();
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ProgressDialog MyProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(Moniter.this);
        progressDialog.setMessage("loading ...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    private List<ContactInfo> createList(int size) {
        Go(result);

        return result ;
    }


    private List<ContactInfo> Go(final List<ContactInfo> result) {

        myRef1 = FirebaseDatabase.getInstance().getReference().child(StoreData.schoolcode).child("TL");

        myRef1.keepSynced(true);
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(one == 0){
                    one  = 1;
                    i  = 0;
                    final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                        myownpd.show();
                        Log.v("ABCDP", itemSnapshot.getValue().toString());
                        value = itemSnapshot.getValue().toString();
                        //    String parts[] = value.split("#");

                        r[(int) i] = value;
                        i++;

                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                        if (pendingLoadCount[0] == 0) {
                            Log.v("ABCD", "Nothing found");
                        }
                    }

                }
                FirebaseDatabase.getInstance().goOffline();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //           myownpd.cancel();
            }
        });

        ///////////////////////////////////////
        final Query myRef2 = FirebaseDatabase.getInstance().getReference().child(StoreData.schoolcode).child(DD);
        myRef2.keepSynced(true);
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
                                ci.name = "Today Total time spend in School : " + Integer.parseInt((String) key.get("settime"))/60 +" : "+Integer.parseInt((String) key.get("settime"))%60 + " hr";
                            ci.title = "Out-Time : " +(String) key.get("timeout");


                            //    Log.v("ABCD", "Nothing found 2"+ci_surname);
                            FirebaseDatabase.getInstance().goOffline();
                        } else{
                            Log.v("ABCD", "Nothing found 2"+ci.email);
                            //       ci.surname = "Absent" ;
                            //     ci.name = "XYZ" ;
                            ci.title = "Absent";
                            FirebaseDatabase.getInstance().goOffline();
                        } j[0] = j[0] +1;  result.add(ci);
                    }
                    myownpd.cancel();
                    recList.setAdapter(ca);
                }
                myRef2.keepSynced(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ///////////////////////////////////////
        return result;
    }

}
