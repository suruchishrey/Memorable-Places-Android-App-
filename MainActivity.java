package com.example.dell.memorableplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> placesInfo;
    ArrayList<String> latitudes;
    ArrayList<String> longitudes;
    ArrayList<LatLng> latLngArrayList;
    ListView placesListView;
    ArrayAdapter arrayAdapter;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userLocation;
    LatLng latLng;
    Double latitude;
    Double longitude;
    int flag;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesInfo=new ArrayList<String>();
        latitudes=new ArrayList<String>();
        longitudes=new ArrayList<String>();
        latLngArrayList =new ArrayList<LatLng>();

        placesListView=findViewById(R.id.listView);
        //placesInfo.add("Tap here to see your location");
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,placesInfo);
        //placesListView.setAdapter(arrayAdapter);
        sharedPreferences=this.getSharedPreferences("package com.example.dell.memorableplaces",Context.MODE_PRIVATE);
        placesInfo.clear();
        latitudes.clear();
        longitudes.clear();
        latLngArrayList.clear();

        locationManager= (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please turn your location services ON!", Toast.LENGTH_LONG).show();
            //finishAndRemoveTask();
        }

        try {
            placesInfo=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("placesInfo",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            for(int i=0;i<latitudes.size();++i)
            {
                latitude=Double.parseDouble(latitudes.get(i));
                longitude=Double.parseDouble(longitudes.get(i));
                latLngArrayList.add(new LatLng(latitude,longitude));

            }
            arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,placesInfo);
            placesListView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!!"+e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if(placesInfo.size()==0)
        {
            placesInfo.add("Tap here to see your location");
            arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,placesInfo);
            //arrayAdapter.notifyDataSetChanged();
            placesListView.setAdapter(arrayAdapter);
        }


        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                if (id == 0) {
                    flag = 0;
                    userLocation = GetUserLocation();
                    intent.putExtra("latitude", userLocation.latitude);
                    intent.putExtra("longitude", userLocation.longitude);
                } else {
                    if (position != 0) {
                        flag = 1;
                        intent.putExtra("latitude", (double) latLngArrayList.get(position - 1).latitude);
                        intent.putExtra("longitude", (double) latLngArrayList.get(position - 1).longitude);

                    }
                }
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                String address=data.getStringExtra("addAddress");
                latitude=data.getDoubleExtra("addlatitude",-34);
                longitude=data.getDoubleExtra("addlongitude",151);
                placesInfo.add(address);
                latLngArrayList.add(new LatLng(latitude,longitude));
                latitudes.add(Double.toString(latitude));
                longitudes.add(Double.toString(longitude));
                try {
                    sharedPreferences.edit().putString("placesInfo",ObjectSerializer.serialize(placesInfo)).apply();
                    sharedPreferences.edit().putString("latitudes",ObjectSerializer.serialize(latitudes)).apply();
                    sharedPreferences.edit().putString("longitudes",ObjectSerializer.serialize(longitudes)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                arrayAdapter.notifyDataSetChanged();

                Toast.makeText(this, " Added new location ="+address, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public LatLng GetUserLocation()
    {
        locationManager= (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latLng = new LatLng(location.getLatitude(),location.getLongitude());
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
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION} ,1);
        }
        else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            //Location lastKnownLocation = getLastKnownLocation();
            //Location lastKnownLocation=locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please turn your location services ON!", Toast.LENGTH_LONG).show();
                //finishAndRemoveTask();
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (lastKnownLocation != null) {
                    latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                }
            }
        }
        return latLng;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }



}
