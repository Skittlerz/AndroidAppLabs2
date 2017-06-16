package com.comp262.braun.lbs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


import android.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//this activity implements OnMapReadyCallback this ensures that OnMapReady() is called
//when the map is ready to be used and provides a non-null instance of GoogleMap
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //this identifies which permission is being asked for
    final private int REQUEST_COURSE_ACCESS = 123;
    //sets whether location updates are allowed
    boolean permissionGranted = false;
    private GoogleMap mMap;
    //location manager receives updates on location changes
    LocationManager lm;
    //location listener handles the location changes from the location manager
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //You cannot instantiate a GoogleMap object directly
        //you must obtain one from the getMapAsync() method
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        //---remove the location listener---
        //it is important to remove the location listener to preserve battery life
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //the access_coarse_location has been declared in the manifest file
            //so this should evaluate to granted
            //result will be sent to the onRequestPermissionsResult method
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COURSE_ACCESS);
            return;
        }else{
            permissionGranted = true;
        }
        if(permissionGranted) {
            lm.removeUpdates(locationListener);
        }
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
        //the locationmanager receives updates on the device's geographical location
        //here it obtains location data
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //this instance of MyLocationListener will be attached to the locationManager
        //it will be notified with location updates by the locationManager
        locationListener = new MyLocationListener();
        //check if ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission are granted
        //if not request permissions
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COURSE_ACCESS);
            return;
        }else{
            permissionGranted = true;
        }
        if(permissionGranted) {
            //this notifies whenever a location has changed
            //requestLocationUpdates takes four parameters:
            //provider—The name of the provider with which you register. In this case, you are using GPS to obtain your geographical location data.
            //minTime—The minimum time interval for notifications, in milliseconds. 0 indicates that you want to be continually informed of location changes.
            //minDistance—The minimum distance interval for notifications, in meters. 0 indicates that you want to be continually informed of location changes.
            //listener—An object whose onLocationChanged() method will be called for each location update
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
        }

        //BELOW IS FROM PREVIOUS LAB - IT HAS BEEN COMMENTED OUT TO INCLUDE THE NEW CODE ABOVE
        // Add a marker in Boston and move the camera
        /**
        LatLng boston = new LatLng(42.3601, -71.0589);
        mMap.addMarker(new MarkerOptions().position(boston).title("Boston, Mass"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(boston));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //the Geocoder class is used to support reverse geocoding
                Geocoder geoCoder = new Geocoder(
                        getBaseContext(), Locale.getDefault());
                try {
                    //converts the latitude and longitude into an address
                    List<Address> addresses = geoCoder.getFromLocation(
                            point.latitude,point.longitude,1);
                    String add = "";
                    if (addresses.size() > 0)
                    {
                        for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();
                             i++)
                            add += addresses.get(0).getAddressLine(i) + "\n";
                    }
                    Toast.makeText(getBaseContext()
                            , add, Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
         **/
    }

    //This method is called when permission is requested in the onMapReady() method
    //if permission is granted then location updates will be given by the location manager
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COURSE_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                } else {
                    permissionGranted = false;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
    // MyLocationListener implements the abstract class LocationListener
    //it needs to implement the four following methods:
    //onLocationChanged(Location location)—Called when the location has changed
    //onProviderDisabled(String provider)—Called when the provider is disabled by the user
    //onProviderEnabled(String provider)—Called when the provider is enabled by the user
    //onStatusChanged(String provider, int status, Bundle extras)—Called when the provider status changes
    private class MyLocationListener implements LocationListener
    {
        //this method displays and navigates to the changed location
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                Toast.makeText(getBaseContext(),
                        "Location changed : Lat: " + loc.getLatitude() +
                                " Lng: " + loc.getLongitude(),
                        Toast.LENGTH_SHORT).show();
                LatLng p = new LatLng(
                        (int) (loc.getLatitude()),
                        (int) (loc.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(7));
            }
        }
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
        }
    }
    //enables zoom out with the 1 key
    //and zoom in with the 3 key
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_3:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case KeyEvent.KEYCODE_1:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
