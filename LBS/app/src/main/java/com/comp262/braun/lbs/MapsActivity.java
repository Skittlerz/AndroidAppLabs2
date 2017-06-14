package com.comp262.braun.lbs;

import android.location.Geocoder;
import android.location.Address;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        // Add a marker in Boston and move the camera
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
