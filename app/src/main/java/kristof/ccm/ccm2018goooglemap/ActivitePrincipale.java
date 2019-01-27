package kristof.ccm.ccm2018goooglemap;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;


import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivitePrincipale extends AppCompatActivity implements LocationListener {

    private static final int ID_DEMANDE_PERMISSION = 123;


    private GoogleMap maGoogleMap;
    private MapFragment monMapFragment;
    private DocumentReference positionCollection;
    private LocationManager lManager;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activite_principale);
        Intent intent = getIntent();
        id = intent.getStringExtra("idUser");

        FragmentManager monFragmentManager = getFragmentManager();

        monMapFragment = (MapFragment) monFragmentManager.findFragmentById(R.id.mamap);

        verifierPermission();

        FirebaseFirestore fireStoreFireBase = FirebaseFirestore.getInstance();
        positionCollection = fireStoreFireBase.collection("localisation").document("location" + id);

        lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        addPosition();
    }

    private void addPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);
        Date date = new Date();

        Map<String, Object> map1 = new HashMap<>();


        map1.put("id_user", id);
        map1.put("latitude", lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
        map1.put("longitude", lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
        map1.put("update_at", date);


        positionCollection.set(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Fred", "result 1 : " + task.isSuccessful());
            }
        });
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


        LocationManager monLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (monLocationManager != null) {
            monLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }

        if (monLocationManager != null) {
            monLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        }

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


    @Override
    public void onLocationChanged(Location location) {

        Location location1 = location;

        addPosition();
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

            }
        });
    }

}
