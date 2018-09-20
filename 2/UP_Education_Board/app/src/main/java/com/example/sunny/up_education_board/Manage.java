package com.example.sunny.up_education_board;

import android.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunny.up_education_board.listeners.OnPictureCapturedListener;
import com.example.sunny.up_education_board.services.PictureService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.example.sunny.up_education_board.Camera2BasicFragment;

import static com.example.sunny.up_education_board.Camera2BasicFragment.STATE_WAITING_LOCK;

public class Manage extends Activity implements OnPictureCapturedListener, ActivityCompat.OnRequestPermissionsResultCallback {


    GoogleApiClient mGoogleApiClient;
    private TextView latituteField;
    private TextView longitudeField, dis;
    private android.location.LocationListener locationListener;
    private LocationManager locationManager;
    private static Double results;
    public ProgressDialog pd;
    private Timer _timeTimeToBeep;
    private TimerTask _task;
    private Thread _thread;
    public static String tdate;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    SimpleDateFormat postFormater = new SimpleDateFormat("MMM d yyyy hh:mm:ss a");
    String tarea;
    String tlat;
    String tlong;
    String tname;
    String timein;
    String timeout;
    String locationname;
    String vtimein;
    String vtimeout;
    String timereq;
    String settime = String.valueOf(0);
    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    TextView intime , outtime , currentloc , yourintime , yourouttime , settoday , lowtime;
    Button startbut , stopbut;
    int start = 0;
    private ImageView imageView;
    private Button uploadButton;
    private static final int SELECT_PICTURE = 100;

    // creating an instance of Firebase Storage
    private StorageReference mStorageRef;
    //creating a storage reference. Replace the below URL with your Firebase storage URL.
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(tname + "/rivers.jpg");

        tname = LoginActivity.Username.getText().toString();
        setTitle(tname);
        intime  = (TextView)findViewById(R.id.intime);
        outtime  = (TextView)findViewById(R.id.outtime);
        currentloc  = (TextView)findViewById(R.id.currentloc);
        yourintime = (TextView)findViewById(R.id.yourintime);
        yourouttime = (TextView)findViewById(R.id.yourouttime);
        settoday = (TextView)findViewById(R.id.date);
        lowtime = (TextView)findViewById(R.id.Lowtime);
        startbut =(Button)findViewById(R.id.startclass);
        stopbut = (Button)findViewById(R.id.stopclass);

      //  Intent i = new Intent(this,MyServices.class);
      //  startService(i);
        try {
            String string1 = "2:11:13 AM";
            Date time1 = new SimpleDateFormat("hh:mm:ss a").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = "5:49:00AM";
            Date time2 = new SimpleDateFormat("hh:mm:ss a").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            String someRandomTime = "3:00:00AM";
            Date d = new SimpleDateFormat("hh:mm:ss a").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                Log.v("True","True");
                Toast.makeText(this,x+"true",Toast.LENGTH_LONG).show();
             //   System.out.println(true);
            }
            else
            {
                Log.v("True","FALSE");
                Toast.makeText(this,x+"false",Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //////////////////////
   /*     if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        } */

        ////////////////////
        new dobackground().execute();
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

    }

    //////////////////////////////////////////////////
    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
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
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(() -> {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                if (pictureUrl.contains("0_pic.jpg")) {
            //        uploadBackPhoto.setImageBitmap(scaled);
                } else if (pictureUrl.contains("1_pic.jpg")) {
            //        uploadFrontPhoto.setImageBitmap(scaled);
                }
            });
         //   showToast("Picture saved to " + pictureUrl);
        }
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
    /////////////////////////////////////////////////
    private class dobackground extends AsyncTask<Void, Void, Void> {

        PieGraph pie = (PieGraph) findViewById(R.id.pie_chart);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GetValue(); //tarea , tlat , tlong , tname,timein , timeout , locationname;

            timein = tdate;
            float[] data = {50, 0, 0, 0, 0};
            pie.setData(data);

            pd = new ProgressDialog(Manage.this);

         //   pd = new ProgressDialog(Manage.this, R.style.MyTheme);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            // Set the progress dialog title and message
            pd.setCancelable(false);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading Location......               ");

         //   pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#11111a")));

            pd.setIndeterminate(false);

            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            //////
            ////////////////////////////////
         //   locationManager.requestLocationUpdates("gps", 60000, 0, locationListener);
            return null;
        }


