package com.example.sunny.up_education_board;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    double lat, lon;
    boolean statusOfGPS;
    LocationManager manager;
    double c_lat = 0.0, c_lon = 0.0;
    Context context;

    TextView txt_lat, txt_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txt_lat = (TextView) findViewById(R.id.fst);
        txt_lng = (TextView) findViewById(R.id.sst);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        buildGoogleApiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        if (!statusOfGPS) {
            displayPromptForEnablingGPS(MapsActivity.this);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            if (!String.valueOf(lat).equals("0.0")) {
                c_lat = mLastLocation.getLatitude();
                c_lon = mLastLocation.getLongitude();
                txt_lat.setText("Current Lattitude is" + String.valueOf(c_lat));
                txt_lng.setText("Current Longitude is" + c_lon);

            /*    LatLng sydney = new LatLng(c_lat, c_lon);
                mMap.setMyLocationEnabled(true);
                mMap.setBuildingsEnabled(true);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
                //     mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            */
                Log.e("Lat and Lng", String.valueOf(c_lat) + c_lon);
            }
        }
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            if (!String.valueOf(lat).equals("0.0")) {
                c_lat = location.getLatitude();
                c_lon = location.getLongitude();
                txt_lat.setText("Current Lattitude is" + String.valueOf(c_lat));
                txt_lng.setText("Current Longitude is" + c_lon);


                LatLng sydney = new LatLng(c_lat, c_lon);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setBuildingsEnabled(true);
             //   mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
                //     mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(c_lat, c_lon), 16.0f));

                Log.e("Lat and Lng", String.valueOf(c_lat)+c_lon);

                Log.e("Lat and Lng", String.valueOf(c_lat)+c_lon);
            }
        }
    }

    public void displayPromptForEnablingGPS(final Activity activity) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Do you want open GPS setting?";
        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
}

