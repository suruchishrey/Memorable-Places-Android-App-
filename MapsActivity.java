package com.example.dell.memorableplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LatLng> latLngArrayList;
    LocationManager locationManager;
    LocationListener locationListener;
    String address;
    Double latitude;
    Double longitude;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent newIntent=new Intent();
        newIntent=getIntent();
        latitude=newIntent.getDoubleExtra("latitude",-34);
        longitude=newIntent.getDoubleExtra("longitude",151);
        latLng=new LatLng(latitude,longitude);
        address=mapLocation(latLng);

        putMarkerOnLocation(latLng);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                address=mapLocation(latLng);
                putMarkerOnLocation(latLng);
                Toast.makeText(MapsActivity.this, "This is "+address, Toast.LENGTH_SHORT).show();
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Intent newIntent=new Intent();
                address=mapLocation(latLng);
                newIntent.putExtra("age",18);
                newIntent.putExtra("addAddress",address);
                newIntent.putExtra("addlatitude",latLng.latitude);
                newIntent.putExtra("addlongitude",latLng.longitude);
                setResult(Activity.RESULT_OK,newIntent);
                putMarkerOnLocation(latLng);
                Toast.makeText(MapsActivity.this, "This is "+address, Toast.LENGTH_SHORT).show();
            }
        });

    }

    void putMarkerOnLocation(LatLng latLng)
    {
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        address=mapLocation(latLng);
        markerOptions.title(address);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));
        mMap.addMarker(markerOptions);
    }

    String mapLocation(LatLng latLng)
    {
        String address="";

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addressList = geocoder.getFromLocation((double)latLng.latitude,(double) latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {

                if (addressList.get(0).getLocality() != null) {
                    address += addressList.get(0).getLocality() + ", ";
                }
                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare() + ", ";
                }
                if (addressList.get(0).getSubAdminArea() != null) {
                    address += addressList.get(0).getSubAdminArea() + ", ";
                }
                if (addressList.get(0).getAdminArea() != null) {
                    address += addressList.get(0).getAdminArea() + ", ";
                }
                if (addressList.get(0).getCountryName() != null) {
                    address += addressList.get(0).getCountryName();
                }
                if(address==null)
                {
                    address= Calendar.getInstance().getTime().toString();
                }
                if (address == "") {
                    address = "Cant find Location :(";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }
}
