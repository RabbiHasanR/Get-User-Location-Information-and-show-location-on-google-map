package com.example.android.mapandlocationdemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    final int REQUEST_CODE = 1;

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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                googleMap.clear();
                //Toast.makeText(MapsActivity.this, "Location Data: "+location.toString(), Toast.LENGTH_SHORT).show();
                //Log.i("Location: ",location.toString());
                // Add a marker in user Location and move the camera
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                //get Information about user Location
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> listAddress=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(listAddress!=null && listAddress.size()>0){
                        //Log.i("Location Information: ",listAddress.get(0).toString());
                        String address="";
                        if(listAddress.get(0).getThoroughfare() !=null){
                            address+=listAddress.get(0).getThoroughfare()+" ";
                        }
                        if(listAddress.get(0).getLocality() !=null){
                            address+=listAddress.get(0).getLocality()+" ";
                        }
                        if(listAddress.get(0).getPostalCode() !=null){
                            address+=listAddress.get(0).getPostalCode()+" ";
                        }
                        if(listAddress.get(0).getAdminArea() !=null){
                            address+=listAddress.get(0).getAdminArea();
                        }

                        Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();
                        Log.i("User Location Address: ",address);
                    }
                }catch (Exception e){

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

            }
        };
        //check device api level
        //if (Build.VERSION.SDK_INT < 23) {
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //} else {
        //check permission for access location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            //track last known location
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            googleMap.clear();
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        }
        //}


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }else{
                    Toast.makeText(this, "Permission was denied 1", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Permission was denied 2", Toast.LENGTH_SHORT).show();
        }
    }
}
