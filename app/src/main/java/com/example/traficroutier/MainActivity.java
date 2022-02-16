package com.example.traficroutier;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private static final int REQUEST_LOCATION = 1;
    private RadioButton male,female;

    private String latitude,longitude,speed;

    private Double lat,longi;

    private BDUser database;

    private float vitesse;

    //JSON Nodes names
    private static final String TAG_message = "message" ;

    private LocationManager locationManager;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



      // check();
/*
        if(read(getApplicationContext(),"session",false))
        {
            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(intent);
            finish();
        }

 */




        //Add permissions
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION);
        editText = findViewById(R.id.input);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        button = findViewById(R.id.create_button);
        OnGPS();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginPost();
                ProvideLocation();
               //save(getApplicationContext(),"session",true);

                //database.insertData(Imei());

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //check if the imei value exists inside the database
/*
    public void check()
    {
        Cursor cursor = database.afficher();

        if(cursor.getString(0) == Imei())
        {
            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(intent);
            finish();
        }
    }

 */
/*
    //Void Saving
    public static void save(Context context , String name ,boolean value)
    {

        SharedPreferences.Editor editor =  context.getSharedPreferences("test",Context.MODE_PRIVATE).edit();


        editor.putBoolean(name,true);

        editor.apply();

    }


    //Get value and return boolean
    public static boolean read(Context context, String name, boolean defaultValue)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("test", Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(name, defaultValue);

    }

 */


    public void ProvideLocation()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Check if gps is enable or not

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write function which enable it

            OnGPS();
        }
        else
        {
            //Gps is already On then

            getLocation();
        }
    }

    private void getLocation() {

        //Check permissions again

        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            //Add permissions
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }
        else
        {
            Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(locationGps != null)
            {
                 lat=locationGps.getLatitude();
                 longi = locationGps.getLongitude();

                 vitesse = locationGps.getSpeed();

                latitude = String.valueOf(lat);

                longitude = String.valueOf(longi);

                speed = String.valueOf(vitesse);


            }
            else if(locationNetwork != null)
            {
                 lat=locationNetwork.getLatitude();
                 longi = locationNetwork.getLongitude();
                 vitesse = locationNetwork.getSpeed();


                latitude = String.valueOf(lat);

                longitude = String.valueOf(longi);

                speed = String.valueOf(vitesse);

            }
            else if(locationPassive != null)
            {
                Double lat=locationPassive.getLatitude();
                Double longi=locationPassive.getLongitude();

                vitesse = locationPassive.getSpeed();

                latitude = String.valueOf(lat);

                longitude = String.valueOf(longi);

                speed = String.valueOf(vitesse);

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Can't get your location",Toast.LENGTH_SHORT).show();
            }
        }

    }

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



    public void LoginPost()
    {
        //final String sexe = editText.getText().toString();

        //verifier la validation des inputs
        if(TextUtils.isEmpty(editText.getText().toString()))
        {
            editText.setError("Please enter an correct AGE");
            editText.requestFocus();
        }
        else
        {


            StringRequest stringRequest = new StringRequest(Request.Method.POST,"http://d5a43abf.ngrok.io/Traffic/send.php",
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                        intent.putExtra("latitude", lat);
                        intent.putExtra("longitude", longi);
                        startActivity(intent);
                        finish();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(),"Verify the fields",Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //final String id=Imei();
                    Map<String,String> param = new HashMap<>();
                    param.put("imei",Imei());
                    param.put("sexe",checked());
                    param.put("Ages",editText.getText().toString());
                    param.put("longitude",longitude);
                    param.put("latitude",latitude);
                    param.put("vitesse",speed);


                    return param;
                }
            };
            Volley.newRequestQueue(this).add(stringRequest);
        }

    }


    //Recuperer les radios

    public String checked()
    {
        String result=null;
        if(male.isChecked())
        {
            result=male.getText().toString();
        }
        else if (female.isChecked())
        {
            result=female.getText().toString();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Veuillez choisir le sexe",Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    //Recuperer l'IMEI du telephone de l'utilisateur

    public String Imei()
    {
        String ImeiValue =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        return ImeiValue;
    }

}