        void in(){
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
            myRef = FirebaseDatabase.getInstance().getReference().child("0532");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String ,Object> key = (Map<String, Object>) dataSnapshot.child("location").getValue();
                    //      tarea , tlat , tlong , tname,timein , timeout , locationname;

                    tarea = (String) key.get("area");
                    tlat = (String) key.get("lat");
                    tlong = (String) key.get("long");
                    key = (Map<String, Object>) dataSnapshot.child("time").getValue();
                    vtimein = (String) key.get("start");
                    vtimeout = (String) key.get("stop");
                    timereq = (String) key.get("required");
                    intime.setText(vtimein);
                    outtime.setText(vtimeout);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.v("XYZ", "Failed to read value.", error.toException());
                }
            });
        }
        public void gettime() {
            Query myRef;
            DatabaseReference mDatabase;
            String parts[] = tdate.split(" ");
            myRef = FirebaseDatabase.getInstance().getReference().child("0532").child(parts[0] + " " + parts[1] + " " + parts[2]);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(tname))
                    {
                        Map<String, Object> key = (Map<String, Object>) dataSnapshot.child(tname).getValue();
                        //      tarea , tlat , tlong , tname,timein , timeout , locationname;

                        settime = (String) key.get("settime");
                    }
                    else {
                        settime = "0";
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void GDATE() {
            DatabaseReference timeRef = FirebaseDatabase.getInstance().getReference("time-test");
            timeRef.setValue(ServerValue.TIMESTAMP);
            timeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long time = dataSnapshot.getValue(Long.class);

                        tdate = postFormater.format(new Date(time));
                        Log.v("ABCD get tdate", "onDataChange: " + tdate);
                    } else {
                        Log.v("ABCD", "onDataChange: No data");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.v("ABCD", ":xx " + tdate);

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
           // latituteField = (TextView) findViewById(R.id.latitude);
         //   longitudeField = (TextView) findViewById(R.id.longtitude);
         //   dis = (TextView) findViewById(R.id.dis);
            // Get the location manager
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Define the criteria how to select the locatioin provider -> use
            // default
            locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
           //         latituteField.setText("\n Lat" + location.getLatitude());
           //         longitudeField.setText("\nLong" + location.getLongitude());

                    Location locat = new Location("");
                    locat.setLatitude(Double.parseDouble(tlat));//(25.43702079);
                    locat.setLongitude(Double.parseDouble(tlong));//(81.77977906);
                    Location loc2 = new Location("");
                    loc2.setLatitude((double) location.getLatitude());
                    loc2.setLongitude((double) location.getLongitude());

     //               Log.v("ABCD", location.getLatitude() + "  // " + location.getLongitude());
                    results = Double.valueOf(loc2.distanceTo(locat));

                    if (results < Double.parseDouble(tarea)) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("0532");
                        String key = ref.push().getKey(); // this will create a new unique key
                        Map<String, Object> value = new HashMap<>();
                        value.put("lat", location.getLatitude());
                        value.put("long", location.getLongitude());
                        gettime();
                        GDATE();
                        int a = Integer.parseInt(settime) + 1;
                        float[] data = {(Float.parseFloat(timereq) * 60)-a, a, 0, 0, 0};
                        lowtime.setText((int)a/60 +" : " +a%60 + " hr : min ");
                        pie.setData(data);
                        value.put("settime", String.valueOf(a));
                       // value.put("timeout", tdate);

                        value.put("timein", timein);

                        value.put("timeout", tdate);
                        String parts[] = tdate.split(" ");
                        ref.child(parts[0] + " " + parts[1] + " " + parts[2]).child(tname).setValue(value);
                        String timeinp[] = timein.split(" ");
                        Log.v("ABCD",timein);
/******** ***/                       String ttimeinp[] = timeinp[3].split(":");
                        yourintime.setText(ttimeinp[0]+":"+ttimeinp[1]+" "+timeinp[4]);

                        String ttparts[] = parts[3].split(":");
                        yourouttime.setText(ttparts[0]+":"+ttparts[1]+" "+parts[4]);

                        String tpart[] = parts[3].split(":");
                        settoday.setText(parts[0] + " " + parts[1] + " " + parts[2] + " , " +tpart[0]+":"+tpart[1]+" "+parts[4]);

                        ///////////////////
                        if(start == 1)
                        {
                            new PictureService().startCapturing(Manage.this , Manage.this);
                      //      Camera2BasicFragment fragment = (Camera2BasicFragment) getFragmentManager().findFragmentById(R.id.container);
                     //       fragment.takePicture();
                        }

                        /////////////////
                    }else{
                        settoday.setText("Not In School");
                    }

                    pd.dismiss();

                    Geocoder geocoder;
                    List<android.location.Address> addresses = null;
                    geocoder = new Geocoder(Manage.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!addresses.isEmpty()) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        //dis.setText(results + " +++ \n" + address);
                        currentloc.setText(address);
                    }
                    //
                }


                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            };

            if (ActivityCompat.checkSelfPermission(Manage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Manage.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                }, 10);
                return ;
            } else {
             //   configureButton();
            }
            locationManager.requestLocationUpdates("gps", 60000, 0, locationListener);

        }
    }
}