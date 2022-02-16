package com.example.traficroutier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 123;

    private Marker marker;

    private MapFragment mapFragment;
    private GoogleMap googleMap;

    private TextView textView;


    private static float vitesse;

    Double lattitude , longitudee;

    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FragmentManager fragmentManager = getFragmentManager();

        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);

        textView = findViewById(R.id.value);
/*
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null)
                {
                    Toast.makeText(getApplicationContext()," "+location.getLongitude()+" "+location.getLatitude(),Toast.LENGTH_LONG).show();
                }
            }
        });
        
 */

        OnGPS();



        Intent intent = getIntent();

       lattitude= intent.getDoubleExtra("latitude",0);
         longitudee=intent.getDoubleExtra("longitude",0);

         loadMap();



    }
    //Enable the GPS

    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }



    public void checkPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },REQUEST_LOCATION);

            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,10000,0,this);
        }
        if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,10000,0,this);
        }

        loadMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_LOCATION)
        {
            checkPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //Loading the map
    private void loadMap()
    {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Main2Activity.this.googleMap = googleMap;

                //Allow to US to have zoom buttons (+ , -)
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setCompassEnabled(true);
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setMyLocationButtonEnabled(true);
                //Enable the location
                googleMap.setMyLocationEnabled(true);
                //get a marker in the location of the user
               marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lattitude,longitudee))
                        .title("USER"));


                //show the traffic situation
                googleMap.setTrafficEnabled(true);

            }
        });
    }
    //getting tha last location of the device


    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Double latitude = location.getLatitude();

        Double longitude = location.getLongitude();

        vitesse = location.getSpeed();
        //getting the format of the time and the date
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //getting the format in to string
        String formatdate= sdf.format(new Date(location.getTime()));

        //Geocoder will allow us to get the address

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses.size() > 0)
            {
                //we get the City's name , the twin's name and the Country's name by using addresses.get(0).geAddressLine(0)
                Log.d("address",""+addresses.get(0).getAddressLine(0));

                textView.setText("la vitesse est :"+vitesse+" m/s"+"\n"+"Emplacement :"+addresses.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("time"," "+formatdate);


        if(googleMap != null)
        {
            LatLng googlePosition = new LatLng(latitude,longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(googlePosition));

        }

    }

}
