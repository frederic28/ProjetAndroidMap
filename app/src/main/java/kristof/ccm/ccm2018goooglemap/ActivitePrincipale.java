package kristof.ccm.ccm2018goooglemap;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;


import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


public class ActivitePrincipale extends AppCompatActivity implements LocationListener {

    private static final int ID_DEMANDE_PERMISSION=123;

    private LocationManager monLocationManager;



    private GoogleMap maGoogleMap;
    private MapFragment monMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activite_principale);


         FragmentManager monFragmentManager=getFragmentManager();

        monMapFragment=(MapFragment)monFragmentManager.findFragmentById(R.id.mamap);






        verifierPermission();

    }

    private void verifierPermission(){

        if ( !(
                ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED

        &&

                ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED))

        {

            ActivityCompat.requestPermissions(this,

                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}

                    , ID_DEMANDE_PERMISSION);
            return;
        }


        monLocationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);


        monLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);

        monLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);

        chargerMap();

    }




    @Override
    protected void onResume() {
        super.onResume();
        verifierPermission();
    }


    @Override
    protected void onPause() {
        super.onPause();
        /*
        if(monLocationManager!=null)
            monLocationManager.removeUpdates(this);
            */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == ID_DEMANDE_PERMISSION){


        }
    }


    private double latitude;
    private double longitude;


    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();


        Toast.makeText(this, "latitude="+latitude + " - longitude="+longitude, Toast.LENGTH_LONG).show();


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

    private void chargerMap(){

        monMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                maGoogleMap=googleMap;

                //maGoogleMap.animateCamera(CameraUpdateFactory.);

            }
        });


    }

}
